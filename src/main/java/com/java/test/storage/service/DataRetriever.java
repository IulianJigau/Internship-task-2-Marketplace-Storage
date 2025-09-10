package com.java.test.storage.service;

import reactor.core.publisher.Flux;

import java.util.List;

public interface DataRetriever {
    Flux<String> streamFile(String fileName);

    List<String> getFiles();
}
