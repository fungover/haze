package org.fungover.haze.integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import redis.clients.jedis.JedisPooled;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

public class HazeExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    private GenericContainer<?> haze;
    private JedisPooled pool;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        haze = new GenericContainer<>(new ImageFromDockerfile()
                .withDockerfile(Path.of("./Dockerfile")))
                .withExposedPorts(6379);
        haze.start();
        pool = new JedisPooled(haze.getHost(), haze.getFirstMappedPort());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // do nothing, Testcontainers handles container shutdown
        if (pool != null)
            pool.close();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        // Get the list of test instances (instances of test classes)
        final List<Object> testInstances =
                extensionContext.getRequiredTestInstances().getAllInstances();

        testInstances.forEach((ti) -> {
            for (Field field : ti.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Pool.class)) {
                    try {
                        field.set(ti, pool);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
