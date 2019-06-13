package com.asgame.snbs.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.jiyou.general.callback.JYGCallback;
import com.jiyou.general.model.JYPayParam;
import com.jiyou.general.model.JYRoleParam;
import com.jiyou.jydudailib.api.JYProxySDK;
import com.jiyou.jydudailib.api.callback.JYDCallback;
import com.jiyou.jydudailib.api.model.JYDPayParam;
import com.jiyou.jydudailib.api.model.JYDRoleParam;
import com.jiyou.jydudailib.base.IDDLogic;
import com.jiyou.jygeneralimp.api.JYGLogicImp;

public class SDKControl implements IDDLogic {
    private static boolean isDu = true;

    private SDKControl() {
    }

    private volatile static SDKControl SDKControl;
    private IDDLogic iddLogic;

    public static SDKControl getInstance() {
        if (SDKControl == null) {
            synchronized (SDKControl.class) {
                if (SDKControl == null) {
                    SDKControl = new SDKControl();
                }
            }
        }
        return SDKControl;
    }

    @Override
    public void onCreate(Context context, Bundle bundle) {
        if (isDu) {
            JYProxySDK.getInstance().onCreate(context, bundle);
        } else {
            JYGLogicImp.getInstance().onCreate(context, bundle);
        }
    }

    @Override
    public void onStart(Context context) {
        if (isDu) {
            JYProxySDK.getInstance().onStart(context);
        } else {
            JYGLogicImp.getInstance().onStart(context);
        }
    }

    @Override
    public void onRestart(Context context) {
        if (isDu) {
            JYProxySDK.getInstance().onRestart(context);
        } else {
            JYGLogicImp.getInstance().onRestart(context);
        }
    }

    @Override
    public void onResume(Context context) {
        if (isDu) {
            JYProxySDK.getInstance().onResume(context);
        } else {
            JYGLogicImp.getInstance().onResume(context);
        }
    }

    @Override
    public void onPause(Context context) {
        if (isDu) {
            JYProxySDK.getInstance().onPause(context);
        } else {
            JYGLogicImp.getInstance().onPause(context);
        }
    }

    @Override
    public void onStop(Context context) {
        if (isDu) {
            JYProxySDK.getInstance().onStop(context);
        } else {
            JYGLogicImp.getInstance().onStop(context);
        }
    }

    @Override
    public void onDestroy(Context context) {
        if (isDu) {
            JYProxySDK.getInstance().onDestroy(context);
        } else {
            JYGLogicImp.getInstance().onDestroy(context);
        }
    }

    @Override
    public void onNewIntent(Context context, Intent intent) {
        if (isDu) {
            JYProxySDK.getInstance().onNewIntent(context, intent);
        } else {
            JYGLogicImp.getInstance().onNewIntent(context, intent);
        }
    }

    @Override
    public void onSaveInstanceState(Context context, Bundle bundle) {
        if (isDu) {
            JYProxySDK.getInstance().onSaveInstanceState(context, bundle);
        } else {
            JYGLogicImp.getInstance().onSaveInstanceState(context, bundle);
        }
    }

    @Override
    public void onConfigurationChanged(Context context, Configuration configuration) {
        if (isDu) {
            JYProxySDK.getInstance().onConfigurationChanged(context, configuration);
        } else {
            JYGLogicImp.getInstance().onConfigurationChanged(context, configuration);
        }
    }

    @Override
    public void onActivityResult(Context context, int i, int i1, Intent intent) {
        if (isDu) {
            JYProxySDK.getInstance().onActivityResult(context, i, i1, intent);
        } else {
            JYGLogicImp.getInstance().onActivityResult(context, i, i1, intent);
        }
    }

    @Override
    public boolean onBackPressed(Context context) {
        if (isDu) {
            return JYProxySDK.getInstance().onBackPressed(context);
        } else {
            return JYGLogicImp.getInstance().onBackPressed(context);
        }
    }

    @Override
    public void init(Context context, final JYDCallback jydCallback) {
        if (isDu) {
            JYProxySDK.getInstance().init(context, jydCallback);
        } else {
            JYGLogicImp.getInstance().init(context, new JYGCallback() {
                @Override
                public void callback(int i, Object o) {
                    jydCallback.callback(i, o);
                }
            });
        }
    }

    @Override
    public void login(Context context, final JYDCallback jydCallback) {
        if (isDu) {
            JYProxySDK.getInstance().login(context, jydCallback);
        } else {
            JYGLogicImp.getInstance().login(context, new JYGCallback() {
                @Override
                public void callback(int i, Object o) {
                    jydCallback.callback(i, o);
                }
            });
        }
    }

    @Override
    public void logout(Context context, final JYDCallback jydCallback) {
        if (isDu) {
            JYProxySDK.getInstance().logout(context, jydCallback);
        } else {
            JYGLogicImp.getInstance().login(context, new JYGCallback() {
                @Override
                public void callback(int i, Object o) {
                    jydCallback.callback(i, o);
                }
            });
        }
    }

    @Override
    public void pay(Context context, JYDPayParam jydPayParam) {
        if (isDu) {
            JYProxySDK.getInstance().pay(context, jydPayParam);
        } else {
            JYPayParam jyPayParam = new JYPayParam();
            jyPayParam.setUserid(jydPayParam.getUserid());
            jyPayParam.setCpBill(jydPayParam.getCpBill());//cp（游戏方）订单
            jyPayParam.setProductId(jydPayParam.getProductId());//商品标识
            jyPayParam.setProductName(jydPayParam.getProductName());//商品名称
            jyPayParam.setProductDesc(jydPayParam.getProductDesc());//商品说明
            jyPayParam.setServerId(jydPayParam.getServerId());//服务器ID
            jyPayParam.setServerName(jydPayParam.getServerName());//服务器名字
            jyPayParam.setRoleId(jydPayParam.getRoleId());//角色ID
            jyPayParam.setRoleName(jydPayParam.getRoleName());// 角色名字
            jyPayParam.setRoleLevel(jydPayParam.getRoleLevel());//角色等级
            jyPayParam.setPrice(jydPayParam.getPrice() / 100f);// 价格(分)
            jyPayParam.setPayNotifyUrl(jydPayParam.getPayNotifyUrl());
            jyPayParam.setExtension(jydPayParam.getExtension());//会原样返回给游戏
            JYGLogicImp.getInstance().pay(context, jyPayParam);
        }
    }

    @Override
    public void createRole(Context context, JYDRoleParam jydRoleParam) {
        if (isDu) {
            JYProxySDK.getInstance().createRole(context, jydRoleParam);
        } else {
            JYRoleParam roleParam = new JYRoleParam();
            roleParam.setRoleId(jydRoleParam.getRoleId());//角色ID
            roleParam.setRoleName(jydRoleParam.getRoleName());// 角色名字
            roleParam.setRoleLevel(jydRoleParam.getRoleLevel());//角色等级
            roleParam.setServerId(jydRoleParam.getServerId());//服务器ID
            roleParam.setServerName(jydRoleParam.getServerName());//服务器名字
            roleParam.setRoleCreateTime(jydRoleParam.getRoleCreateTime());//获取服务器存储的角色创建时间,时间戳，单位秒，长度10，不可用本地手机时间，同一角色创建时间不可变，上线UC联运必需接入，（字符串类型，sdk 内如会有转换）
            JYGLogicImp.getInstance().createRole(context, roleParam);
        }
    }

    @Override
    public void enterGame(Context context, JYDRoleParam jydRoleParam) {
        if (isDu) {
            JYProxySDK.getInstance().enterGame(context, jydRoleParam);
        } else {
            JYRoleParam roleParam = new JYRoleParam();
            roleParam.setRoleId(jydRoleParam.getRoleId());//角色ID
            roleParam.setRoleName(jydRoleParam.getRoleName());// 角色名字
            roleParam.setRoleLevel(jydRoleParam.getRoleLevel());//角色等级
            roleParam.setServerId(jydRoleParam.getServerId());//服务器ID
            roleParam.setServerName(jydRoleParam.getServerName());//服务器名字
            roleParam.setRoleCreateTime(jydRoleParam.getRoleCreateTime());//获取服务器存储的角色创建时间,时间戳，单位秒，长度10，不可用本地手机时间，同一角色创建时间不可变，上线UC联运必需接入，（字符串类型，sdk 内如会有转换）
            JYGLogicImp.getInstance().enterGame(context, roleParam);
        }
    }

    @Override
    public void roleUpLevel(Context context, JYDRoleParam jydRoleParam) {
        if (isDu) {
            JYProxySDK.getInstance().roleUpLevel(context, jydRoleParam);
        } else {
            JYRoleParam roleParam = new JYRoleParam();
            roleParam.setRoleId(jydRoleParam.getRoleId());//角色ID
            roleParam.setRoleName(jydRoleParam.getRoleName());// 角色名字
            roleParam.setRoleLevel(jydRoleParam.getRoleLevel());//角色等级
            roleParam.setServerId(jydRoleParam.getServerId());//服务器ID
            roleParam.setServerName(jydRoleParam.getServerName());//服务器名字
            roleParam.setRoleCreateTime(jydRoleParam.getRoleCreateTime());//获取服务器存储的角色创建时间,时间戳，单位秒，长度10，不可用本地手机时间，同一角色创建时间不可变，上线UC联运必需接入，（字符串类型，sdk 内如会有转换）
            JYGLogicImp.getInstance().roleUpLevel(context, roleParam);
        }
    }

    @Override
    public void loginAuthNotify(Context context, String s) {
        if (isDu) {
            JYProxySDK.getInstance().loginAuthNotify(context, s);
        } else {

        }
    }

    @Override
    public void getGameUrl(Context context, JYDCallback jydCallback) {
        if (isDu) {
            JYProxySDK.getInstance().getGameUrl(context, jydCallback);
        } else {

        }
    }
}
