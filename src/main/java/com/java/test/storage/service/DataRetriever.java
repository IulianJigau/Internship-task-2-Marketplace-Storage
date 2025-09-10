package com.java.test.storage.service;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface DataRetriever {
    StreamingResponseBody streamFile(String fileName);

    List<String> getFiles();
}
