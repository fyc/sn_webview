package com.asgame.snbs;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import com.example.android_sn_jiyou_as.R;

public class SplashActivity extends BaseActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(getMainLooper());
        // 设置没有标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        RelativeLayout layoutSplash = (RelativeLayout) findViewById(R.id.relativeLayout_splash);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);//设置动画播放时长1000毫秒（1秒）
        layoutSplash.startAnimation(alphaAnimation);
        //设置动画监听
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                //页面的跳转
//                JYProxySDK.getInstance().getGameUrl(SplashActivity.this, new JYDCallback<String>() {
//                    @Override
//                    public void callback(int i, final String url) {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!TextUtils.isEmpty(url)) {
//                                    MainActivity.startActivity(SplashActivity.this, url);
//                                    finish();
//                                }
//                            }
//                        }, 10);
//                    }
//                });
                MainActivity.startActivity(SplashActivity.this);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}

