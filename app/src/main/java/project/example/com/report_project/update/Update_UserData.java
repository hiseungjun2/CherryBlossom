package project.example.com.report_project.update;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Update_UserData {

    // DB에 회원가입 정보 저장
    public void UpdateUserData(String id, String pw, String name, String birth, String prof, String local, String purp, String imgurl) {

        class updateD extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                try {
                    String id = (String)params[0];
                    String pw = (String)params[1];
                    String name = (String)params[2];
                    String birth = (String)params[3];
                    String prof =(String)params[4];
                    String local =(String)params[5];
                    String purp =(String)params[6];
                    String imgurl =(String)params[7];

                    // URL 선언
                    // String strUrl = "http://hiseungjun2.cafe24.com/report_project/JSONUserUpdate.jsp";
                    String strUrl = "http://tmdwns9738.cafe24.com/report_project/JSONUserUpdate.jsp";
//                    String strUrl = "http://192.168.137.100:8080/report_project/JSONUserUpdate.jsp";
                    String data = "id=" + id + "&pw=" + pw + "&name=" + name +
                            "&birth=" + birth + "&prof=" + prof + "&local=" + local + "&purp=" + purp + "&imgurl=" + imgurl;

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

        updateD task = new updateD();
        // 실행
        task.execute(id, pw, name, birth, prof, local, purp, imgurl);
    }
}
