package cn.lxbest.wb2020.workchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

/**添加任务组内知识总结*/
public class Add_ZSZJ_Activity extends AppCompatActivity implements View.OnClickListener{

    EditText et_title,et_content;

    Button bt_post;

    private int tid;

    private int zid;//知识总结id
    private String title;//修改之前的
    private String content;//修改之前的

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_zszj_activity);

        init();
        Intent intent=getIntent();
        tid=intent.getIntExtra("tid",0);
        zid=intent.getIntExtra("zid",0);
        title=intent.getStringExtra("title");
        content=intent.getStringExtra("content");
        if(title!=null&&content!=null&&zid!=0){
            //修改知识总结信息操作
            et_title.setText(title);
            et_content.setText(content);
        }
        bt_post.setOnClickListener(this);
    }

    void init(){
        et_title=findViewById(R.id.et_title);
        et_content=findViewById(R.id.et_content);

        bt_post=findViewById(R.id.bt_post);
    }
    @Override
    public void onClick(View v) {
        //提交知识总结给服务器
        bt_post.setEnabled(false);
        String url;
        if(title!=null&&content!=null&&zid!=0){
            //修改操作
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.zszj_m,"zid="+zid+"&type=1");
        }else{
            //添加操作
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.zszj,"tid="+tid+"&uid="+App.user.uid);
        }

        String title=et_title.getText().toString().trim();//知识总结标题
        String content=et_content.getText().toString().trim();//知识总结内容

        if(title.length()==0||content.length()==0){
            Funcs.showtoast(this,"标题和内容不能为空");
            return;
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Field_Table_ZSZJ.ztitle,title);
            jsonObject.put(Const.Field_Table_ZSZJ.zcontent,content);
        }catch (Exception e){
            e.printStackTrace();
        }

        HttpEntity entity=new StringEntity(jsonObject.toString(), HTTP.UTF_8);

        App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject js=Funcs.bytetojson(responseBody);
                if(js!=null){
                    try {
                        int code=js.getInt(Const.Key_Resp.Code);
                        if(code==200){
                            //添加成功
                            Funcs.showtoast(Add_ZSZJ_Activity.this,"成功");
                        }else{
                            //添加失败
                            Funcs.showtoast(Add_ZSZJ_Activity.this,"失败");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                bt_post.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(Add_ZSZJ_Activity.this,"服务器错误连接");
                bt_post.setEnabled(true);
            }
        });
    }
}
