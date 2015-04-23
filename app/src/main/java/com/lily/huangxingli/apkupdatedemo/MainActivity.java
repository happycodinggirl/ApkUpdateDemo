package com.lily.huangxingli.apkupdatedemo;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private NotificationManager notificationManager;
    private Resources mResource;
    private View notifyContentView;
    private DownloadManager dm;
    private long enqueue;
    private MyDownloadReceiver downloadReceiver;
    File destFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadReceiver=new MyDownloadReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
        mResource=getResources();
        notifyContentView=View.inflate(getApplicationContext(),R.layout.downloadprogress,null);

        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( hasNewVersion()){
                   showNewVersionDialog();
                   //downNewVersion();
               }

            }
        });
    }

    public boolean hasNewVersion(){
        return true;
    }

    public void downNewVersion() {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
     //   dm.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_ MOBILE|DownloadManager.Request.NETWORK_WIFI);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse("http://file.m.163.com/app/free/201312/11/com.example.buycar_3.apk"));
        request.setTitle(mResource.getString(R.string.notifyMessage));
        request.setDescription(mResource.getString(R.string.notifyMessage));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //禁止发出通知，既后台下载
        //request.setShowRunningNotification(true);
        //不显示下载界面
     //   request.setVisibleInDownloadsUi(false);
        //设置下载后文件存放的位置
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/car");
        if (!file.exists()){
            file.mkdir();

        }
        destFile=new File(file,"car.apk");
        if (!destFile.exists()){

        }
        request.setDestinationUri(Uri.fromFile(destFile));
       // request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "car.apk");
         dm.enqueue(request);


    }
    public void showNewVersionDialog(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(mResource.getString(R.string.dialogtitle));
        builder.setMessage(mResource.getString(R.string.dialogmsg));
     //   builder.create();
        builder.setPositiveButton(mResource.getString(R.string.dialogyesButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // NotificationManager notificationManager=
              //  showNotification();
                downNewVersion();
            }
        });
        builder.setNegativeButton(mResource.getString(R.string.dialognoButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void showNotification(){
        Log.v("TAG","----showNotification----");
        notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder=new Notification.Builder(getApplicationContext())
               // .setContentTitle(mResource.getString(R.string.notifyMessage))
                .setSmallIcon(R.drawable.abc_ic_voice_search_api_mtrl_alpha);
       // builder.setWhen(System.currentTimeMillis());
      //  builder.setProgress(100, 0, true);
        Notification downloadNotify=builder.build();
        downloadNotify.contentView=new RemoteViews(getPackageName(),R.layout.downloadprogress);
        //downloadNotify.contentView.setTextViewText(R.id.textview, mResource.getString(R.string.notifyMessage));
        downloadNotify.contentView.setProgressBar(R.id.progressbar,100,0,true);
        downloadNotify.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0,downloadNotify);



    }
    public class MyDownloadReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Log.v("TAG", "---33s4444ss33-downloadFinished====");
                Intent intent1 = new Intent(Intent.ACTION_VIEW);

                Uri uri=Uri.fromFile(destFile);

                intent1.setDataAndType(Uri.fromFile(destFile),
                        "application/vnd.android.package-archive");
                intent1.addCategory(Intent.CATEGORY_DEFAULT);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
    }
}
