package project.example.com.report_project.notice;

public class Notice_list {
    private String notice_id;
    private String notice_title;
    private String notice_date;

    public Notice_list(String notice_id, String notice_title, String notice_date) {
        this.notice_id = notice_id;
        this.notice_title = notice_title;
        this.notice_date = notice_date;
    }

    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public void setNotice_title(String notice_title) {
        this.notice_title = notice_title;
    }

    public String getNotice_date() {
        return notice_date;
    }

    public void setNotice_date(String notice_date) {
        this.notice_date = notice_date;
    }
}
