package com.java.test.storage.service;

import com.java.test.storage.exception.ResourceNotFoundException;
import com.java.test.storage.exception.ResourceValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataRetrieverServiceImpl implements DataRetrieverService {

    private final static int BATCH_SIZE = 4096;
    private final static String FILE_EXTENSION = ".csv";

    @Value("${app.storage-path}")
    private String storagePath;

    @Override
    public List<String> getFiles() {
        Path dir = Paths.get(storagePath);

        try (Stream<Path> paths = Files.list(dir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ResourceNotFoundException("Storage path is invalid: " + storagePath);
        }
    }

    @Override
    public StreamingResponseBody streamFile(String fileName) {
        Path filePath = Paths.get(storagePath, fileName);

        if (!fileName.endsWith(FILE_EXTENSION)) {
            throw new ResourceValidationException("Wrong file format");
        }

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("File not found or unreadable");
        }

        return outputStream -> {
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                byte[] buffer = new byte[BATCH_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };
    }
}
