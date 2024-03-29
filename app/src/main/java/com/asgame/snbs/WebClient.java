package com.asgame.snbs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebClient extends WebViewClient {
    private IWebViewCache mWebViewCache;

    private Activity activity;
    FrameLayout load_bg;

    public WebClient(Activity activity, FrameLayout load_bg) {
        this.activity = activity;
        this.load_bg = load_bg;
    }

    void setWebViewCache(IWebViewCache webViewCache) {
        this.mWebViewCache = webViewCache;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 点击链接在webView里面打开
        view.loadUrl(url);
        return true;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return onIntercept(view, url);
//		return super.shouldInterceptRequest(view, url);
    }

    private WebResourceResponse onIntercept(WebView view, String url) {
        return getResourceFromCache(url);
    }

    private WebResourceResponse getResourceFromCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return mWebViewCache.getResource(url);
    }

//    View v;

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
// TODO Auto-generated method stub
        super.onPageStarted(view, url, favicon);
//        v = View.inflate(activity, R.layout.shennv_load_bg, null);
        load_bg.setVisibility(View.VISIBLE);
//webview加载之前为其添加一个完全覆盖的view过度视图（这里是使用了一个自定义的progressbar）
//        view.addView(v, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
// TODO Auto-generated method stub
        super.onPageFinished(view, url);
        load_bg.setVisibility(View.GONE);
//webView加载之后，移除该视图v
//        view.removeView(v);
    }

}
