package project.example.com.report_project;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import project.example.com.report_project.chat.Chat_ViewListAdapter;
import project.example.com.report_project.chat.chatDto;

public class ChatViewActivity extends AppCompatActivity {

    String user_name;   // 사용자 이름
    String user_id;     // 사용자 아이디
    String chatuser_name;   // 상대방 유저 이름
    String chatuser_imgurl;     // 상대방 유저 이미지
    String chatuser_id;     // 상대방 유저 아이디
    String message_date;    // 메세지 작성 시간 변수
    String chatRoonName;    // 채팅방 이름

    ListView chatListView;
    EditText msgEdit;
    Button msgBtn;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference =   firebaseDatabase.getReference();

    ArrayList<chatDto> chatDtoArrayList = new ArrayList<chatDto>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        // 액션바 등록 및 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 채팅룸 리스트에서 받아온 사용중인 회원
        Intent intent = getIntent();
        chatuser_name = intent.getStringExtra("chatuser_name");
        chatuser_imgurl = intent.getStringExtra("chatuser_imgurl");
        chatuser_id = intent.getStringExtra("chatuser_id");
        user_name = intent.getStringExtra("user_name");
        user_id = intent.getStringExtra("user_id");

        // 타이틀 설정
        setTitle(chatuser_name);

        chatListView = findViewById(R.id.chatListView);
        msgEdit = findViewById(R.id.msgEdit);
        msgBtn = findViewById(R.id.msgBtn);

        final Chat_ViewListAdapter chat_viewListAdpater = new Chat_ViewListAdapter(this,R.layout.their_message, chatDtoArrayList, user_id);
        chatListView.setAdapter(chat_viewListAdpater);

        String strUser[] = user_id.split("@");
        String strUserComma[] = strUser[1].split("\\.");
        String strChatUser[] = chatuser_id.split("@");
        String strChatUserComma[] = strChatUser[1].split("\\.");

        if(chatuser_id.compareTo(user_id) > 0) {
            chatRoonName = strUser[0]+strUserComma[0]+"_"+strChatUser[0]+strChatUserComma[0];
        } else if(chatuser_id.compareTo(user_id) < 0) {
            chatRoonName = strChatUser[0]+strChatUserComma[0]+"_"+strUser[0]+strUserComma[0];
        }

        databaseReference.child("chat").child(chatRoonName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatDto chat = dataSnapshot.getValue(chatDto.class);
                chat_viewListAdpater.addItem(chat.getUser_name(), chat.getUser_id(), chat.getMessage(), chat.getDate(), chat.getImgurl());
                chat_viewListAdpater.notifyDataSetChanged();
                chatListView.setSelection(chat_viewListAdpater.getCount()-1);
//                addMessage(dataSnapshot, chat_theirListAdpater);
                Log.e("LOG", "s:"+s);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msgEdit.getText().toString().equals("")) {
                    return;
                }
                long now = System.currentTimeMillis();
                Date da = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("a h:mm");   // 날짜 표현 포맷 설정
                // 게시판 등록을 위한 변수 선언 및 값 저장
                String date = dateFormat.format(da);
                chatDto chat = new chatDto(user_name, user_id, msgEdit.getText().toString(), date, chatuser_imgurl); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("chat").child(chatRoonName).push().setValue(chat); // 데이터 푸쉬
                msgEdit.setText(""); //입력창 초기화
            }
        });
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
}
