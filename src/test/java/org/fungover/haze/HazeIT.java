package org.fungover.haze;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
class HazeIT {

    @Container
    public GenericContainer haze = new GenericContainer(new ImageFromDockerfile()
            .withDockerfile(Path.of("./Dockerfile")))
            .withExposedPorts(6379);

    JedisPooled pool;

    @BeforeEach
    void createPool() {
        String address = haze.getHost();
        Integer port = haze.getFirstMappedPort();
        pool = new JedisPooled(address, port);
    }

    @AfterEach
    void closePool() {
        pool.close();
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
        pool.del("test");
        pool.del("test1");
        assertThat(pool.setnx("test", "test")).isEqualTo(1);
        assertThat(pool.setnx("test1", "test")).isEqualTo(1);
        //Key test already exists so should not be set
        assertThat(pool.setnx("test", "test1")).isZero();
        pool.del("test");
        pool.del("test1");
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
    void listLPushLPop() {
        assertThat(pool.lpush("left", "first")).isEqualTo(1);
        assertThat(pool.llen("left")).isEqualTo(1);
        assertThat(pool.lpop("left")).isEqualTo("first");
        assertThat(pool.llen("left")).isZero();
        pool.del("left");
        assertThat(pool.exists("left")).isFalse();
    }

    @Test
    void listRPushRPop() {
        assertThat(pool.rpush("right", "first")).isEqualTo(1);
        assertThat(pool.llen("right")).isEqualTo(1);
        assertThat(pool.rpop("right")).isEqualTo("first");
        assertThat(pool.llen("right")).isZero();
        pool.del("right");
        assertThat(pool.exists("right")).isFalse();
    }

    @Test
    void listKeyWithMultipleValues() {
        assertThat(pool.lpush("test", "first")).isEqualTo(1);
        assertThat(pool.lpush("test", "second")).isEqualTo(1);  //This is wrong
        assertThat(pool.llen("test")).isEqualTo(2);
        assertThat(pool.lpush("test", "third", "fourth")).isEqualTo(2);
        assertThat(pool.llen("test")).isEqualTo(4);
        assertThat(pool.rpush("test", "fifth", "sixth")).isEqualTo(2);
        assertThat(pool.llen("test")).isEqualTo(6);

        pool.del("test");
        assertThat(pool.exists("right")).isFalse();
    }

//    @Test
//    void unknownCommand() {
//        try (Socket socket = new Socket("localhost", port)) {
//            // socket.setSoTimeout(3000);
//            socket.getOutputStream().write("helloworld key value\r\n".getBytes(StandardCharsets.UTF_8));
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////            assertThat(bufferedReader.readLine()).isEqualTo("-ERR unknown command 'helloworld', with args beginning with: 'key' 'value' ");
//            assertThat(bufferedReader.readLine()).isEqualTo("-ERR unknown command");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
