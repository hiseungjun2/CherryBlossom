package project.example.com.report_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;

import project.example.com.report_project.board.Board_JsonParser;
import project.example.com.report_project.board.Board_ListAdapter;
import project.example.com.report_project.board.Board_SendbyHttp;
import project.example.com.report_project.board.Board_list;
import project.example.com.report_project.chat.Chat_SelectListAdapter;
import project.example.com.report_project.chat.Chat_Selectlist;
import project.example.com.report_project.chat.Chat_UserJsonParser;
import project.example.com.report_project.chat.Chat_UserSendHttp;
import project.example.com.report_project.notice.Notice_JsonParser;
import project.example.com.report_project.notice.Notice_ListAdapter;
import project.example.com.report_project.notice.Notice_SendbyHttp;
import project.example.com.report_project.notice.Notice_UserJsonParser;
import project.example.com.report_project.notice.Notice_UserSendHttp;
import project.example.com.report_project.notice.Notice_list;
import project.example.com.report_project.service.MyService;

// 기본 List Activity
public class ListActivity extends AppCompatActivity {

    // 변수 선언
    String userId;  // 회원 아이디
    String userPw;  // 회원 비밀번호
    String userName;    // 회원 이름
    String userBirth;   // 회원 생일
    String userProf;    // 회원 직업
    String userLocal;   // 회원 거주지역
    String userPurp;    // 회원 목적
    String userImageurl;    // 회원 사진 파일명
    String chatuser_name;   // 채팅 상대방 이름
    String chatuser_id;     // 채팅 상대방 아이디
    // ListView 사용하기 위한 변수 선언
    ListView boardView;    ListView noticeView;     ListView chatSelectView;
    // 이름 검색 시 필요
    EditText chatSelectEdit;    Button chatSelectBtn;
    // 리스트 메인화면 탭 호스트
    TabHost host;
    // 플로팅 액션 버튼 사용하기 위한 변수 선언 (게시판 글 생성)
    FloatingActionButton boardaddBtn;
    // 객체 생성
    Notice_ListAdapter NLAdapter;   // 공지사항 ListAdapter
    Board_ListAdapter BLAdapter;    // 게시판 ListAdapter
    Chat_SelectListAdapter CSLAdapter;
    ArrayList<Notice_list> noticeArrayList; // 공지사항
    ArrayList<Board_list> boardArrayList;   // 게시판
    ArrayList<Chat_Selectlist> chatArrayList;   // 채팅유저 검색
    Notice_UserJsonParser NUJParser = new Notice_UserJsonParser();  // 회원 정보 파싱
    Notice_UserSendHttp NUSHttp = new Notice_UserSendHttp();    // 회원 정보 파싱위한 서버와 통신하는 메서드
    Notice_JsonParser NJParser = new Notice_JsonParser();   // 공지사항 파싱
    Notice_SendbyHttp NSbHttp = new Notice_SendbyHttp();    // 공지사항 파싱위한 서버와 통신하는 메서드
    Board_JsonParser BJParser = new Board_JsonParser();     // 게시판 정보 파싱
    Board_SendbyHttp BSbHttp = new Board_SendbyHttp();  // 게시판 정보 파싱위한 서버와 통신하는 메서드
    Chat_UserJsonParser CUJParser = new Chat_UserJsonParser();  // 채팅 유저 정보 파싱
    Chat_UserSendHttp CUSHttp = new Chat_UserSendHttp();    // 채팅 유저 정보 파싱위한 서버와 통신하는 메서드

    // 메인
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 생성
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);   // 액션바에 로고 사용
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo_2);   // 액션바 로고 생성
        setContentView(R.layout.activity_list);

        // SharedPreferences 에 저장된 아이디 값 불러와서 저장
        userId = getPreferences();

        // 레이아웃 선언
        boardView = findViewById(R.id.boardView);
        noticeView = findViewById(R.id.noticeView);
        chatSelectView = findViewById(R.id.chatSelectView);
        boardaddBtn = findViewById(R.id.boardaddBtn);
        chatSelectBtn = findViewById(R.id.chatSelectBtn);
        chatSelectEdit = findViewById(R.id.chatSelectEdit);

        // 공지사항 및 게시판, 채팅룸 ArrayList 생성
        noticeArrayList = new ArrayList<Notice_list>();        boardArrayList = new ArrayList<Board_list>();        chatArrayList = new ArrayList<Chat_Selectlist>();

        // 받은 회원 아이디로 회원 정보 검색
        StrictMode.ThreadPolicy Upolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(Upolicy);
        String result= NUSHttp.UserSendByHttp();    // 서버와 통신
        String[][] UserparseData = NUJParser.UserjsonParserList(result);  //json 데이터 파싱
        String[] Udata = new String[UserparseData.length];
        for (int i = 0 ; i <Udata.length ; i++) {
            if(UserparseData[i][0].equals(userId)) {  // 데이터베이스의 회원 아이디와 받은 회원 아이디값이 같으면 실행
                userPw = UserparseData[i][1];                userName = UserparseData[i][2];
                userBirth = UserparseData[i][3];             userProf = UserparseData[i][4];
                userLocal = UserparseData[i][5];             userPurp = UserparseData[i][6];
                userImageurl = UserparseData[i][7];
                break;
            } else {    // 아닐 경우 반복문 계속 실행
                continue;
            }
        }

        // 게시판 정보 파싱
        String Boardresult = BSbHttp.SendByHttp();  // 서버와 통신
        String[][] BoardparseData = BJParser.BoardjsonParserList(Boardresult); // json 데이터 파싱
        String[] Bdata = new String[BoardparseData.length];
        for (int i = 0; i < Bdata.length; i++) {
            // ListView 에 게시판 정보 세팅
            boardArrayList.add(new Board_list(BoardparseData[i][0], BoardparseData[i][4], BoardparseData[i][1], BoardparseData[i][2]));
                                                        // 글번호                      작성자이름                      글제목                       작성일
        }

        // 공지사항 정보 파싱
        String Noticeresult = NSbHttp.SendByHttp(); // 서버와 통신
        String[][] NoticeparseData = NJParser.NoticejsonParserList(Noticeresult);  //json 데이터 파싱
        String[] Ndata = new String[NoticeparseData.length];
        for (int i = 0 ; i<Ndata.length ; i++) {
            // ListView 에 공지사항 정보 세팅
            noticeArrayList.add(new Notice_list(NoticeparseData[i][0], NoticeparseData[i][1], NoticeparseData[i][3]));
        }                                               // 글번호                    글제목                    작성일

        // 채팅유저 정보 세팅
        String Chatresult = CUSHttp.UserSendByHttp();    // 서버와 통신
        final String[][] ChatparseData = CUJParser.UserjsonParserList(Chatresult);  //json 데이터 파싱
        final String[] Cdata = new String[ChatparseData.length];
        for (int i = 0 ; i <Cdata.length ; i++) {
            if(userId.equals(UserparseData[i][0])) {  continue; }
            chatArrayList.add(new Chat_Selectlist(ChatparseData[i][0], ChatparseData[i][1], ChatparseData[i][2]));
        }

        // ListActivity 의 화면 설정 ( TabHost)
        host = findViewById(R.id.host);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("tab1");
        spec.setIndicator("홈");
        spec.setContent(R.id.tab_content1);
        host.addTab(spec);

        spec = host.newTabSpec("tab2");
        spec.setIndicator("게시판");
        spec.setContent(R.id.tab_content2);
        host.addTab(spec);

        spec = host.newTabSpec("tab3");
        spec.setIndicator("대화하기");
        spec.setContent(R.id.tab_content3);
        host.addTab(spec);

        // 탭바 클릭시 액션바의 제목 변경하기위한 TabChangeListener
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(host.getCurrentTabTag().equals("tab1")) {
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setLogo(R.drawable.logo_2);   // 로고로 변경
                } else if(host.getCurrentTabTag().equals("tab2")) {
                    actionBar.setDisplayUseLogoEnabled(false);
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.setTitle("게시판");  // 게시판 으로 변경
                }   else if(host.getCurrentTabTag().equals("tab3")) {
                    actionBar.setDisplayUseLogoEnabled(false);
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.setTitle("대화하기");  // 게시판 으로 변경
                }
            }
        });

        // 게시판 글 작성시, 보낸 인텐트 값이 true 일 경우 게시판 탭을 선택한 것으로
        Intent intent = getIntent();
        if (intent.getBooleanExtra("resBoard", false)) {
            host.setCurrentTabByTag("tab2");
        }

        // 공지사항 ListView 생성
        NLAdapter = new Notice_ListAdapter(ListActivity.this, noticeArrayList);
        noticeView.setAdapter(NLAdapter);

        // 공지사항 글 선택 시
        noticeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoticeSpecActivity.class);
                intent.putExtra("notice_id", String.valueOf(noticeArrayList.get(position).getNotice_id()));     // 글 번호 값 보냄
                startActivity(intent);
            }
        });

        // 게시판 ListView 생성
        BLAdapter =new Board_ListAdapter(ListActivity.this, boardArrayList);
        boardView.setAdapter(BLAdapter);

        // 게시판 글 선택 시
        boardView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BoardSpecActivity.class);
                intent.putExtra("board_id", String.valueOf(boardArrayList.get(position).getBoard_id()));    // 글 번호 값 보냄
                intent.putExtra("user_name", userName);
                startActivity(intent);
            }
        });

        // 게시판 글 작성 버튼 클릭 시 (FloatingActionButton)
        boardaddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), BoardAddActivity.class);
                startActivity(intent1);
            }
        });

        //  채팅유저 ListView 생성
        CSLAdapter  = new Chat_SelectListAdapter(this, chatArrayList);
        chatSelectView.setAdapter(CSLAdapter);
        CSLAdapter.notifyDataSetChanged();

        // 검색 버튼 클릭 시 입력된 이름만 표시
        chatSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatArrayList.clear();
                String name = chatSelectEdit.getText().toString();
                for (int i = 0 ; i <Cdata.length ; i++) {
                    if(name.equals((ChatparseData[i][1]))) {
                        chatArrayList.add(new Chat_Selectlist(ChatparseData[i][0], ChatparseData[i][1], ChatparseData[i][2]));
                    }
                    continue;
                }
                CSLAdapter  = new Chat_SelectListAdapter(ListActivity.this, chatArrayList);
                chatSelectView.setAdapter(CSLAdapter);
            }
        });

        // 리스트뷰의 이름 클릭 시 채팅 방으로 이동
        chatSelectView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chatuser_name = chatArrayList.get(i).getUser_name();
                chatuser_id = chatArrayList.get(i).getUser_id();
                Intent intent = new Intent(getApplicationContext(), ChatViewActivity.class);
                intent.putExtra("chatuser_name", chatuser_name);
                intent.putExtra("user_id",userId);
                intent.putExtra("user_name",userName);
                intent.putExtra("chatuser_id", chatuser_id);
                intent.putExtra("chatuser_imgurl", userImageurl);
                startActivity(intent);
            }
        });

        // 서비스 실행
        Intent intent2 = new Intent(ListActivity.this, MyService.class);
        startService(intent2);

    }

    // SharedPreferences 로 저장된 아이디를 불러오기
    private String getPreferences() {
        SharedPreferences SPuserId = getSharedPreferences("userId", MODE_PRIVATE);
        return SPuserId.getString("userId", "");
    }

    // 옵션 메뉴 만들기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    //option 버튼 안의 메뉴를 클릭하였을때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 회원정보수정 버튼 클릭 시
            case R.id.itemUpdate:
                Intent intent = new Intent(ListActivity.this, UserupdateActivity.class);
                intent.putExtra("userId", userId); intent.putExtra("userPw", userPw);
                intent.putExtra("userName", userName); intent.putExtra("userBirth", userBirth);
                intent.putExtra("userProf", userProf); intent.putExtra("userLocal", userLocal);
                intent.putExtra("userPurp", userPurp); intent.putExtra("userImageurl", userImageurl);
                startActivity(intent);
                finish();
                return true;
            // 로그아웃 버튼 클릭 시
            case R.id.itemLogout:
                Toast.makeText(this, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
                // SharedPreferences 에 저장된 회원 아이디 삭제
                SharedPreferences SPuserId = getSharedPreferences("userId", MODE_PRIVATE);
                SharedPreferences.Editor editor = SPuserId.edit();
                editor.remove("userId").commit();
                Intent intent1 = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
