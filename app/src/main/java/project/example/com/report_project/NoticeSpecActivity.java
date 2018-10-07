package project.example.com.report_project;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import project.example.com.report_project.notice.Notice_JsonParser;
import project.example.com.report_project.notice.Notice_SendbyHttp;
import project.example.com.report_project.notice.Notice_list;

public class NoticeSpecActivity extends AppCompatActivity {

    // 레이아웃 변수 선언
    TextView noticeSpec_title;
    TextView noticeSpec_content;
    TextView noticeSpec_date;

    // 공지사항 번호, 제목 등 변수
    String noticeId ;
    String noticeTitle;
    String noticeContent;
    String noticeDate;

    // 객체 생성
    Notice_SendbyHttp NSbHttp = new Notice_SendbyHttp();
    Notice_JsonParser NJParser = new Notice_JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_spec);
        setTitle("공지사항");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 변수 저장
        noticeSpec_title = findViewById(R.id.noticeSpec_title);
        noticeSpec_content = findViewById(R.id.noticeSpec_content);
        noticeSpec_date = findViewById(R.id.noticeSpec_date);

        // ListActivity에서 보낸 값 저장
        Intent intent = getIntent();
        noticeId = intent.getStringExtra("notice_id");

        // 공지사항 데이터 불러오기 및 저장
        StrictMode.ThreadPolicy Npolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(Npolicy);
        String Noticeresult = NSbHttp.SendByHttp();
        String[][] NoticeparseData = NJParser.NoticejsonParserList(Noticeresult);  //json 데이터 파싱
        String[] Ndata = new String[NoticeparseData.length];
        for (int i = 0 ; i < Ndata.length ; i++) {
            if(noticeId.equals(String.valueOf(NoticeparseData[i][0]))) {
                noticeTitle = NoticeparseData[i][1];
                noticeContent = NoticeparseData[i][2];
                noticeDate =NoticeparseData[i][3];
                break;
            } else {
                continue;
            }
        }

        // 각 데이터를 레이아웃에 표시
        noticeSpec_title.setText(noticeTitle);
        noticeSpec_content.setText(noticeContent);
        noticeSpec_date.setText(noticeDate);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
