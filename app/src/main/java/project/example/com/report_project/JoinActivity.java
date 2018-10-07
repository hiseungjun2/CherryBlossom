package project.example.com.report_project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import project.example.com.report_project.join.Join_InsertUserData;
import project.example.com.report_project.join.Join_JsonParser;
import project.example.com.report_project.join.Join_SendbyHttp;

// 회원가입 Activity
public class JoinActivity extends AppCompatActivity {
    // 변수 선언
    EditText joinId;    // 유저 회원가입 이메일 작성 공간
    EditText joinPw;    // 유저 회원가입 비밀번호 작성 공간
    EditText joinName;  // 유저 회원가입 이름 작성 공간
    TextView joinBirth;     // 유저 회원가입 생일 텍스트뷰
    TextView joinProf;      // 유저 회원가입 직업 텍스트뷰
    TextView joinLocal1;    // 유저 회원가입 도 텍스트뷰
    TextView joinLocal2;    // 유저 회원가입 시/군/구 텍스트뷰
    TextView joinPurp;  // 유저 회원가입 목적 텍스트뷰
    ImageView btnCamera;    // 카메라 이미지뷰
    ImageView btnIdCheck;   // 이메일 중복확인 버튼
    LinearLayout mainLayout;    // 메인 레이아웃
    // db 에 넣기위한 날짜, 지역 변수 선언
    String dateBirth;
    // 날짜 다이얼로그를 위한 정수형 변수 선언
    int mYear;    int mMonth;    int mDate;
    //    년도            월                   일
    // 객체 생성
    Join_SendbyHttp JSbHttp = new Join_SendbyHttp();    // 회원가입 위해 서버와 통신하는 메서드
    Join_InsertUserData JIUData = new Join_InsertUserData();    // DB에 저장하기 위한 메서드
    Join_JsonParser JJParser = new Join_JsonParser();   // 이메일 중복확인 위해 정보 파싱하는 메서드
    String currentPhotopath; //실제 사진 파일 경로
    String imagePath;
    private Uri imageURI, photoURI, albumURI;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private final int GALLERY_CODE = 11111; //requestCode 선택한 사진에 대한 요청값을 구분하는 용도
    private final int CAMERA_CODE = 22222; // requestCode 선택한 사진에 대한 요청값을 구분하는 용도
    private static final int MY_PERMISSION_CAMERA = 3333;
    private final int REQUEST_CAMERA_IMAGE_CROP = 4444;
    private final int REQUEST_ALBUM_IMAGE_CROP = 5555;
    //이미지 파일 전송에쓰일 URL  수정-------------------------------------------------------------------------------------------
    private String serverURL = "http://tmdwns9738.cafe24.com/report_project/UserPictureUpdate.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // 액션바 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);  // 액션바 뒤로가기 버튼 생성
        actionBar.setHomeButtonEnabled(true);   // // 액션바 뒤로가기 버튼 생성
        setTitle("회원 가입");  // 액션바 제목 설정

        // 변수에 ID 값 저장
        joinId = findViewById(R.id.joinId);   // 이메일
        joinPw = findViewById(R.id.joinPw);   // 비밀번호
        joinName = findViewById(R.id.joinName);   // 이름
        joinBirth = findViewById(R.id.joinBirth);     // 생일
        joinProf = findViewById(R.id.joinProf);   // 직업
        joinLocal1 = findViewById(R.id.joinLocal1);   // 지역(도)
        joinLocal2 = findViewById(R.id.joinLocal2);   // 지역(시/군/구)
        joinPurp = findViewById(R.id.joinPurp);   // 목적
        btnCamera = findViewById(R.id.btnCamera);    // 카메라 이미지
        btnIdCheck = findViewById(R.id.btnIdCheck);  // 중복확인 버튼
        // 메인 LinearLayout 선언
        mainLayout = findViewById(R.id.mainLayout);
        // 배경화면 클릭 시 소프트키보드 사라지게 하기 위해 만든 ClickListener
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(joinId.getWindowToken(),0);
                imm.hideSoftInputFromWindow(joinPw.getWindowToken(),0);
                imm.hideSoftInputFromWindow(joinName.getWindowToken(),0);
            }
        });

        // 오늘 날짜 저장
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);        mMonth = c.get(Calendar.MONTH);        mDate = c.get(Calendar.DAY_OF_MONTH);
        //    년도                                        월                                                       일

        // AlertDialog 위한 배열 선언
        final CharSequence[] Camera = {"사진 촬영", "앨범에서 사진 선택"};
        final CharSequence[] Professor = {"학생", "회사원", "기타"};
        final CharSequence[] localDo = {"경기도", "강원도", "충청북도", "충청남도", "경상남도",
                "경상북도", "전라남도", "전라북도", "제주특별자치도",
                "서울특별시", "부산광역시", "대구광역시","인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시"};
        final CharSequence[] localSi = {"창원시", "진주시", "포항시", "경주시", "안동시",
                "목포시", "여수시", "전주시", "군산시",
                "천안시", "공주시", "청주시", "충주시",
                "춘천시", "원주시", "수원시", "성남시",
                "제주시", "서귀포시"};
        final CharSequence[] Purpose = {
                "친한 친구를 사귀고 싶어서",
                "연애를 하고 싶어서",
                "사람을 만나는 것을 좋아해서",
                "기타"
        };

        PermissionCheckAndRequest();
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //퍼미션 체크 및 요청
                AlertDialog.Builder alterBuilder = new AlertDialog.Builder(JoinActivity.this);
                // 전체 실행할 코드
                alterBuilder.setTitle("업로드할 이미지 선택");
                alterBuilder.setItems(Camera, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case PICK_FROM_CAMERA :
                                TakePicture();
                                break;
                            case PICK_FROM_ALBUM :
                                SelectGallery();
                                break;
                        }
                    }
                });
                AlertDialog dialog = alterBuilder.create();
                dialog.show();
            }
        });


        // 아이디 중복확인 이벤트
        btnIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String result = JSbHttp.JoinSendByHttp();
                String[][] parseData = JJParser.jsonParserList(result);  //json 데이터 파싱
                String[] data = new String[parseData.length];
                // 중복체크 위한 임시변수 선언
                boolean flag = false;
                for(int i = 0 ; i < data.length ; i++) {
                    if(parseData[i][0].equals(joinId.getText().toString())) {
                        // 같은 아이디가 있다면 flag 값 변경
                        flag = true;
                        break;
                    }
                }
                if(flag) {
                    // 참일 시
                    Toast.makeText(getApplicationContext(),"아이디를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 거짓일 시
                    Toast.makeText(getApplicationContext(),"사용가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // 날짜 버튼 이벤트
        joinBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(JoinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        // 사용자가 입력한 날짜 값 저장
                        mYear = year;                        mMonth = month;                        mDate = date;
                        // 날짜를 String 으로 변환
                        dateBirth = String.valueOf(mYear) + "-" + String.valueOf(mMonth+1) + "-" + String.valueOf(mDate);
                        joinBirth.setText(String.format("%d년 %d월 %d일", mYear, mMonth+1, mDate));
                    }
                },mYear, mMonth, mDate);
                // 다이얼로그 출력
                datePickerDialog.show();
            }
        });

        // 직업 선택 이벤트
        joinProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alterBuilder = new AlertDialog.Builder(JoinActivity.this);
                alterBuilder.setTitle("직업 선택")
                                .setSingleChoiceItems(Professor, -1,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                joinProf.setText(Professor[i]);
                                            }
                                        })
                                .setPositiveButton("확인", null)
                                .setNegativeButton("취소", null);
                AlertDialog alertDialog = alterBuilder.create();
                alertDialog.show();
            }
        });

        // 도 선택 이벤트
        joinLocal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alterBuilder = new AlertDialog.Builder(JoinActivity.this);
                alterBuilder.setTitle("도 선택")
                                .setSingleChoiceItems(localDo, -1,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                joinLocal1.setText(localDo[i]);
                                            }
                                        })
                                .setPositiveButton("확인", null)
                                .setNegativeButton("취소", null);
                AlertDialog alertDialog = alterBuilder.create();
                alertDialog.show();
            }
        });

        // 시/군/구 선택 이벤트
        joinLocal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alterBuilder = new AlertDialog.Builder(JoinActivity.this);
                alterBuilder.setTitle("시/군/구 선택")
                                .setSingleChoiceItems(localSi, -1,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                joinLocal2.setText(localSi[i]);
                                            }
                                        })
                                .setPositiveButton("확인", null)
                                .setNegativeButton("취소", null);
                AlertDialog alertDialog = alterBuilder.create();
                alertDialog.show();
            }
        });

        // 목적 선택 이벤트
        joinPurp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alterBuilder = new AlertDialog.Builder(JoinActivity.this);
                alterBuilder.setTitle("목적 선택")
                                .setSingleChoiceItems(Purpose, -1,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                joinPurp.setText(Purpose[i]);
                                            }
                                        })
                                .setPositiveButton("확인", null)
                                .setNegativeButton("취소", null);
                AlertDialog alertDialog = alterBuilder.create();
                alertDialog.show();
            }
        });
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
            case android.R.id.home:
                Toast.makeText(JoinActivity.this,"회원가입을 취소합니다.", Toast.LENGTH_SHORT).show();
//                NavUtils.navigateUpFromSameTask(this);   실행되어있던 Activity 모두 종료
                finish();
                return true;
            case R.id.itemSave:
                String id = joinId.getText().toString();
                String pw = joinPw.getText().toString();
                String name = joinName.getText().toString();
                String prof = joinProf.getText().toString();
                String localSum = joinLocal1.getText().toString() + "-" + joinLocal2.getText().toString();
                String purp = joinPurp.getText().toString();
                // 회원 이미지 업로드
                if(imagePath != null) {
                    DoFileUpload(serverURL, imagePath);
                    String filepath = new File(imagePath).getName();
                    JIUData.JoinInsertUserdata(id, pw, name, dateBirth, prof, localSum, purp, filepath);
                } else {
                    JIUData.JoinInsertUserdata(id, pw, name, dateBirth, prof, localSum, purp, "");
                }
                Toast.makeText(getApplicationContext(),"회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void TakePicture() {
        String state = Environment.getExternalStorageState(); //외부 저장소가 현재 사용 가능한지 확인
        if (Environment.MEDIA_MOUNTED.equals(state)) { // 외부저장소를 사용 할 수 있을 시
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//카메라 호출한다
            if (intent.resolveActivity(getPackageManager()) != null) {//카메라호출 인텐트를 처리할수 있는 앱이 있는지 알아봄
                File photoFile = null;
                try {
                    photoFile = createImageFile(); // 카메라로 찍은 사진을 실제 파일로 생성하는 코드 실행
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this, getPackageName(), photoFile); //${applicationId}.provider ,project.example.com.report_project 지우지말것
                    imageURI = photoURI;

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_CODE); //오버라이드 onActivityResult()의 case 문 CAMERA_CODE 실행
                }
            }
        }
    }

    // 이미지형식 파일로 생성하는 코드
    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());//이미지파일 이름형식 지정
        String mImageCaptureName = "JPEG_" + timeStamp + ".jpg"; //이미지파일 확장자명 지정
        File imageFile = null;
        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "gyeom");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        imageFile = new File(dir, mImageCaptureName);
        currentPhotopath = imageFile.getAbsolutePath();
        return imageFile;
    }

    //카메라로 찍은 사진 컴퓨터에 저장
    private void GalleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotopath);
        Uri contentURI = Uri.fromFile(f);
        mediaScanIntent.setData(contentURI);
        sendBroadcast(mediaScanIntent);
    }

    //갤러리에서 사진을 가져오는 경우
    private void SelectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE); //오버라이드 onActivityResult()의 case 문 GALLERY_CODE 실행
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 카메라로 사진을 찍거나 갤러리에서 사진 선택에 대한 사용자가 응답을 할경우  실행
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { //사진을 선택했을경우 취소했을 경우는 RESULT_CANCLE
            switch (requestCode) {
                case GALLERY_CODE:
                    if (data.getData() != null) {
                        try {
                            photoURI = data.getData();
                            cropImage_album();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBKLSE", "sds");
                        }
                    }
                    break;
                case CAMERA_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            cropImage_camera();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //카메라 취소버튼 누렀을시
                    }
                    break;
                case REQUEST_CAMERA_IMAGE_CROP:
                    if (resultCode == Activity.RESULT_OK) {
                        GalleryAddPic();
                        SendPicture(data.getData());
                    } else {
                        //크롭 취소버튼 누를시
                    }
                    break;
                case REQUEST_ALBUM_IMAGE_CROP:
                    if (resultCode == Activity.RESULT_OK) {
                        SendPicture(data.getData());
                    } else {
                        //크롭 취소버튼 누를시
                    }
                    break;
            }
        }
    }

    //사진을 이미지뷰로 전송
    private void SendPicture(Uri imgURI) {
        imagePath = getRealPathFromURI(imgURI);  //URI경로를 해당 파일의 실제 경로를 가져온다
//        Log.i("URI~~~~~~~~~~", "" + imgURI);
//        Log.i("PATH~~~~~~~~~~", "" + imagePath);
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(imagePath); // 선택된 이미지 파일의 Exif테그(어떠한 파일인지)를 읽는다
        } catch (Exception e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL); //선택된 파일의 특정 Exif테그 값을 리턴한다
        int exifDegree = exifOrientationToDegrees(exifOrientation); // 사진의 회전값을 받아오는 함수 실행

        BitmapFactory.Options options;

        try {

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
            bitmap = getCircledBitmap(bitmap);
            btnCamera.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
//            DoFileUpload(serverURL, imagePath);
        } catch (Exception e) {
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                bitmap = getCircledBitmap(bitmap);
                btnCamera.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
            } catch (Exception a) {
                a.getStackTrace();
            }
        }
    }

    // 사진의 회전값을 가져오기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // 사진을 정방향대로 회전하기
    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true); // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
    }

    //사진의 절대 경로 구하기 , 이미지 서버로 전송할떄도 쓰임(getImagePathtouri
    private String getRealPathFromURI(Uri contentURI) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentURI, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    public void cropImage_camera() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX",200);
        //cropIntent.putExtra("outputY",200);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        startActivityForResult(cropIntent, REQUEST_CAMERA_IMAGE_CROP);
    }

    public void cropImage_album() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX",200);
        //cropIntent.putExtra("outputY",200);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoURI);
        startActivityForResult(cropIntent, REQUEST_ALBUM_IMAGE_CROP);
    }


    //요건 수정안함
    public static Bitmap getCircledBitmap(Bitmap bitmap) { // 사진을 원형으로 크롭하기 위한 함수
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

//---------------------------------------------------------------------- 카메라 권한 ↓ ------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(JoinActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    // 퍼미션 체크 함수
    private void PermissionCheckAndRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this).setTitle("알림").setMessage("저장소 권한이 거부되었습니다.").setNegativeButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    // 이미지 업로드
    public class ImageAsyncTask extends AsyncTask<String, Void, String> {
        private String urlString;
        private String params;
        private String fileName;
        public ImageAsyncTask(String urlString, String params, String fileName){
            this.urlString=urlString;
            this.params= params;
            this.fileName= fileName;
        }
        @Override
        protected String doInBackground(String... strings) {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            try {
                File sourceFile = new File(fileName);
                DataOutputStream dos;
                if (!sourceFile.isFile()) {
                    Log.e("uploadFile", "Source File not exist :" + fileName);
                } else {
                    FileInputStream mFileInputStream = new FileInputStream(sourceFile);
                    URL connectUrl = new URL(urlString);
                    // open connection
                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);
                    // write data
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    int bytesAvailable = mFileInputStream.available();
                    int maxBufferSize = 1024 * 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    // read image
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = mFileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    mFileInputStream.close();
                    dos.flush(); // finish upload...
                    if (conn.getResponseCode() == 200) {
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer stringBuffer = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                    }
                    mFileInputStream.close();
                    dos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 파일 업로드
    public void DoFileUpload(String apiUrl, String absolutePath) {
        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(apiUrl, "", absolutePath); //HttpFileUpload(apiUrl, "", absolutePath);
        imageAsyncTask.execute();
    }

}