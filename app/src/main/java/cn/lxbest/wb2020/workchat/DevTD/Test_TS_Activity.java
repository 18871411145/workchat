package cn.lxbest.wb2020.workchat.DevTD;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cn.lxbest.wb2020.workchat.App;
import cn.lxbest.wb2020.workchat.R;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;

public class Test_TS_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.test_ts_activity);

        Button btn_ts=findViewById(R.id.btn_ts);

        btn_ts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将自己id发送给服务器让服务器推送回来
                sendId();
            }
        });
    }

    void sendId(){
        String url= Funcs.servUrlWQ("ts","deviceId="+App.deviceId);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
