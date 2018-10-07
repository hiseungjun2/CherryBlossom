package project.example.com.report_project.board_comment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.example.com.report_project.R;

public class Comment_ListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Comment_list> comment_listArrayList;

    TextView user_name;
    TextView comment_content;
    TextView comment_date;

    public Comment_ListAdapter(Context context, ArrayList<Comment_list> comment_listArrayList) {
        this.context = context;
        this.comment_listArrayList = comment_listArrayList;
    }

    @Override
    public int getCount() {
        return comment_listArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.comment_listArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_list,null);

            user_name = (TextView)view.findViewById(R.id.user_name);
            comment_content = (TextView)view.findViewById(R.id.comment_content);
            comment_date = (TextView)view.findViewById(R.id.comment_date);

            user_name.setText(comment_listArrayList.get(i).getUser_name());
            comment_content.setText(comment_listArrayList.get(i).getComment_content());
            comment_date.setText(comment_listArrayList.get(i).getComment_date());
        }
        return view;
    }

}
