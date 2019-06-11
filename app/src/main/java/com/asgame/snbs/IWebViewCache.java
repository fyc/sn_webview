package com.asgame.snbs;

import android.webkit.WebResourceResponse;

public interface IWebViewCache {

    WebResourceResponse getResource(String url);

    void destroyResource();

}
