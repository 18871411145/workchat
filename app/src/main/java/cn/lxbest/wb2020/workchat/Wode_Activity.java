package cn.lxbest.wb2020.workchat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.lxbest.wb2020.workchat.DevTD.TestData1;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Wode_Activity extends AppCompatActivity implements View.OnClickListener {

    ImageView image_head;//头像 1
    TextView text_name;//姓名
    TextView text_mobile;//联系方式 2
    TextView text_email;//邮箱 3
    TextView text_sex;//性别 4
    TextView text_age;//年龄 5
    TextView text_department;//所在部门 6
    TextView text_position;//所处职位 7
    TextView text_lastlogin;//最后登录时间
    TextView text_joined;//我参与组的列表
    TextView text_askfor;//员工申请
    //编辑按钮
    TextView text_edit_head,text_edit_mobile,text_edit_email,text_edit_sex,text_edit_age,text_edit_department,text_edit_position;

    //修改各信息栏
    ConstraintLayout cons_bj;//外部局部
    TextView text_bj;//编辑类型
    EditText edt_bj;//编辑输入框
    Button btn_post;//上传数据按钮

    //优化代码的存储器；存放state及其代表的含义
    Map<Integer,String> map=new HashMap();

    //推出登录
    Button btn_logout;

    void init(){
        image_head=findViewById(R.id.head_image);
        text_name=findViewById(R.id.name);
        text_mobile=findViewById(R.id.lxfs);
        text_email=findViewById(R.id.yx);
        text_sex=findViewById(R.id.sex);
        text_age=findViewById(R.id.age);
        text_department=findViewById(R.id.department);
        text_position=findViewById(R.id.zhuwei);
        text_lastlogin=findViewById(R.id.lastlogin);
        text_joined=findViewById(R.id.wdzphj);
        text_askfor=findViewById(R.id.xqhzlb);

        text_edit_head=findViewById(R.id.textView6);
        text_edit_mobile=findViewById(R.id.bj);
        text_edit_email=findViewById(R.id.textView);
        text_edit_sex=findViewById(R.id.textView2);
        text_edit_age=findViewById(R.id.textView3);
        text_edit_department=findViewById(R.id.textView4);
        text_edit_position=findViewById(R.id.textView5);

        cons_bj=findViewById(R.id.cons_bj);
        text_bj=findViewById(R.id.bj_type);
        edt_bj=findViewById(R.id.bj_text);
        btn_post=findViewById(R.id.post);

        btn_logout=findViewById(R.id.logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.logout(Wode_Activity.this);
            }
        });
    }

    void addtext(){
        Picasso.with(this).load(Funcs.qnUrl(App.user.qnid)).placeholder(R.drawable.home_head).into(image_head);
        text_name.setText(App.user.name);
        text_mobile.setText(App.user.mobile);
        text_email.setText(App.user.email);
        text_sex.setText(App.user.sex==1?"男":"女");
        text_department.setText(App.user.department);
        text_position.setText(App.user.position);
        text_age.setText(App.user.age+"");
         String last=Const.Year_Month_Day.format(new Date(App.user.last_login));
        text_lastlogin.setText(last);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wode_activity);
        Funcs.setMyActionBar(this,"我的");

        //初始化控件
        init();
        //填充数据
        addtext();
        //按钮监听
        text_edit_head.setOnClickListener(this);
        text_edit_mobile.setOnClickListener(this);
        text_edit_email.setOnClickListener(this);
        text_edit_sex.setOnClickListener(this);
        text_edit_age.setOnClickListener(this);
        text_edit_department.setOnClickListener(this);
        text_edit_position.setOnClickListener(this);

        btn_post.setOnClickListener(this);

        text_joined.setOnClickListener(this);
        text_askfor.setOnClickListener(this);

        //设置权限
        App.verifyStoragePermissions(this);
    }

    int state=1;//含义为post数据时的类型（控件定义备注数字）
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.textView6:
                //编辑头像
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent,"选择图片"),22);
                break;
            case R.id.bj:
                //编辑电话
                state=1;
                map.put(1,Const.Field_Table_User.phone);
                cons_bj.setVisibility(View.VISIBLE);
                text_bj.setText("联系电话");
                break;
            case R.id.textView:
                //编辑邮箱
                state=2;
                map.put(2,Const.Field_Table_User.Email);
                cons_bj.setVisibility(View.VISIBLE);
                text_bj.setText("电子邮箱");
                break;
            case R.id.textView2:
                //编辑性别
                state=3;
                map.put(3,Const.Field_Table_User.Sex);
                cons_bj.setVisibility(View.VISIBLE);
                text_bj.setText("性别");
                break;
            case R.id.textView3:
                //编辑年龄
                state=4;
                map.put(4,Const.Field_Table_User.Age);
                cons_bj.setVisibility(View.VISIBLE);
                text_bj.setText("年龄");
                break;
            case R.id.textView4:
                //编辑部门
                state=5;
                map.put(5,Const.Field_Table_User.department);
                cons_bj.setVisibility(View.VISIBLE);
                text_bj.setText("所在部门");
                break;
            case R.id.textView5:
                //编辑职位
                state=6;
                map.put(6,Const.Field_Table_User.position);
                cons_bj.setVisibility(View.VISIBLE);
                text_bj.setText("所处职位");
                break;
            case R.id.post:
                //提交修改内容
                postChange();
                break;
            case R.id.wdzphj:
                //显示我加入的组
                Intent intent1=new Intent(this,Joined_Activity.class);
                intent1.putExtra(Const.Field_Table_User.Uid,App.user.uid);
                startActivity(intent1);
                break;
            case R.id.xqhzlb:
                //显示我的部门员工申请
                Intent intent2=new Intent(this,Member_Activity.class);
                intent2.putExtra(Const.Field_Table_User.Uid,App.user.uid);
                startActivity(intent2);
                break;
        }
    }


    byte[] bytes=null;

    //获取图片返回的流并写入字节数组中，方便传给七牛
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==22&&resultCode== Activity.RESULT_OK&&data!=null){
            //上传图片过程中用loading界面掩盖
            App.showLoadingMask(this);
            Uri uri=data.getData();
            try {
                InputStream is=this.getContentResolver().openInputStream(uri);
                Bitmap bitmap= BitmapFactory.decodeStream(is);
                image_head.setImageBitmap(bitmap);
                InputStream inputStream=this.getContentResolver().openInputStream(uri);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                byte[] bytes=output.toByteArray();
                inputStream.close();
                output.close();
                App.postImgToQnServer(bytes, new Funcs.CallbackInterface() {
                    @Override
                    public void onCallback(Object obj) {
                        if(obj==null){
                            App.hideLoadingMask(Wode_Activity.this);
                            return;
                        }
                        String qnid= (String) obj;
                        postQnid(qnid);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void postQnid(String qnid){
        String url= Funcs.servUrlWQ(Const.Key_Resp_Path.person,"qnid="+qnid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseData(jsonObject);
                }

                App.hideLoadingMask(Wode_Activity.this);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(Wode_Activity.this,"修改失败,请检查网络连接");
                App.hideLoadingMask(Wode_Activity.this);
            }
        });
    }

    void parseData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                Funcs.showtoast(this,"上传成功");
            }else{
                Funcs.showtoast(this,"上传失败");
            }

        }catch (Exception e){
        }
    }

    void postChange(){

        String s=edt_bj.getText().toString().trim();
        if (s.length()==0){
            cons_bj.setVisibility(View.GONE);
            return;
        }

        String url= Funcs.servUrl(Const.Key_Resp_Path.person);
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(map.get(state),edt_bj.getText().toString());
        }catch (Exception e){

        }
            HttpEntity entity=new StringEntity(jsonObject.toString(), HTTP.UTF_8);

            App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject data= Funcs.bytetojson(responseBody);
                    parseResData(data);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    if(App.env==Const.Env.DEV_TD){
                        JSONObject jsonObject1= TestData1.JustOk();
                        parseResData(jsonObject1);
                    }else{
                        Funcs.showtoast(Wode_Activity.this,"修改失败,请检查网络连接");
                        cons_bj.setVisibility(View.GONE);
                    }

                }
            });


    }
    //TODO 客户端还是服务器做判断看数据格式是否合理
    void parseResData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                //修改个人页面信息并修改preference
                String content=edt_bj.getText().toString().trim();
                if(state==1){
                    text_mobile.setText(content);
                    App.user.mobile=content;
                }else if(state==2){
                    text_email.setText(content);
                    App.user.email=content;
                }else if(state==3){
                    text_sex.setText(content);
                    App.user.sex=content.equals("男")?1:2;
                }else if(state==4){
                    text_age.setText(content);
                    App.user.age=Integer.parseInt(content);
                }else if(state==5){
                    text_department.setText(content);
                    App.user.department=content;
                }else{
                    text_position.setText(content);
                    App.user.position=content;
                }
                App.putUserToPreference();
                Funcs.showtoast(this,"修改成功");
            }else{
                Funcs.showtoast(this,"修改失败,格式不正确");
            }
            edt_bj.setText("");
            cons_bj.setVisibility(View.INVISIBLE);
        }catch (Exception e){

        }

    }

}
