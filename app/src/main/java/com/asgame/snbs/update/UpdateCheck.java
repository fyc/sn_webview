package com.asgame.snbs.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

public class UpdateCheck extends AsyncTask<String, Void, Integer> {

	public Context context;
	private String ip;

	public UpdateCheck(Context ctx, int ipId) {
		this.context = ctx;
		this.ip = ctx.getResources().getString(ipId);
		this.execute(this.ip + "/androidUpdate/update.php");
	}

	@Override
	protected Integer doInBackground(String... params) {
		try {
			String url = params[0];
			//首先创建一个客户端实例
			HttpClient mhttpclient = new DefaultHttpClient();
			//设置传递的方法
			HttpGet mhttpget = new HttpGet(url);
			//通过客户端进行发送
			HttpResponse mhttpResponse = mhttpclient.execute(mhttpget);
			//通过HttpResponse获取方法体
			HttpEntity mHttpEntity = mhttpResponse.getEntity();
			//通过流获取具体的内容
			InputStream in = mHttpEntity.getContent();
			//创建缓冲区
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine())!= null){
				sb.append(line);
			}
			return Integer.parseInt(sb.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if(result != null) {
			try {
				check(result);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void check(Integer v) throws Exception {
		//获取packagemanager的实例
		PackageManager packMgr = context.getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packMgr.getPackageInfo(context.getPackageName(), 0);
		//解析json判断版本号
		if(packInfo.versionCode < v) {
			//提示下载
			downloadAlert();
		}
	}

	private void downloadAlert(){
		// 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// 设置提示框的标题
		builder.setTitle("版本升级").
				// 设置要显示的信息
						setMessage("发现新版本！请及时更新").
				// 设置确定按钮
						setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Toast.makeText(MainActivity.this, "选择确定哦", 0).show();
						download();//下载最新的版本程序
					}
				}).

				// 设置取消按钮,null是什么都不做，并关闭对话框
						setNegativeButton("取消", null);

		// 生产对话框
		AlertDialog alertDialog = builder.create();
		// 显示对话框
		alertDialog.show();
	}

	private void download() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				if(canDownloadState()) {
					//启动服务
					Intent service = new Intent(context, UpdateService.class);
					Bundle bundle = new Bundle();
					bundle.putString("url", ip + "/androidUpdate/sn_release.apk");
					service.putExtra("download", bundle);
					context.startService(service);

				} else {

					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(ip + "/androidUpdate/sn_release.apk");
					intent.setData(content_url);
					context.startActivity(intent);

				}
			}
		}).start();
	}

	private boolean canDownloadState() {
		try {
			int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

			if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
					|| state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
