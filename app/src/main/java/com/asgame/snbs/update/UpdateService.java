package com.asgame.snbs.update;

import java.io.File;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {

    public UpdateService() {

    }

    /** 安卓系统下载类 **/
    DownloadManager manager;

    /** 接收下载完的广播 **/
    DownloadCompleteReceiver receiver;

    /** 初始化下载器 **/
    private void initDownManager(String url) {

        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        receiver = new DownloadCompleteReceiver();

        //设置下载地址
        Uri parse = Uri.parse(url);
        DownloadManager.Request down = new DownloadManager.Request(parse);

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        // 下载时，通知栏显示途中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }

        // 显示下载界面
        down.setVisibleInDownloadsUi(true);

        // 设置下载后文件存放的位置
        String apkName = parse.getLastPathSegment();

        File file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/" + apkName);
        if (file != null && file.exists()) {
            file.delete();
        }

        down.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, apkName);

        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        SharedPreferences sp = getSharedPreferences("downloadApk", MODE_PRIVATE);
        sp.edit().putString("apkName", apkName).commit();

        // 将下载请求放入队列
        long downloadId = manager.enqueue(down);

        sp.edit().putLong("downloadId", downloadId).commit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("UpdateServer", "start");
        // 调用下载

        Bundle bundle = intent.getBundleExtra("download");
        initDownManager(bundle.getString("url"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d("kodulf","id="+downId);

                SharedPreferences sp = context.getSharedPreferences("downloadApk", MODE_PRIVATE);
                long saveApkId = sp.getLong("downloadId", -1L);
                if (downId == saveApkId) {

                    //自动安装apk
                    String apkName = sp.getString("apkName", null);
                    installApkNew(context, apkName);
                }
                //停止服务并关闭广播
                UpdateService.this.stopSelf();
            }
        }

        //安装apk
        protected void installApkNew(Context context, String apkName) {
            File file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/" + apkName);
            if (file != null) {
                Intent install = new Intent("android.intent.action.VIEW");
                Uri downloadFileUri = Uri.fromFile(file);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } else {
                Log.d("File null", apkName);
            }
        }
    }
}