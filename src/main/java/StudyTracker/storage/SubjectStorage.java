package studytracker.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import studytracker.model.Subject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SubjectStorage {
    
    private final Path filePath;
    private final ObjectMapper objectMapper;

    public SubjectStorage(Path filePath) {
        this.filePath = filePath;

        this.objectMapper = new ObjectMapper();

        objectMapper.enable(
                SerializationFeature.INDENT_OUTPUT
        );
    }

    public List<Subject> loadAll() throws IOException {

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        return objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<Subject>>() {
                });
    }

    public void saveAll(List<Subject> subjects)
            throws IOException {

        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        objectMapper.writeValue(
                filePath.toFile(),
                subjects);
    }

}
