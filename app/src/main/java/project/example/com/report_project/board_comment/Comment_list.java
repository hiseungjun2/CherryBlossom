package project.example.com.report_project.board_comment;

/**
 * Created by SeungJun on 2018-05-20.
 */

public class Comment_list {

    private String user_id;
    private String user_name;
    private String comment_content;
    private String comment_date;

    public Comment_list(String user_id, String user_name, String comment_content, String comment_date) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.comment_content = comment_content;
        this.comment_date = comment_date;
    }

    public String getUser_name() {        return user_name;    }

    public void setUser_name(String user_name) {        this.user_name = user_name;    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }
}
