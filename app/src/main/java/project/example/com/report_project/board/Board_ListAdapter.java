package project.example.com.report_project.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.example.com.report_project.R;

public class Board_ListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Board_list> board_listArrayList;

    TextView user_name;
    TextView board_title;
    TextView board_date;

    public Board_ListAdapter(Context context, ArrayList<Board_list> board_listArrayList) {
        this.context = context;
        this.board_listArrayList = board_listArrayList;
    }

    @Override
    public int getCount() {
        return board_listArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.board_listArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.board_list,null);

            user_name = (TextView)view.findViewById(R.id.user_name);
            board_title = (TextView)view.findViewById(R.id.board_title);
            board_date = (TextView)view.findViewById(R.id.board_date);

            user_name.setText(board_listArrayList.get(i).getUser_name());
            board_title.setText(board_listArrayList.get(i).getBoard_title());
            board_date.setText(board_listArrayList.get(i).getBoard_date());
        }
        return view;
    }

}
