package project.example.com.report_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import project.example.com.report_project.main.Main_JsonParserList;
import project.example.com.report_project.main.Main_SendbyHttp;

// 메인 (로그인) Activity
public class MainActivity extends AppCompatActivity {

    // 변수 선언
    EditText editId;    // 유저 로그인 이메일
    EditText editPw;   // 유저 로그인 패스워드
    LinearLayout btnJoin;   // 메인 로그인 레이아웃
    LinearLayout btnLogin;  // 메인 회원가입 레이아웃
    LinearLayout mainLayout;    // 메인 레이아웃

    // 인텐트로 다음 화면에 값 넘기기 위한 변수
    String userId;

    // 패키지 내 객체 생성
    Main_SendbyHttp MSbHttp = new Main_SendbyHttp();    // 회원 조회를 위해 서버와 통신하는 메서드
    Main_JsonParserList MJParser = new Main_JsonParserList();   // 회원 정보 파싱하는 메서드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 타이틀바 없애기 (항상 super.onCreate() 전에 위치하기!)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메인 LinearLayout 선언
        mainLayout = findViewById(R.id.mainLayout);

        // 변수에 ID 값 저장
        editId = findViewById(R.id.editId);   // 유저 로그인 이메일 넣는 공간
        editPw = findViewById(R.id.editPw);   // 유저 로그인 패스워드 넣는 공간
        btnJoin = findViewById(R.id.btnJoin);     // 로그인 버튼 레이아웃
        btnLogin = findViewById(R.id.btnLogin);       // 회원가입 버튼 레이아웃

        // SharedPreferences 이용하여 휴대폰에 저장된 아이디값이 있다면 로그인화면 없이 바로 ListActivity 로 진입
        if(getPreferences() != "") {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);            startActivity(intent);            finish();
        }

        // 배경화면 클릭 시 소프트키보드 사라지게 하기 위해 만든 ClickListener
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editId.getWindowToken(),0);
                imm.hideSoftInputFromWindow(editPw.getWindowToken(),0);
            }
        });

        // 로그인 버튼 클릭 시 이벤트
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String result = MSbHttp.SendByHttp();
                String[][] parseData = MJParser.jsonParserList(result);  //json 데이터 파싱
                String[] data = new String[parseData.length];
                // 각 상황 구별하기위한 임시 변수 선언
                int flag = 0 ;
                // 각 상황 구별
                for(int i = 0; data.length > i; i++) {
                    if(parseData[i][0].equals(editId.getText().toString())) {
                        if(parseData[i][1].equals(editPw.getText().toString())) {
                            flag = 0;
                            userId = parseData[i][0];   // 로그인 성공 및 변수에 값 대입
                            break;
                        } else {
                            flag = 2;
                            break;
                        }
                    } else {
                        flag = 1;
                    }
                }

                // 아이디 오류, 비밀번호 오류, 로그인 성공 시 토스트
                switch (flag) {
                    case 0 :
                        savePreferences(userId);
                        Toast.makeText(getApplicationContext(),"환영합니다 " + userId + "님!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1 :
                        Toast.makeText(getApplicationContext(),"존재하지 않는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2 :
                        Toast.makeText(getApplicationContext(),"비밀번호가 정확하지 않습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // 회원가입 버튼 클릭 시 이벤트
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    // SharedPreferences 로 저장된 아이디를 불러오기
    private String getPreferences() {
        SharedPreferences SPuserId = getSharedPreferences("userId", MODE_PRIVATE);
        return SPuserId.getString("userId", "");    // "userId" 라는 이름의 값을 불러온다, 없을 시 Default 로 "" 불러온다
    }
    // 회원 아이디를 SharedPreferences 형식으로 앱 내에 저장
    private void savePreferences(String userId) {
        // "userId" 라는 이름으로 파일 저장
        SharedPreferences SPuserId = getSharedPreferences("userId", MODE_PRIVATE);
        SharedPreferences.Editor editor = SPuserId.edit();
        editor.putString("userId", userId).commit();
    }

}
