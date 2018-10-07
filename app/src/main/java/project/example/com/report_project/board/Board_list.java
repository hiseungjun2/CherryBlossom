package project.example.com.report_project.board;


public class Board_list {

    private String board_id;
    private String user_name;
    private String board_title;
    private String board_date;

    public Board_list(String board_id, String user_name, String board_title, String board_date) {
        this.board_id = board_id;
        this.user_name = user_name;
        this.board_title = board_title;
        this.board_date = board_date;
    }

    public String getBoard_id() {        return board_id;    }

    public void setBoard_id(String board_id) {        this.board_id = board_id;    }

    public String getUser_name() {        return user_name;    }

    public void setUser_name(String user_name) {        this.user_name = user_name;    }

    public String getBoard_title() {
        return board_title;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public String getBoard_date() {
        return board_date;
    }

    public void setBoard_date(String board_date) {
        this.board_date = board_date;
    }

}
