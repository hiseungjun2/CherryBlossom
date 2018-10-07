package project.example.com.report_project.notice;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Notice_UserSendHttp {

    // 서버와 통신
    public String UserSendByHttp() {
        String strUrl = "http://tmdwns9738.cafe24.com/report_project/JSONUserSelect.jsp";
        // String strUrl = "http://hiseungjun2.cafe24.com/report_project/JSONUserSelect.jsp";
        // String strUrl = "http://192.168.137.100:8080/report_project/JSONUserSelect.jsp";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get=new HttpGet(strUrl);

        try {
            //데이터 보낸 뒤 서버에서 데이터를 받아오는 과정
            HttpResponse response = client.execute(get);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown();
            return "";
        }
    }
}
