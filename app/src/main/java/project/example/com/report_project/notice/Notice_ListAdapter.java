package project.example.com.report_project.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.example.com.report_project.R;

public class Notice_ListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Notice_list> notice_listArrayList;

//    TextView notice_id;
    TextView notice_title;
    TextView notice_date;

    public Notice_ListAdapter(Context context, ArrayList<Notice_list> notice_listArrayList) {
        this.context = context;
        this.notice_listArrayList = notice_listArrayList;
    }

    @Override
    public int getCount() {
        return notice_listArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.notice_listArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.notice_list,null);

//            notice_id = (TextView)view.findViewById(R.id.notice_id);
            notice_title = (TextView)view.findViewById(R.id.notice_title);
            notice_date = (TextView)view.findViewById(R.id.notice_date);

//            notice_id.setText(notice_listArrayList.get(i).getNotice_id());
            notice_title.setText(notice_listArrayList.get(i).getNotice_title());
            notice_date.setText(notice_listArrayList.get(i).getNotice_date());
        }
        return view;
    }
}
