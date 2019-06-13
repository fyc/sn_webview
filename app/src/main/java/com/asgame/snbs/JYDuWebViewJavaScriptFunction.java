package com.asgame.snbs;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.asgame.snbs.model.InitBean;
import com.asgame.snbs.utils.SDKControl;
import com.google.jygson.JsonObject;
import com.jiyou.jydudailib.api.callback.JYDCallback;
import com.jiyou.jydudailib.api.constants.JYDStatusCode;
import com.jiyou.jydudailib.api.model.JYDPayParam;
import com.jiyou.jydudailib.api.model.JYDRoleParam;
import com.jiyou.jydudailib.config.LoadConfig;
import com.jiyou.jydudailib.tools.GsonUtils;
import com.jiyou.jydudailib.tools.ToastUtil;
import com.jiyou.jysdklib.mvp.model.JYSdkLoginBean;

public class JYDuWebViewJavaScriptFunction {
    Context context;
    WebView webView;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    public JYDuWebViewJavaScriptFunction(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @JavascriptInterface
    public void onGetPlatform() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JsonObject Obj = new JsonObject();
                Obj.addProperty("state", 1);
                JsonObject Obj2 = new JsonObject();
                Obj2.addProperty("osType", "android");
                Obj2.addProperty("isNative", true);
                Obj.add("data", Obj2);
                onGetPlatformSuccessed(Obj.toString());
//                onGetPlatformSuccessed("true||android");
            }
        }, 100);

    }

    @JavascriptInterface
    public void onInit(String str) {
        final InitBean bean = GsonUtils.GsonToBean(str, InitBean.class);
        LoadConfig.JY_GAMENAME = bean.getGameName();
        LoadConfig.JY_GAME_VERSION = bean.getGameVersion();
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().init(context, new JYDCallback<String>() {
                    @Override
                    public void callback(int code, String response) {
                        if (code == JYDStatusCode.SUCCESS) {
                            onInitSuccessed("{\"state\":1}");
                        }
                    }
                });
            }
        }, 100);

    }

    //登录:
    @JavascriptInterface
    public void onLogin() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().login(context, new JYDCallback<String>() {
                    @Override
                    public void callback(int code, String response) {
                        if (code == JYDStatusCode.SUCCESS) {
                            JYSdkLoginBean loginBean = GsonUtils.GsonToBean(response, JYSdkLoginBean.class);
                            int state = loginBean.getState();
                            if (state == 1) {
                                JsonObject Obj = new JsonObject();
                                Obj.addProperty("state", 1);
                                JsonObject Obj2 = new JsonObject();
                                Obj2.addProperty("userId", loginBean.getData().getUser_id());
                                Obj2.addProperty("token", loginBean.getData().getToken());
                                Obj2.addProperty("gameId", LoadConfig.JY_GAMEID + "");
                                Obj2.addProperty("channelId", LoadConfig.JY_CHANNEL_ID + "");
                                Obj2.addProperty("cpId", LoadConfig.JY_CP_ID + "");
                                Obj2.addProperty("gameName", LoadConfig.JY_GAMENAME);
                                Obj2.addProperty("gameVersion", LoadConfig.JY_GAME_VERSION);
                                Obj.add("data", Obj2);
                                onLoginSuccessed(Obj.toString());
                            } else {
                                ToastUtil.showLongHideSoftInput(context, loginBean.getMessage());
                            }
                        }
                    }
                });
            }
        }, 100);

    }

    //提交 Player 信息 上传玩家信息
    @JavascriptInterface
    public void onCreateRole(String str) {
        final JYDRoleParam roleParam = GsonUtils.GsonToBean(str, JYDRoleParam.class);
//        JYRoleParam roleParam = new JYRoleParam();
//        roleParam.setRoleId("1");//角色ID
//        roleParam.setRoleName("风雨逍遥");// 角色名字
//        roleParam.setRoleLevel(1);//角色等级
//        roleParam.setServerId("302");//服务器ID
//        roleParam.setServerName("服务器1");//服务器名字
//        roleParam.setRoleCreateTime("1456397360");//获取服务器存储的角色创建时间,时间戳，单位秒，长度10，不可用本地手机时间，同一角色创建时间不可变，上线UC联运必需接入，（字符串类型，sdk 内如会有转换）
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().createRole(context, roleParam);
            }
        }, 100);
    }

    //提交 Player 信息 上传玩家信息
    @JavascriptInterface
    public void onEnterGame(String str) {
        final JYDRoleParam roleParam = GsonUtils.GsonToBean(str, JYDRoleParam.class);
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().enterGame(context, roleParam);
            }
        }, 100);


    }

    //提交 Player 信息 上传玩家信息
    @JavascriptInterface
    public void onRoleUpLevel(String str) {
        final JYDRoleParam roleParam = GsonUtils.GsonToBean(str, JYDRoleParam.class);
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().roleUpLevel(context, roleParam);
            }
        }, 100);
    }

    //支付：
    @JavascriptInterface
    public void onPay(String str) {
        final JYDPayParam payParam = GsonUtils.GsonToBean(str, JYDPayParam.class);
//        jyPayParam.setCpBill(System.currentTimeMillis() + "");
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().pay(context, payParam);
            }
        }, 100);
    }

    @JavascriptInterface
    public void onLogout() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().logout(context, new JYDCallback<String>() {
                    @Override
                    public void callback(int code, String response) {
                        onLogoutSuccessed(response);
                    }
                });
            }
        }, 100);
    }


    @JavascriptInterface
    public void loginAuthNotify(final String str) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SDKControl.getInstance().loginAuthNotify(context, str);
            }
        }, 100);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onGetPlatformSuccessed(final String str) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("onGetPlatformCallback(" + str + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                    }
                });
            }
        }, 100);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onInitSuccessed(final String str) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("onInitCallback(" + str + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                    }
                });
            }
        }, 100);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onLoginSuccessed(final String str) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("onLoginCallback(" + str + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                    }
                });
            }
        }, 100);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void onLogoutSuccessed(final String str) {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("onLogoutCallback(" + str + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
//                        ToastUtil.showLong(s);
                    }
                });
            }
        }, 100);
    }
}
