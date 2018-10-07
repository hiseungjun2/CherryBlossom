package project.example.com.report_project.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import project.example.com.report_project.R;

public class Chat_ViewListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<chatDto> chatDtoArrayList;
    private String user_id;
    private int resid;
    public Bitmap bmImg;
    public MessageViewHolder holder;


    public Chat_ViewListAdapter(Context context, int resid, ArrayList<chatDto> chatDtoArrayList, String user_id) {
        this.context = context;
        this.resid = resid;
        this.user_id = user_id;
        this.chatDtoArrayList = chatDtoArrayList;
    }

    public void add(chatDto chat){
        this.chatDtoArrayList.add(chat);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatDtoArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.chatDtoArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        holder = new MessageViewHolder();
        chatDto chat = chatDtoArrayList.get(i);
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (chat.getUser_id().equals(user_id)) { //승준아 여기에다가는 자기자신인지 확인하기위해서 자기 이메일 값만 넣어주면되 ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
            view = messageInflater.inflate(R.layout.my_message, null);
            holder.message_body = view.findViewById(R.id.message_body);
            holder.message_date = view.findViewById(R.id.message_date);
            view.setTag(holder);
            holder.message_body.setText(chat.getMessage());
            holder.message_date.setText(chat.getDate());

        } else {
            view = messageInflater.inflate(R.layout.their_message, null);
            holder.image = view.findViewById(R.id.image);
            holder.name = view.findViewById(R.id.name);
            holder.message_body = view.findViewById(R.id.message_body);
            holder.message_date = view.findViewById(R.id.message_date);
            view.setTag(holder);

            holder.name.setText(chat.getUser_name());
            holder.message_body.setText(chat.getMessage());
            holder.message_date.setText(chat.getDate());
            openImage("http://tmdwns9738.cafe24.com/report_project/img/"+ chatDtoArrayList.get(i).getImgurl());
            holder.image.setImageBitmap(bmImg);
            holder.image.setBackground(new ShapeDrawable(new OvalShape()));
            holder.image.setClipToOutline(true);
        }

//        tmessage_body = view.findViewById(R.id.tmessage_body);
//        tname = view.findViewById(R.id.tname);
//        tmessage_date = view.findViewById(R.id.tmessage_date);
//        timage = view.findViewById(R.id.timage);
//
//        tmessage_body.setText(chatDtoArrayList.get(i).getMessage());
//        tname.setText(chatDtoArrayList.get(i).getUser_name());
//        tmessage_date.setText(chatDtoArrayList.get(i).getDate());
//        openImage("http://hiseungjun2.cafe24.com/report_project/img/"+ chatDtoArrayList.get(i).getImgurl());
//        timage.setBackground(new ShapeDrawable(new OvalShape()));
//        timage.setClipToOutline(true);

        return view;
    }

    public void addItem(String name, String id, String contents, String date, String imgurl) {

        chatDto chat = new chatDto();

        /* MyItem에 아이템을 setting한다. */
        chat.setUser_name(name);
        chat.setMessage(contents);
        chat.setDate(date);
        chat.setImgurl(imgurl);
        chat.setUser_id(id);

        /* mItems에 MyItem을 추가한다. */
        chatDtoArrayList.add(chat);
    }

    // 이미지 불러오기
    public Bitmap openImage(String url) {
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            bmImg = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmImg;
    }
}

class MessageViewHolder{
    public TextView message_body;
    public TextView name;
    public TextView message_date;
    public ImageView image;
}
