package project.example.com.report_project.chat;

public class chatDto {
    private String user_name;
    private String message;
    private String date;
    private String imgurl;
    private String user_id;


    public chatDto() {}

    public chatDto(String user_name, String user_id, String message, String date, String imgurl) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.message = message;
        this.date = date;
        this.imgurl = imgurl;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
