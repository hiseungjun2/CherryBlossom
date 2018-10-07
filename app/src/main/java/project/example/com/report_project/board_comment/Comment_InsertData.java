package project.example.com.report_project.board_comment;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Comment_InsertData {

    public void CommentInsertdata(String content, String date, String userid, String username, String boardid) {

        class insertD extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                try {
                    String content = (String)params[0];
                    String date = (String)params[1];
                    String userid = (String)params[2];
                    String username = (String)params[3];
                    String boardid = (String)params[4];

                    // URL 선언
                    // String strUrl = "http://192.168.137.100:8080/report_project/JSONUserInsert.jsp";
                    // String strUrl = "http://hiseungjun2.cafe24.com/report_project/JSONCommentInsert.jsp";
                    String strUrl = "http://tmdwns9738.cafe24.com/report_project/JSONCommentInsert.jsp";
                    String data = "content=" + content + "&date=" + date + "&userid=" + userid +
                            "&username=" + username + "&boardid=" + boardid;

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
        task.execute(content, date, userid, username, boardid);
    }
}
