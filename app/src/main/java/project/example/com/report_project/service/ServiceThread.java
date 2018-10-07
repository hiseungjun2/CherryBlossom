package project.example.com.report_project.service;

import android.os.Handler;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler) {
        this.handler = handler;
    }

    public void stopForever() {
        synchronized (this) {
            this.isRun = false;
        }
    }

    // 서비스 실행
    public void run() {
        while (this.isRun) {
            this.handler.sendEmptyMessage(0);
            try {
                Thread.sleep(5000);     // 5초마다
            } catch (Exception e) {
            }
        }
    }

}
