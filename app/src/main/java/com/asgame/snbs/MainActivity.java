package com.asgame.snbs;

import com.asgame.snbs.update.UpdateCheck;
//import com.asgame.snbs.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.example.android_sn_jiyou_as.R;

public class MainActivity extends Activity {

	public WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		initView();

		loadGame();

		new UpdateCheck(this, R.string.ip);
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void initView() {
		webView = (WebView) findViewById(R.id.webView1);

		WebSettings webSettings = webView.getSettings();

		webSettings.setJavaScriptEnabled(true);// 有js的页面都要设为true

		webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片
		webSettings.setDefaultTextEncodingName("utf-8");// 设置编码格式

		String cacheDirPath = getFilesDir().getAbsolutePath() + "/webcache";// 设置缓存路径
		webSettings.setAppCachePath(cacheDirPath);

		webSettings.setAppCacheEnabled(true);// 是否使用缓存
		webSettings.setDomStorageEnabled(true);// DOM Storage

		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 根据cache-control决定是否从网络上取数据。

		WebClient webclient = new WebClient();
		IWebViewCache mWebViewCache = new WebViewCache(webView.getContext(), null);
		webclient.setWebViewCache(mWebViewCache);
		webView.setWebViewClient(webclient);
		webView.setWebChromeClient(new WebChome());
		webView.addJavascriptInterface(new JSSupport(), "snapk");
	}

	public void loadGame() {
//		webView.loadUrl(getResources().getString(R.string.ip) + "/h5mmo/game.php");
		webView.loadUrl(getResources().getString(R.string.ip));
	}

	/**
	 * 返回键切到上个页面
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 据说这样能减少内存泄漏
	 */
	@Override
	protected void onDestroy() {
		webView.setVisibility(View.GONE);
		webView.getSettings().setJavaScriptEnabled(false);
		webView.removeAllViews();
		webView.destroy();
		System.exit(0);
		super.onDestroy();
	}

	/**
	 * 挂到后台先停掉JS
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onResume() {
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.onResume();
		super.onResume();
	}

	@Override
	public void onStop() {
//		webView.getSettings().setJavaScriptEnabled(false);
//		webView.onPause();
		super.onStop();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
