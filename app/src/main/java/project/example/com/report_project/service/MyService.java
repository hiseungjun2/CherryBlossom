package project.example.com.report_project.service;

import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import project.example.com.report_project.ListActivity;
import project.example.com.report_project.MainActivity;
import project.example.com.report_project.R;
import project.example.com.report_project.board.Board_CountJsonParser;
import project.example.com.report_project.board.Board_CountSendHttp;


public class MyService extends Service {
    Board_CountSendHttp BCHttp = new Board_CountSendHttp();
    Board_CountJsonParser BCParser = new Board_CountJsonParser();
    NotificationManager Notifi_M;
    int afterCount;
    String afterId;
    NotificationCompat.Builder bd = null;
    int nowCount;
    String nowId;
    ServiceThread thread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new ThreadPolicy.Builder().permitAll().build());
        nowCount = Integer.parseInt(BCParser.BoardCountjsonParserList(BCHttp.SendByHttp())[0][0]);
        nowId = getPreferences();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(NotificationCompat.CATEGORY_SERVICE, "onStartCommand 실행");
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            String channelId = "one-channel";
            NotificationChannel channel = new NotificationChannel(channelId, "My Channel One", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Channel One Description");
            Notifi_M.createNotificationChannel(channel);
            bd = new NotificationCompat.Builder(this, channelId);
        } else {
            bd = new NotificationCompat.Builder(this, null);
        }

        thread = new ServiceThread(new myServiceHandler());
        thread.start();
        return START_STICKY;
    }

    public void onDestroy() {
        thread.stopForever();
        thread = null;
        Log.i(NotificationCompat.CATEGORY_SERVICE, "onDestroy 실행");
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
    }

    private String getPreferences() {
        return getSharedPreferences("userId", 0).getString("userId", "");
    }

    class myServiceHandler extends Handler {

        public void handleMessage(Message msg) {
            Log.i(NotificationCompat.CATEGORY_SERVICE, "handleMessage 실행");
            Intent intent = new Intent(MyService.this, ListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("ddd", MyService.this.nowId);
            StrictMode.setThreadPolicy(new ThreadPolicy.Builder().permitAll().build());
            String[][] UserparseData = BCParser.BoardCountjsonParserList(BCHttp.SendByHttp());
            afterCount = Integer.parseInt(UserparseData[0][0]);
            afterId = UserparseData[0][1];
            if (nowCount != afterCount) {
                if (!nowId.equals(afterId)) {
                    bd.setContentTitle("Cherry Blossom")
                            .setContentText("새 게시글이 등록되었습니다.")
                            .setSmallIcon(R.drawable.logo)
                            .setAutoCancel(true).setTicker("새 게시글")
                            .setDefaults(1)
                            .setPriority(1)
                            .setContentIntent(pendingIntent);
                    Notifi_M.notify(777, bd.build());
                }
                nowCount = afterCount;
            }
        }
    }
}
