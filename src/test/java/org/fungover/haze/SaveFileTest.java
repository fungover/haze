package org.fungover.haze;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;

class SaveFileTest {
    HazeDatabase database;

    @Test
    @DisplayName("saveToFile should save all key value pairs to a file")
    void saveToFileSuccess(@TempDir Path tempDir) throws IOException {
        var map = Map.of("key", "value", "secondKey", "secondValue");
        SaveFile.setDir(tempDir);

        assertThat(SaveFile.writeOnFile(map)).isEqualTo("+OK\r\n");

        var actualFile = findLastModifiedFile(tempDir);
        assertThat(actualFile).isNotEmpty();

        assertThat(linesOf(actualFile.get()))
                .hasSize(4)
                .contains(
                        "key",
                        "value",
                        "secondKey",
                        "secondValue");
    }

    @Test
    @DisplayName("saveToFile with unknown path should return -Err")
    void saveToFileFails(@TempDir Path tempDir) {
        //Mock HazeDatabase, we don't need a real version
        database = Mockito.mock(HazeDatabase.class);
        var map = Map.of("key", "value");
        Path invalidPath = Paths.get(tempDir.toString(), "unknownFolder");
        SaveFile.setDir(invalidPath);

        assertThat(SaveFile.writeOnFile(map)).isEqualTo("-Error 'message'\r\n");
        assertThat(tempDir).isEmptyDirectory();
    }

    Optional<Path> findLastModifiedFile(Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return stream.max(this::compareLastModified);
        }
    }

    int compareLastModified(Path p1, Path p2) {
        try {
            return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
