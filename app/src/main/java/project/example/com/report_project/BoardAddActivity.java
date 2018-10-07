package project.example.com.report_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import java.util.Date;

import project.example.com.report_project.board.Board_InsertData;
import project.example.com.report_project.board.Board_UserJsonParser;
import project.example.com.report_project.board.Board_UserSendHttp;

// 게시판 글 작성 Activity
public class BoardAddActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 변수 선언
    TextView textName;    TextView textBirth;
    TextView textProf;    TextView textLocal;
    TextView textPurp;      ImageView userImage;
    EditText editTitle;    EditText editAppeal;
    LinearLayout mainLayout;
    // 회원 정보 선언
    String userId;    String userName;
    String userBirth;    String userProf;
    String userLocal;    String userPurp;
    String userImageurl;
    // 지도 관련
    GoogleMap mMap;
    Geocoder geocoder;
    double lat;  // 위도
    double lon;  // 경도
    String userLocation; // 위도,경도로 구한 주소
    // 객체 선언
    Board_UserJsonParser BUJParser = new Board_UserJsonParser();    // 회원 정보 파싱
    Board_UserSendHttp BUSHttp = new Board_UserSendHttp();  // 회원 정보 파싱하기 위한 서버와 통신하는 메서드
    Board_InsertData BIData = new Board_InsertData();   // 게시판 추가 메서드
    // 회원의 현재 지역 변수 선언
    LocationManager locationManager;
    LocationListener locationListener;
    // 지도 자동완성 추가
    PlaceAutocompleteFragment autocompleteFragment;
    GoogleApiClient mGoogleClient;
    String locat;
    // 이미지 업로드 위한 Bitmap
    Bitmap bmImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_add);
        // 액션바 등록 및 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("게시판 작성하기");
        // SharedPreferences 에 저장된 아이디 값 불러와서 저장
        userId = getPreferences();
        // 변수 설정
        textName = findViewById(R.id.textName);        textBirth = findViewById(R.id.textBirth);
        textProf = findViewById(R.id.textProf);        textLocal = findViewById(R.id.textLocal);
        textPurp = findViewById(R.id.textPurp);      userImage = findViewById(R.id.userImage);
        editTitle = findViewById(R.id.editTitle);        editAppeal = findViewById(R.id.editAppeal);
        // 메인 LinearLayout 선언
        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTitle.getWindowToken(),0);
                imm.hideSoftInputFromWindow(editAppeal.getWindowToken(),0);
            }
        });

        // 유저 정보 가져오기
        StrictMode.ThreadPolicy Upolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(Upolicy);
        String result = BUSHttp.UserSendByHttp();    // 서버와 통신
        String[][] UserparseData = BUJParser.UserjsonParserList(result);  //json 데이터 파싱
        String[] Udata = new String[UserparseData.length];
        for (int i = 0; i < Udata.length; i++) {
            if (UserparseData[i][0].equals(userId)) {    // 데이터베이스의 회원 아이디와 받은 회원 아이디값이 같으면 실행
                userName = UserparseData[i][2];                userBirth = UserparseData[i][3];
                userProf = UserparseData[i][4];                userLocal = UserparseData[i][5];
                userPurp = UserparseData[i][6];                 userImageurl = UserparseData[i][7];
                break;
            } else {    // 아닐 경우 반복문 실행
                continue;
            }
        }

        // 게시판 내의 회원 정보칸에 개인정보 세팅
        textName.setText(userName);        textBirth.setText(userBirth);
        textLocal.setText(userLocal);        textProf.setText(userProf);
        textPurp.setText(userPurp);
        openImage("http://tmdwns9738.cafe24.com/report_project/img/" + userImageurl);
        userImage.setBackground(new ShapeDrawable(new OvalShape()));
        userImage.setClipToOutline(true);
        // 지도 생성
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapLoc);
        mapFragment.getMapAsync(this);
        settingGPS();   // 현재 위치 세팅
        getMyLocation();    // 지역 가져오기
        // 구글 API - 장소 자동완성 사용
        mGoogleClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).build();

        // 구글 API - 장소 자동완성 사용
        autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            // 나열된 장소 선택 시
            public void onPlaceSelected(Place place) {
                lat = place.getLatLng().latitude;   // 위도 저장
                lon = place.getLatLng().longitude;  // 경도 저장
                locat = place.getName().toString(); // 현재 위치의 지명 저장
                userLocation = place.getAddress().toString();
                Log.i("지도 자동완성으로 생성된 위치의 위도/경도", String.valueOf(lat) + " / " + String.valueOf(lon));
                onMapReady(mMap);
            }
            @Override
            public void onError(Status status) {
                Log.i( "오류","An error occurred: " + status);
            }
        });

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
        menuInflater.inflate(R.menu.menu2, menu);
        return true;
    }

    // ActionBar 의 버튼 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 뒤로 가기
            case android.R.id.home:
                Toast.makeText(BoardAddActivity.this,"글쓰기를 취소합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BoardAddActivity.this, ListActivity.class);
                intent.putExtra("resBoard", true);
//                NavUtils.navigateUpFromSameTask(this);   실행되어있던 Activity 모두 종료
                finish();
                return true;
            case R.id.itemSave:
                // 현재 작성일 설정
                long now = System.currentTimeMillis();
                Date da = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   // 날짜 표현 포맷 설정
                // 게시판 등록을 위한 변수 선언 및 값 저장
                String date = dateFormat.format(da);
                String title = editTitle.getText().toString();
                String id = userId;
                String name = userName;
                String content = editAppeal.getText().toString();
                // 게시판 데이터베이스에 작성한 글 추가
                BIData.BoardInsertdata(title, date, id, name, userLocation, content);
                Toast.makeText(getApplicationContext(), "게시글을 작성하였습니다.", Toast.LENGTH_SHORT).show();
                // 화면 전환 (ListActivity로)
                Intent intent1 = new Intent(getApplicationContext(), ListActivity.class);
                intent1.putExtra("resBoard", true);
                startActivity(intent1);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // gps 로 현재 좌표 가져오기
    public void settingGPS() {
        Log.e("dkdkdkdk", "111111111111111111111111");
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager)getSystemService(BoardAddActivity.LOCATION_SERVICE);
        Log.e("dkdkdkdk", "2222222222222222222222");
        locationListener = new LocationListener() {
            // 현재위치가 계속 바뀌면 변수에 위도 경도 저장 후 지도 재 생성
            public void onLocationChanged(Location location) {
                Log.e("dkdkdkdk", "333333333333333333333");
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.i("현재 위치의 위도/경도", String.valueOf(lat) + " " + String.valueOf(lon));
                // 지도 생성
                onMapReady(mMap);
                // TODO 위도, 경도로 하고 싶은 것
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {            }
            public void onProviderEnabled(String provider) {            }
            public void onProviderDisabled(String provider) {            }
        };

    }

     // 사용자의 위치를 수신
    public Location getMyLocation() {

        Log.e("dkdkdkdk", "4444444444444444444444444");
        Location currentLocation = null;
        Log.e("dkdkdkdk", "555555555555555555555555555");
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            Log.e("dkdkdkdk", "6666666666666666666666666666");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else {
            Log.e("dkdkdkdk", "77777777777777777777777777777");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
            Log.e("dkdkdkdk", "888888888888888888888888888888");
            if(locationManager != null) {
                // 수동으로 위치 구하기
                Log.e("dkdkdkdk", "999999999999999999999999999999999");
                String locationProvider = LocationManager.GPS_PROVIDER;
                currentLocation = locationManager.getLastKnownLocation(locationProvider);
                if (currentLocation != null) {
                    lon = currentLocation.getLongitude();
                    lat = currentLocation.getLatitude();
                    Log.d("Main", "longtitude=" + lon + ", latitude=" + lat);
                }
            }
        }
        return currentLocation;
    }

    // 지도 생성
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat,lon);
        MarkerOptions mOptions = new MarkerOptions();
        mOptions.title("현재 위치");
        mOptions.snippet(locat);    // 마커의 스니펫(간단한 텍스트) 설정
        mOptions.position(sydney);
        mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_black_18dp));
        mMap.addMarker(mOptions);   // 마커(핀) 추가
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        locationManager.removeUpdates(locationListener);    // GPS 수신 종료
    }

    // 이미지 업로드
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
