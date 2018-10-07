package project.example.com.report_project.chat;

public class Chat_Selectlist {

    private String user_name;
    private String user_id;
    private String user_imgurl;

    public Chat_Selectlist(String user_id, String user_name, String user_imgurl) {
        this.user_name = user_name;
        this.user_imgurl = user_imgurl;
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_imgurl() {
        return user_imgurl;
    }

    public void setUser_imgurl(String user_imgurl) {
        this.user_imgurl = user_imgurl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
