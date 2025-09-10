package com.java.test.storage.controller;

import com.java.test.storage.service.DataRetriever;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "Data Transmitter", description = "Provides data for the core application")
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {

    private final DataRetriever dataRetriever;

    @Operation(summary = "Load from csv")
    @GetMapping(value = "/stream/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody stream(@PathVariable String fileName) {
        return dataRetriever.streamFile(fileName);
    }

    @GetMapping("/files")
    public List<String> getFiles() {
        return dataRetriever.getFiles();
    }
}
