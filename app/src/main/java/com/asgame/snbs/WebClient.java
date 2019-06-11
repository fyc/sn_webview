package com.asgame.snbs;

import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebClient extends WebViewClient {
	private IWebViewCache mWebViewCache;

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

}
