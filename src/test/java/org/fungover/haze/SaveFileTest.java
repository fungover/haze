package org.fungover.haze;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;

class SaveFileTest {

    String userHome;

    @BeforeEach
    void beforeEach() {
        userHome = System.getProperty("user.home");
    }

    @AfterEach
    void afterEach() {
        System.setProperty("user.home", userHome);
    }

    @Test
    @DisplayName("Failing to save file returns -Err")
    void saveToFileFails(@TempDir Path tempDir) {
        Path invalidPath = Path.of(tempDir.toString(), "unknownFolder");
        System.setProperty("user.home", invalidPath.toString());
        var map = Map.of("key", "value");

        assertThat(SaveFile.writeOnFile(map)).isEqualTo("-Error 'message'\r\n");
        assertThat(tempDir).isEmptyDirectory();
    }

    @Test
    @DisplayName("Saving successfully to file returns +OK\\r\\n")
    void saveToFileShouldWork(@TempDir Path tempDir) throws IOException {
        System.setProperty("user.home", tempDir.toString());
        var map = Map.of("key", "value");

        assertThat(SaveFile.writeOnFile(map)).isEqualTo("+OK\r\n");

        var actualFile = findLastModifiedFile(Path.of(tempDir.toString(), "fungover", "haze"));
        assertThat(actualFile).isNotEmpty();

        assertThat(linesOf(actualFile.get()))
                .hasSize(2)
                .contains(
                        "key",
                        "value");
    }

    private Optional<Path> findLastModifiedFile(Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return stream.max(this::compareLastModified);
        }
    }

    private int compareLastModified(Path p1, Path p2) {
        try {
            return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("File already exists returns +OK\\r\\n")
    void fileAlreadyExistsReturnsOK(@TempDir Path tempDir) {
        System.setProperty("user.home", tempDir.toString());
        var map = Map.of("key", "value");

        SaveFile.createFile();

        assertThat(SaveFile.writeOnFile(map)).isEqualTo("+OK\r\n");
    }
}

