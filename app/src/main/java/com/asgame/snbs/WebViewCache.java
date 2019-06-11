package com.asgame.snbs;

import android.content.Context;
import android.text.TextUtils;
import android.util.LruCache;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.asgame.snbs.lru.DiskLruCache;
import com.asgame.snbs.utils.AppVersionUtil;
import com.asgame.snbs.utils.InputStreamUtils;
import com.asgame.snbs.utils.MD5Utils;
import com.asgame.snbs.utils.MimeTypeMapUtils;

public class WebViewCache implements IWebViewCache, IRemoteRequestConfig {

    private static final String TAG = WebViewCache.class.getName();
    private static final String CACHE_DIR_NAME = "FastWebViewCache";
    private DiskLruCache mDiskLruCache;
    private LruCache<String, MemoryResource> mLruCache;
    private Context mContext;
    private IRemoteResourceLoader mResourceLoader;
    private boolean mMemoryCacheEnable = false;
    private IExtensionFilter mDiskExtensionFilter;
    private String mOriginalUrl;
    private CacheVerifier mCacheVerifier;

    public WebViewCache(Context context, DiskCacheConfig cacheConfig) {
        this(context, cacheConfig, false);
    }

    public WebViewCache(Context context, DiskCacheConfig cacheConfig, boolean memoryCacheEnable) {
        mMemoryCacheEnable = memoryCacheEnable;
        mContext = context.getApplicationContext();
        mResourceLoader = generateRemoteResourceLoader();
        mDiskLruCache = generateDiskLruCache(cacheConfig);
        mDiskExtensionFilter = generateExtensionFilter(cacheConfig);
        if (mMemoryCacheEnable) {
            mLruCache = generateLruCache();
        }
    }

    private IExtensionFilter generateExtensionFilter(DiskCacheConfig config) {
        if (config != null && config.getFilter() != null) {
            return config.getFilter();
        }
        IExtensionFilter filter = new ExtensionFilter();
        filter.addExtension("png");
        filter.addExtension("json");
        filter.addExtension("jpg");
        filter.addExtension("js");
        filter.addExtension("mp3");
        filter.addExtension("zip");
        filter.addExtension("jsr");
        filter.addExtension("atlas");
        return filter;
    }

    private DiskLruCache generateDiskLruCache(DiskCacheConfig config) {
        String dir = mContext.getCacheDir() + File.separator + CACHE_DIR_NAME;
        int version = AppVersionUtil.getVersionCode(mContext);
        long cacheSize = 200 * 1024 * 1024;
        int valueCount = 1;
        if (config != null) {
            dir = config.getCacheDir();
            version = config.getVersion();
            cacheSize = config.getCacheSize();
        }
        try {
            return DiskLruCache.open(new File(dir), version, valueCount, cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LruCache<String, MemoryResource> generateLruCache() {
        int cacheSize = 5 * 1024 * 1024;
        return new LruCache<String, MemoryResource>(cacheSize);
    }

    private IRemoteResourceLoader generateRemoteResourceLoader() {
        return new RemoteResourceLoader();
    }

    @Override
    public WebResourceResponse getResource(String url) {
        if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
            return null;
        }
        if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
            return null;
        }
        String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);//获取文件类型
        String mimeType = MimeTypeMapUtils.getMimeTypeFromExtension(extension);
        if (!url.equals(mOriginalUrl)) {
            if (TextUtils.isEmpty(extension)) {
                return null;
            }
//            Log.v("ssssssss", "url: " + url + ", extension: " + extension + ", has been filtered.");
            if (!mDiskExtensionFilter.isFilter(extension)) {
//                Log.v("ssssssssssss", "url: " + url + ", extension: " + extension + ", has been filtered.");
                return null;
            }
        }
        InputStream inputStream = getFromCache(url);
        if (inputStream != null) {
//        	Log.v("缓存ssss", "ssssssssssss");
            return new WebResourceResponse(mimeType, "UTF-8", inputStream);
        }
        RemoteResource resource = mResourceLoader.getResource(url);
        if (resource == null) {
            return null;
        }
        inputStream = InputStreamUtils.copy(resource.getInputStream());
        boolean needCache = mCacheVerifier == null || mCacheVerifier.needCache(resource);
        if (needCache) {
            onCache(url, inputStream);
        }
        try {
            return new WebResourceResponse(mimeType, "UTF-8", inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onCache(String url, InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        cacheToMemory(url, inputStream);
        try {
            inputStream.reset();
            if (mDiskLruCache.isClosed()) {
                return;
            }
            String key = getKey(url);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            OutputStream outputStream = editor.newOutputStream(0);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            while (len != -1) {
                outputStream.write(buffer, 0, len);
                len = inputStream.read(buffer);
            }
            outputStream.flush();
            outputStream.close();
            editor.commit();
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyResource() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mMemoryCacheEnable && mLruCache != null) {
            mLruCache.evictAll();
        }
        mDiskExtensionFilter.clearExtension();
        mDiskExtensionFilter = null;
        mContext = null;
        mResourceLoader = null;
    }

    private InputStream getFromCache(String url) {
        InputStream inputStream = getFromMemoryCache(url);
        if (inputStream != null) {
//            Log.d(TAG, "load from memory cache " + url);
            return inputStream;
        }
        inputStream = getFromDiskCache(url);
        if (inputStream != null) {
//            Log.d(TAG, "load from disk cache " + url);
            cacheToMemory(url, inputStream);
        }
        return inputStream;
    }

    private void cacheToMemory(String url, InputStream inputStream) {
        if (mMemoryCacheEnable) {
            MemoryResource resource = new MemoryResource();
            resource.setInputStream(inputStream);
            mLruCache.put(getKey(url), resource);
//            Log.d(TAG, "put to memory cache " + url);
        }
    }

    private InputStream getFromDiskCache(String url) {
        InputStream inputStream = null;
        try {
            if (mDiskLruCache.isClosed()) {
                return null;
            }
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getKey(url));
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(CacheIndexType.CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private InputStream getFromMemoryCache(String url) {
        if (!mMemoryCacheEnable) {
            return null;
        }
        MemoryResource memoryResource = mLruCache.get(getKey(url));
        if (memoryResource != null) {
            InputStream inputStream = memoryResource.getInputStream();
            if (inputStream != null) {
                try {
                    inputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return inputStream;
        }
        return null;
    }

    private static String getKey(String url) {
        try {
            return MD5Utils.getMD5(URLEncoder.encode(url,"gbk"), false);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void addHeader(String url, Map<String, String> header) {
        if (mResourceLoader == null) {
            return;
        }
        IRemoteRequestConfig config = (IRemoteRequestConfig) mResourceLoader;
        config.addHeader(url, header);
    }

    @Override
    public void setOriginalUrl(String url) {
        mOriginalUrl = url;
        if (mResourceLoader == null) {
            return;
        }
        IRemoteRequestConfig config = (IRemoteRequestConfig) mResourceLoader;
        config.setOriginalUrl(url);
    }

    public interface CacheVerifier {
        boolean needCache(RemoteResource resource);
    }

    public void setCacheVerifier(CacheVerifier cacheVerifier) {
        this.mCacheVerifier = cacheVerifier;
    }
}
