package project.example.com.report_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import project.example.com.report_project.board.Board_JsonParser;
import project.example.com.report_project.board.Board_SendbyHttp;
import project.example.com.report_project.board.Board_UserJsonParser;
import project.example.com.report_project.board.Board_UserSendHttp;
import project.example.com.report_project.board_comment.Comment_InsertData;
import project.example.com.report_project.board_comment.Comment_JsonParser;
import project.example.com.report_project.board_comment.Comment_ListAdapter;
import project.example.com.report_project.board_comment.Comment_SendbyHttp;
import project.example.com.report_project.board_comment.Comment_list;

// 게시판 상세 보기 Activity
public class BoardSpecActivity extends AppCompatActivity implements OnMapReadyCallback{

    // 각 변수 선언
    TextView boardSpec_content;
    TextView boardSpec_date;    TextView boardSpec_name;
    TextView boardSpec_birth;    TextView boardSpec_prof;
    TextView boardSpec_local;    TextView boardSpec_purp;
    TextView textMap;
    EditText editComment;
    ImageView userImage;    ImageView btnComment;    ImageView imgMap;
    RelativeLayout board_Viewmap;    LinearLayout mainLayout;

    GoogleMap mMap;
    Geocoder geocoder;
    String userLocation;

    String boardId;      String board_userId;
    String username;    String userId ;
    String title = null;
    String content = null;
    String date = null;
    String map = null;

    String name= null;
    String birth = null;
    String prof = null;
    String local = null;
    String purp = null;
    String imgurl = null;

    // 댓글 세팅하기위한 ListView 선언
    ListView commentView;
    Comment_ListAdapter CLAdapter;
    ArrayList<Comment_list> commentArrayList;

    // 객체 생성
    Board_SendbyHttp BSbHttp = new Board_SendbyHttp();  // 게시판 정보 파싱하기 위한 서버와 통신하는 메서드
    Board_JsonParser BJParser = new Board_JsonParser();     // 게시판 정보 파싱
    Board_UserSendHttp BUSHttp = new Board_UserSendHttp();  // 회원 정보 파싱하기 위한 서버와 통신하는 메서드
    Board_UserJsonParser BUJParser = new Board_UserJsonParser();    // 회원 정보 파싱
    Comment_SendbyHttp CSbHttp = new Comment_SendbyHttp();  // 댓글 정보 파싱하기 위한 서버와 통신하는 메서드
    Comment_JsonParser CJParser = new Comment_JsonParser();     // 댓글 정보 파싱
    Comment_InsertData CIData = new Comment_InsertData();   // 댓글 저장

    Bitmap bmImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_spec);
        setTitle("상세내용");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 변수 저장
        boardSpec_content = findViewById(R.id.boardSpec_content);
        boardSpec_date = findViewById(R.id.boardSpec_date);
        userImage = findViewById(R.id.userImage);
        boardSpec_name = findViewById(R.id.textName);
        boardSpec_birth = findViewById(R.id.textBirth);
        boardSpec_prof = findViewById(R.id.textProf);
        boardSpec_local = findViewById(R.id.textLocal);
        boardSpec_purp = findViewById(R.id.textPurp);
        board_Viewmap = findViewById(R.id.board_Viewmap);
        textMap = findViewById(R.id.textMap);
        imgMap = findViewById(R.id.imgMap);
        editComment = findViewById(R.id.editComment);
        btnComment = findViewById(R.id.btnComment);
        commentView = findViewById(R.id.listComment);
        commentArrayList = new ArrayList<Comment_list>();

        // 인텐트에서 보낸 값 저장
        Intent intent = getIntent();
        boardId = intent.getStringExtra("board_id");
        username = intent.getStringExtra("user_name");
        userId = getPreferences();

        // 메인 LinearLayout 선언
        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editComment.getWindowToken(),0);
            }
        });

        // 게시판 데이터 불러오기 및 저장
        StrictMode.ThreadPolicy Bpolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(Bpolicy);
        String Boardresult = BSbHttp.SendByHttp();  // 서버와 통신
        String[][] BoardparseData = BJParser.BoardjsonParserList(Boardresult);  //json 데이터 파싱
        String[] Bdata = new String[BoardparseData.length];
        for (int i = 0 ; i < Bdata.length ; i++) {
            if(boardId.equals(String.valueOf(BoardparseData[i][0]))) {
                title = BoardparseData[i][1];                date = BoardparseData[i][2];
                board_userId =BoardparseData[i][3];                map = BoardparseData[i][5];
                content = BoardparseData[i][6];
                break;
            } else {
                continue;
            }
        }

        // 게시판 내 회원정보 불러오기
        String Userresult = BUSHttp.UserSendByHttp();   // 서버와 통신
        String[][] UserparseData = BUJParser.UserjsonParserList(Userresult);    //json 데이터 파싱
        String[] Udata = new String[UserparseData.length];
        for (int i = 0 ; i < Udata.length ; i++) {
            if(board_userId.equals(String.valueOf(UserparseData[i][0]))) {
                name = UserparseData[i][2];                birth =UserparseData[i][3];
                prof = UserparseData[i][4];                local = UserparseData[i][5];
                purp = UserparseData[i][6];                 imgurl = UserparseData[i][7];
                break;
            } else {
                continue;
            }
        }

        // 지도 이미지 선택 시 지도화면 표출
        imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (board_Viewmap.getVisibility()==view.VISIBLE) {
                    board_Viewmap.setVisibility(view.GONE);
                    imgMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_black_24dp));
                } else if (board_Viewmap.getVisibility()==view.GONE) {
                    board_Viewmap.setVisibility(view.VISIBLE);
                    imgMap.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_off_black_24dp));
                }
            }
        });

        // 댓글 데이터 리스트에 저장
        String Commentresult = CSbHttp.SendByHttp();    // 서버와 통신
        String[][] CommentparseData = CJParser.CommentjsonParserList(Commentresult);  //json 데이터 파싱
        String[] Cdata = new String[CommentparseData.length];
        for (int i = (Cdata.length-1) ; i>=0 ; i--) {
            if(boardId.equals(CommentparseData[i][5])){
                commentArrayList.add(new Comment_list(CommentparseData[i][3], CommentparseData[i][4],  CommentparseData[i][1], CommentparseData[i][2]));
            }
        }

        // 각 데이터를 레이아웃에 표시
        setTitle(title);
        boardSpec_content.setText(content);        boardSpec_date.setText("작성일 : " + date);
        boardSpec_name.setText(name);        boardSpec_birth.setText(birth);
        boardSpec_local.setText(local);        boardSpec_purp.setText(purp);
        boardSpec_prof.setText(prof);        textMap.setText("주소 : " + map);

        // 작성자 이미지 불러오기
        openImage("http://tmdwns9738.cafe24.com/report_project/img/" + imgurl);
        userImage.setBackground(new ShapeDrawable(new OvalShape()));
        userImage.setClipToOutline(true);

        // 댓글 ListView 생성
        CLAdapter = new Comment_ListAdapter(BoardSpecActivity.this, commentArrayList);
        commentView.setAdapter(CLAdapter);
        // 지도 생성
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapLoc);
        mapFragment.getMapAsync(this);
        // 댓글 작성시
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 작성일 설정
                long now = System.currentTimeMillis();
                Date da = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   // 날짜 표현 포맷 설정
                // 댓글 등록을 위한 변수 선언 및 값 저장
                String date = dateFormat.format(da);
                String comment_content = editComment.getText().toString();
                CIData.CommentInsertdata(comment_content, date, userId, username, boardId);
                Toast.makeText(BoardSpecActivity.this, "댓글작성 하였습니다.", Toast.LENGTH_SHORT).show();
                // 화면 전환
                Intent intent = new Intent(BoardSpecActivity.this, BoardSpecActivity.class);
                intent.putExtra("board_id", boardId);
                intent.putExtra("user_name", username);
                startActivity(intent);
                finish();
            }
        });
    }

    // SharedPreferences 이용하여 저장한 회원 아이디 데이터 불러오기
    private String getPreferences() {
        SharedPreferences SPuserId = getSharedPreferences("userId", MODE_PRIVATE);
        return SPuserId.getString("userId", "");
    }

    // 메뉴 생성
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 뒤로가기
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 지도 생성
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);
        String str = textMap.getText().toString();
        List<Address> addressList = null;
        try {
            // 정상적인 주소일 경우 필요한 데이터로 반환
            addressList = geocoder.getFromLocationName(str,10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.isEmpty()) {
            // 주소가 잘못되어 잘못된 값이 들어간 경우
            Toast.makeText(BoardSpecActivity.this, "잘못된 주소로 인하여 지도 출력이 제한됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println(addressList.get(0).toString());
            // 콤마를 기준으로 split
            String []splitStr = addressList.get(0).toString().split(",");
            userLocation = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
            System.out.println(userLocation);

            String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
            String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
            System.out.println(latitude);
            System.out.println(longitude);

            // 좌표(위도, 경도) 생성
            LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            // 마커 생성
            MarkerOptions mOptions2 = new MarkerOptions();
            mOptions2.title("search result");
            mOptions2.snippet(userLocation);
            mOptions2.position(point);
            mOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_black_18dp));
            // 마커 추가
            mMap.addMarker(mOptions2);
            // 해당 좌표로 화면 줌
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
        }
    }

    // 이미지 불러오기
    public void openImage(String url) {
        class back extends AsyncTask<String, Integer,Bitmap> {
            @Override
            protected Bitmap doInBackground(String... urls) {
                // TODO Auto-generated method stub
                try {

                    URL myFileUrl = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bmImg = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bmImg;
            }

            protected void onPostExecute(Bitmap img) {
                userImage.setImageBitmap(bmImg);
            }
        }

        back task = new back();
        task.execute(url);
    }
}
