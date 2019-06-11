package com.asgame.snbs;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RemoteResource {

    private InputStream inputStream;

    private Map<String, List<String>> responseHeaders;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
