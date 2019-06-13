package com.asgame.snbs;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import com.asgame.snbs.update.UpdateCheck;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.asgame.snbs.utils.SDKControl;
import com.example.android_sn_jiyou_as.R;
import com.jiyou.jydudailib.api.callback.JYDCallback;

public class MainActivity extends BaseActivity {

    public WebView webView;
    Handler handler;
    private static String mUrl = "";

    public static void startActivity(Activity context) {
        if (context != null) {
//            mUrl = url;
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(getMainLooper());
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();

        loadGame();

        new UpdateCheck(this, R.string.ip);
        SDKControl.getInstance().onCreate(this, savedInstanceState);
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

        WebClient webclient = new WebClient(this);
        IWebViewCache mWebViewCache = new WebViewCache(webView.getContext(), null);
        webclient.setWebViewCache(mWebViewCache);
        webView.setWebViewClient(webclient);
        webView.setWebChromeClient(new WebChome());
//		webView.addJavascriptInterface(new JSSupport(), "snapk");
        webView.addJavascriptInterface(new JYDuWebViewJavaScriptFunction(MainActivity.this, webView), "JYDuSDK");
    }

    public void loadGame() {
//		webView.loadUrl(getResources().getString(R.string.ip) + "/h5mmo/game.php");
//        webView.loadUrl(getResources().getString(R.string.ip));
        SDKControl.getInstance().getGameUrl(MainActivity.this, new JYDCallback<String>() {
            @Override
            public void callback(int i, final String url) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(url)) {
                            webView.loadUrl(url);
//                            webView.loadUrl("http://192.168.0.109:5500/CP_test.html");
                        }
                    }
                }, 10);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                SDKControl.getInstance().onBackPressed(this);
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || webView == null || intent.getData() == null)
            return;
        webView.loadUrl(intent.getData().toString());
        super.onNewIntent(intent);
        SDKControl.getInstance().onNewIntent(this, intent);
    }

    // TODO GAME 在onActivityResult中需要调用YSDKApi.onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKControl.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SDKControl.getInstance().onStart(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onRestart()
    @Override
    protected void onRestart() {
        super.onRestart();
        SDKControl.getInstance().onRestart(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onResume()
    @Override
    protected void onResume() {
        super.onResume();
        SDKControl.getInstance().onResume(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onPause()
    @Override
    protected void onPause() {
        super.onPause();
        SDKControl.getInstance().onPause(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onStop()
    @Override
    protected void onStop() {
        super.onStop();
        SDKControl.getInstance().onStop(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onDestory()

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
        SDKControl.getInstance().onDestroy(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
