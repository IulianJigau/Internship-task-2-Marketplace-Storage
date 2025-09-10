package com.java.test.storage.controller;

import com.java.test.storage.service.DataRetriever;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@Tag(name = "Data Transmitter", description = "Provides data for the core application")
@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {

    private final DataRetriever dataRetriever;

    @Operation(summary = "Load from csv")
    @GetMapping(value = "/stream/{fileName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(@PathVariable String fileName) {
        return dataRetriever.streamFile(fileName)
                .map(line -> ServerSentEvent.builder(line)
                        .event("data")
                        .build()
                )
                .onErrorResume(ex -> Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("error")
                                .data(ex.getMessage())
                                .build()
                ));
    }

    @GetMapping("/files")
    public List<String>getFiles(){
        return dataRetriever.getFiles();
    }
}
