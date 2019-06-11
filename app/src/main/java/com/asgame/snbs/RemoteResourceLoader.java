package com.asgame.snbs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RemoteResourceLoader implements IRemoteResourceLoader, IRemoteRequestConfig {

    private static final String TAG = RemoteResourceLoader.class.getName();
    private Map<String, Map<String, String>> mHeaders;
    private String mOriginalUrl;

    @Override
    public RemoteResource getResource(String url) {
        try {
            URL urlRequest = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
            putHeader(httpURLConnection, url);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(20000);
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                RemoteResource remoteResource = new RemoteResource();
                remoteResource.setInputStream(httpURLConnection.getInputStream());
                if (url.equals(mOriginalUrl)) {
                    remoteResource.setResponseHeaders(httpURLConnection.getHeaderFields());
                }
                return remoteResource;
            }
            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
//            Log.d(TAG, e.toString() + " " + url);
            e.printStackTrace();
        } catch (IOException e) {
//            Log.d(TAG, e.toString() + " " + url);
            e.printStackTrace();
        } catch (Exception e) {
//            Log.d(TAG, e.toString() + " " + url);
            e.printStackTrace();
        }
        return null;
    }

    private void putHeader(HttpURLConnection httpURLConnection, String url) {
        if (mHeaders == null || !mHeaders.containsKey(url)) {
            return;
        }
        Map<String, String> header = mHeaders.get(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addHeader(String url, Map<String, String> header) {
        if (mHeaders == null) {
            mHeaders = new HashMap<String, Map<String, String>>();
        }
        mHeaders.put(url, header);
    }

    @Override
    public void setOriginalUrl(String url) {
        mOriginalUrl = url;
    }
}
