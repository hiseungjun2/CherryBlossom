package project.example.com.report_project.board;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Board_InsertData {

    public void BoardInsertdata(String title, String date, String id, String name, String map, String content) {

        class insertD extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                try {
                    String title = (String)params[0];
                    String date = (String)params[1];
                    String id = (String)params[2];
                    String name = (String)params[3];
                    String map = (String)params[4];
                    String content =(String)params[5];

                    // URL 선언
                    // String strUrl = "http://192.168.137.100:8080/report_project/JSONUserInsert.jsp";
                    // String strUrl = "http://hiseungjun2.cafe24.com/report_project/JSONBoardInsert.jsp";
                    String strUrl = "http://tmdwns9738.cafe24.com/report_project/JSONBoardInsert.jsp";
                    String data = "title=" + title + "&date=" + date + "&id=" + id + "&name=" + name +
                            "&map=" + map + "&content=" + content;

                    // --------------- db 입력
                    URL url = new URL(strUrl);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);     // 데이터 전송
                    wr.flush();         // 메모리 비우기

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    return sb.toString();

                } catch (Exception e) {}

                return null;
            }
        }

        insertD task = new insertD();
        // 실행
        task.execute(title, date, id, name, map, content);
    }
}
