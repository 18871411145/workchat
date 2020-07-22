package cn.lxbest.wb2020.workchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;

public class ZSZJ_XQ_Activity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_title,tv_content;//标题内容文本

    Button btn_xg,btn_sc;//修改删除按钮

    private int zid;//知识总结id
    private String title;
    private String content;

    AlertDialog dialog;//判断对话框
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zszj_xq_activity);
        Funcs.setMyActionBar(this,"知识总结");

        //初始化控件
        init();
        Intent intent=getIntent();
        zid=intent.getIntExtra("zid",0);
        title=intent.getStringExtra("title");
        content=intent.getStringExtra("content");

        tv_title.setText(title);
        tv_content.setText(content);

        btn_xg.setOnClickListener(this);
        btn_sc.setOnClickListener(this);
    }


    void init(){
        tv_title=findViewById(R.id.tv_title);
        tv_content=findViewById(R.id.tv_content);

        btn_xg=findViewById(R.id.btn_xg);
        btn_sc=findViewById(R.id.btn_sc);

        dialog=App.getAlterDialog(this, "是否删除", "", new Funcs.CallbackInterface() {
            @Override
            public void onCallback(Object obj) {
                //删除该知识总结
                delete();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.btn_xg:
                //修改操作
                Intent intent=new Intent(this,Add_ZSZJ_Activity.class);
                intent.putExtra("zid",zid);
                intent.putExtra("content",content);
                intent.putExtra("title",title);
                startActivity(intent);
                break;
            case R.id.btn_sc:
                //删除操作
                dialog.show();
                break;
        }
    }

    //删除知识总结
    private void delete(){

        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.zszj_m,"zid="+zid+"&type=2");

        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject js=Funcs.bytetojson(responseBody);
                if(js!=null){
                    try {
                        int code=js.getInt(Const.Key_Resp.Code);
                        if(code==200){
                            //添加成功
                            Funcs.showtoast(ZSZJ_XQ_Activity.this,"成功");
                        }else{
                            //添加失败
                            Funcs.showtoast(ZSZJ_XQ_Activity.this,"失败");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(ZSZJ_XQ_Activity.this,"服务器错误连接");
            }
        });
    }
}
