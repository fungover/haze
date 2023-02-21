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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class HazeIT {

    static Process pro;
    static JedisPooled pool;
    static int port;

    @BeforeAll
    static void startServer() throws IOException {
        var pathSep = System.getProperty("path.separator");
        port = findFreePort();
        System.out.println(port);
        String[] command = {"java", "--enable-preview", "-cp", "target/classes" + pathSep + "target/dependency/*", "org.fungover.haze.Main", "--port", String.valueOf(port)};
        pro = Runtime.getRuntime().exec(command);

        await().atMost(10, SECONDS).until(serverIsUp());

        pool = new JedisPooled("localhost", port);
    }

    private static Callable<Boolean> serverIsUp() {
        return () -> {
            try (Socket socket = new Socket("localhost", port)) {
                return true;
            } catch (IOException e) {
                return false;
            }
        }; // The condition that must be fulfilled
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
        //    result = pool.sendCommand(Protocol.Command.PING, "HELLO\r\n There");
        //    assertThat(SafeEncoder.encode((byte[]) result)).isEqualTo("HELLO\r\n There");
    }

    @Test
    void setNx() {
        assertThat(pool.setnx("test", "test")).isEqualTo(1);
        assertThat(pool.setnx("test1", "test")).isEqualTo(1);
        //Key test already exists so should not be set
        assertThat(pool.setnx("test", "test1")).isZero();
    }

    @Test
    void setGet() {
        assertThat(pool.set("test", "test")).isEqualTo("OK");
        assertThat(pool.get("test")).isEqualTo("test");
        pool.del("test");
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
//            assertThat(bufferedReader.readLine()).isEqualTo("-ERR unknown command 'helloworld', with args beginning with: 'key' 'value' ");
            assertThat(bufferedReader.readLine()).isEqualTo("-ERR unknown command");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listMethods() {
        assertThat(pool.lpush("mylist", "third")).isEqualTo(1);
        assertThat(pool.llen("mylist")).isEqualTo(1);
        assertThat(pool.lpush("mylist", "second","first")).isEqualTo(3);
        assertThat(pool.llen("mylist")).isEqualTo(3);
        assertThat(pool.lpop("mylist")).isEqualTo("first");
        assertThat(pool.llen("mylist")).isEqualTo(2);
        assertThat(pool.rpush("mylist", "last")).isEqualTo(3);
        assertThat(pool.rpop("mylist")).isEqualTo("last");
        assertThat(pool.lset("mylist",0, "test")).isEqualTo("OK");
        assertThat(pool.del("mylist")).isEqualTo(1);
    }

    private static int findFreePort() {
        int port = 0;
        try (ServerSocket socket = new ServerSocket()) {
            // Allow direct reuse of port after closing the socket.
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress("localhost", 0));
            port = socket.getLocalPort();
        } catch (IOException ignored) {
        }
        if (port > 0) {
            return port;
        }
        throw new RuntimeException("Could not find a free port");
    }
}
