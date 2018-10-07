package project.example.com.report_project.notice;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Notice_UserJsonParser {

    // JSON 파싱
    public String[][] UserjsonParserList(String pRecvServerPage) {

        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

        try {
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            // 받아온 pRecvServerPage를 분석하는 부분
            String[] jsonName = {"id", "pw", "name", "birth", "prof", "local", "purp", "imgurl"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for (int i = 0; i < jArr.length(); i++) {
                json = jArr.getJSONObject(i);

                for (int j = 0; j < jsonName.length; j++) {
                    parseredData[i][j] = json.getString(jsonName[j]);
                }
            }

            // 분해 된 데이터를 확인하기 위한 부분
            for (int i = 0; i < parseredData.length; i++) {
//                Log.i("JSON을 분석한 데이터 " + i + " : ", parseredData[i][0]);
//                Log.i("JSON을 분석한 데이터 " + i + " : ", parseredData[i][1]);
//                Log.i("JSON을 분석한 데이터 " + i + " : ", parseredData[i][2]);
//                Log.i("JSON을 분석한 데이터 " + i + " : ", parseredData[i][3]);
//                Log.i("JSON을 분석한 데이터 " + i + " : ", parseredData[i][4]);
            }

            return parseredData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
