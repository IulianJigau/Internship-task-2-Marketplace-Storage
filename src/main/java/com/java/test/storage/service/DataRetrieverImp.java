package com.java.test.storage.service;

import com.java.test.storage.exception.ResourceNotFoundException;
import com.java.test.storage.exception.ResourceValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataRetrieverImp implements DataRetriever {

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
    public Flux<String> streamFile(String fileName) {
        Path filePath = Paths.get(storagePath, fileName);

        if (!fileName.endsWith(".csv")) {
            return Flux.error(new ResourceValidationException("Wrong file format"));
        }

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return Flux.error(new ResourceNotFoundException("File not found or unreadable"));
        }

        return Flux.using(
                () -> Files.newBufferedReader(filePath, StandardCharsets.UTF_8),
                reader -> Flux.generate((reactor.core.publisher.SynchronousSink<String> sink) -> {
                    try {
                        String line = reader.readLine();
                        if (line == null)
                            sink.complete();
                        else
                            sink.next(line);
                    } catch (IOException e) {
                        sink.error(e);
                    }
                }),
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
