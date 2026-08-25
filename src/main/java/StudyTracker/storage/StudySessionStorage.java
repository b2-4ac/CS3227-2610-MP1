package studytracker.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import studytracker.model.StudySession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StudySessionStorage {

    private final Path filePath;
    private final ObjectMapper objectMapper;

    public StudySessionStorage(Path filePath) {

        this.filePath = filePath;

        this.objectMapper = new ObjectMapper();

        // Support LocalDateTime and Duration
        objectMapper.registerModule(new JavaTimeModule());

        // Make JSON easier for humans to read
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<StudySession> loadAll() throws IOException {

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        return objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<StudySession>>() {
                });
    }

    public void save(StudySession session) throws IOException {

        List<StudySession> sessions = loadAll();

        sessions.add(session);

        Files.createDirectories(filePath.getParent());

        objectMapper.writeValue(
                filePath.toFile(),
                sessions);
    }
}
