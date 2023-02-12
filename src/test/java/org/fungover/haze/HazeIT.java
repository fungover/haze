package org.fungover.haze;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class HazeIT {

    static Process pro;
    static JedisPooled pool;
    static int port;

    @BeforeAll
    static void startServer() throws IOException {
        port = findFreePort();
        System.out.println(port);
        String[] command = {"java", "--enable-preview", "-cp", "target/classes", "org.fungover.haze.Main", "--port", String.valueOf(port)};
        pro = Runtime.getRuntime().exec(command);
        pool = new JedisPooled("localhost", port);
    }

    @AfterAll
    static void stopServer() throws InterruptedException {
        pool.close();
        pro.destroy();
        pro.waitFor();
        System.out.println("exitValue() " + pro.exitValue());
    }

    @Test
    void pingPong() {
        //Simple PING command with no message should return PONG as simple string
        var result = pool.sendCommand(Protocol.Command.PING);
        assertThat(SafeEncoder.encode((byte[]) result)).isEqualTo("PONG");
        //PING with message argument should return bulk string with the argument
        result = pool.sendCommand(Protocol.Command.PING, "HELLO");
        assertThat(SafeEncoder.encode((byte[]) result)).isEqualTo("HELLO");
        //PING with message argument containing space should return bulk string with the argument
        result = pool.sendCommand(Protocol.Command.PING, "HELLO\r\n There");
        assertThat(SafeEncoder.encode((byte[]) result)).isEqualTo("HELLO\r\n There");
    }

    @Test
    void setNx() {
        //Remove keys before trying this
        pool.del("test", "test1");
        assertThat(pool.setnx("test", "test")).isEqualTo(1);
        assertThat(pool.setnx("test1", "test")).isEqualTo(1);
        //Key test already exists so should not be set
        assertThat(pool.setnx("test", "test1")).isEqualTo(0);
    }

    @Test
    void setGet() {
        assertThat(pool.set("test", "test")).isEqualTo("OK");
        assertThat(pool.get("test")).isEqualTo("test");
    }

    @Test
    void exists() {
        assertThat(pool.set("test", "test")).isEqualTo("OK");
        assertThat(pool.exists("test")).isTrue();
        pool.del("notused");
        assertThat(pool.exists("notused")).isFalse();
    }

    @Test
    void unknownCommand() {
        try (Socket socket = new Socket("localhost", port)) {
            socket.setSoTimeout(3000);
            socket.getOutputStream().write("helloworld key value\r\n".getBytes(StandardCharsets.UTF_8));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            assertThat(bufferedReader.readLine()).isEqualTo("-ERR unknown command 'helloworld', with args beginning with: 'key' 'value' ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int findFreePort() {
        int port = 0;
        try (ServerSocket socket = new ServerSocket(0)) {
            // Disable timeout and reuse address after closing the socket.
            socket.setReuseAddress(true);
            port = socket.getLocalPort();
        } catch (IOException ignored) {
        }
        if (port > 0) {
            return port;
        }
        throw new RuntimeException("Could not find a free port");
    }
}
