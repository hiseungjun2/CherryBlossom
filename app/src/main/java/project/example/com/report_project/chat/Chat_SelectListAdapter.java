package project.example.com.report_project.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import project.example.com.report_project.R;

public class Chat_SelectListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Chat_Selectlist> chat_selectlistArrayList;

    private TextView user_name;
    private Bitmap bmImg;
    private ImageView chatSelectImage;


    public Chat_SelectListAdapter(Context context, ArrayList<Chat_Selectlist> chat_selectlistArrayList) {
        this.context = context;
        this.chat_selectlistArrayList = chat_selectlistArrayList;
    }

    @Override
    public int getCount() {
        return chat_selectlistArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.chat_selectlistArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.from(context).inflate(R.layout.chat_selectlist,viewGroup,false);
        }
        user_name = view.findViewById(R.id.chatSelectName);
        chatSelectImage = view.findViewById(R.id.chatSelectImage);

        user_name.setText(chat_selectlistArrayList.get(i).getUser_name());
        openImage("http://tmdwns9738.cafe24.com/report_project/img/"+ chat_selectlistArrayList.get(i).getUser_imgurl());
        chatSelectImage.setBackground(new ShapeDrawable(new OvalShape()));
        chatSelectImage.setClipToOutline(true);
        return view;
    }

//    // 이미지 불러오기
//    public void openImage(String url) {
//        class back extends AsyncTask<String, Integer,Bitmap> {
//            @Override
//            protected Bitmap doInBackground(String... urls) {
//                // TODO Auto-generated method stub
//                try {
//                    URL myFileUrl = new URL(urls[0]);
//                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//                    InputStream is = conn.getInputStream();
//                    bmImg = BitmapFactory.decodeStream(is);
//                    is.close();
//                    chatSelectImage.setImageBitmap(bmImg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return bmImg;
//            }
//        }
//        back task = new back();
//        task.execute(url);
//    }

    // 이미지 불러오기
    public void openImage(String url) {
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            bmImg = BitmapFactory.decodeStream(bis);
            bis.close();
            chatSelectImage.setImageBitmap(bmImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
