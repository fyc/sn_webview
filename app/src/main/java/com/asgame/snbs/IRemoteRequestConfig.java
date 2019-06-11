package com.asgame.snbs;

import java.util.Map;

public interface IRemoteRequestConfig {

    void addHeader(String url, Map<String, String> header);

    void setOriginalUrl(String url);

}
