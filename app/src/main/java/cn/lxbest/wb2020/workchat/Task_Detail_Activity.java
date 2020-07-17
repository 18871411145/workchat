package cn.lxbest.wb2020.workchat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lxbest.wb2020.workchat.DevTD.TestData1;
import cn.lxbest.wb2020.workchat.DevTD.Test_TS_Activity;
import cn.lxbest.wb2020.workchat.Model.User;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Task_Detail_Activity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    int tid=0;

    TextView title,task_content, tv_message,tv_port,tv_zszj;
    Button btn_ss,send_message;

    ListView list_message,list_person;

    PickListAdapter adapter1=new PickListAdapter();//组员信息适配器
    MessageListAdapter adapter2=new MessageListAdapter();//消息适配器

    List<User> list1=new ArrayList<>();//保存聊天组人员
    List<User.Message> list2=new ArrayList<>();//聊天信息保存

    List<HashMap> at_list=new ArrayList<>();//保存@的人id

    ConstraintLayout cons_send,send_menu,pick_person;//发送信息框，功能菜单框,选人界面

    EditText edt_text;//输入文字框
    String edt_s;//输入框中字符串（动态改变）
    ImageView iv_file1,iv_file2;//选取图片跟文件资源按钮
    Map<String,String> files=new HashMap<>();//存放对应资源id

    //设置节点相关ui
    ConstraintLayout cons_setport;//外框架
    EditText et_title,et_content;//节点标题，节点描述
    Button btn_b1,btn_b2;//按钮1，按钮2


    void init(){
        title=findViewById(R.id.title);
        btn_ss=findViewById(R.id.show_send);
        task_content=findViewById(R.id.task_content);
        list_message=findViewById(R.id.talk_list);//消息list
        list_person=findViewById(R.id.member);//选择@的list

        send_menu=findViewById(R.id.send_menu);//菜单
        tv_message =findViewById(R.id.t1);//显示发送消息栏
        tv_port=findViewById(R.id.t2);//管理任务节点
        tv_zszj=findViewById(R.id.t3);//知识总结设置

        cons_send=findViewById(R.id.cons_send);//发送消息栏

        pick_person=findViewById(R.id.pick_person);//选人界面

        edt_text=findViewById(R.id.message);//输入框
        send_message=findViewById(R.id.button1);//发送消息按钮
        iv_file1=findViewById(R.id.iv_file1);//选取图片资源按钮
        iv_file2=findViewById(R.id.iv_file2);//选取其他文件资源按钮

        cons_send.setVisibility(View.GONE);
        send_menu.setVisibility(View.GONE);

        //设置节点相关ui
        cons_setport=findViewById(R.id.cons_setport);
        et_title=findViewById(R.id.et_title);
        et_content=findViewById(R.id.et_content);

        btn_b1=findViewById(R.id.btn_b1);
        btn_b2=findViewById(R.id.btn_b2);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_activity);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        init();

        Intent intent=getIntent();
        tid=intent.getIntExtra(Const.Field_Table_Task.tid,0);
        String ti=intent.getStringExtra(Const.Field_Table_Task.title);
        String content=intent.getStringExtra(Const.Field_Table_Task.content);

        title.setText(ti);
        task_content.setText(content);

        btn_ss.setOnClickListener(this);
        send_message.setOnClickListener(this);

        tv_message.setOnClickListener(this);
        tv_port.setOnClickListener(this);
        tv_zszj.setOnClickListener(this);

        btn_b1.setOnClickListener(this);
        btn_b2.setOnClickListener(this);

        iv_file1.setOnClickListener(this);
        iv_file2.setOnClickListener(this);

        edt_text.addTextChangedListener(this);

        list_person.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String name=list1.get(position).name;
                String text=edt_text.getText().toString();
                if(text.contains("@"+name)){
                    //存在已经@的人（此操作无效）
                    int l=text.lastIndexOf("@");
                    edt_text.setText(text.substring(0,l));
                    pick_person.setVisibility(View.GONE);
                    return;
                }
                edt_text.setText(text+name);
                pick_person.setVisibility(View.GONE);
            }
        });
        list_message.setAdapter(adapter2);
        //得到消息信息
        getMessageData();

    }

    void getMessageData(){
        list2.clear();
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.message,"tid="+tid);

        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseMessageData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getMessage();
                    parseMessageData(jsonObject);
                }else{
                    Funcs.setMyActionBar(Task_Detail_Activity.this,"网络连接失败");
                }
            }
        });

    }
    void parseMessageData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=data.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject js=jsonArray.getJSONObject(i);
                    list2.add(new User.Message(js));
                }
                adapter2.notifyDataSetChanged();
            }else{
                Funcs.setMyActionBar(this,"获取数据错误");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//获取组成员信息
    void getMember(){
        //以为数据不加载
        if(list1.size()!=0){
            list_person.setAdapter(adapter1);
            pick_person.setVisibility(View.VISIBLE);
            App.hideLoadingMask(this);
            return;
        }
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.ats,"tid="+tid);

        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if (jsonObject!=null){
                parseMemberData(jsonObject);
                }

                App.hideLoadingMask(Task_Detail_Activity.this);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getMemberData();
                    parseMemberData(jsonObject);
                }else{
                    Funcs.setMyActionBar(Task_Detail_Activity.this,"网络连接失败");
                }
                App.hideLoadingMask(Task_Detail_Activity.this);
            }
        });

    }

    void parseMemberData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=data.getJSONArray(Const.Key_Resp.Data);
                if(jsonArray.length()==0){
                    Funcs.setMyActionBar(this,"当前没有组员");
                    return;
                }
                for(int i=0;i<jsonArray.length();i++){
                    list1.add(new User(jsonArray.getJSONObject(i)));
                }
                list_person.setAdapter(adapter1);
                pick_person.setVisibility(View.VISIBLE);
            }else{
                Funcs.setMyActionBar(this,"获取数据错误");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean ifback=false;//按返回键是否返回上一个activity，关闭输入框操作
    boolean ifport=false;//是否显示节点设置按钮
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.show_send:
                if(send_menu.getVisibility()==View.VISIBLE){
                    send_menu.setVisibility(View.GONE);
                }else{
                send_menu.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.t1:
                cons_send.setVisibility(View.VISIBLE);
                send_menu.setVisibility(View.GONE);
                ifback=true;
                break;
            case R.id.t2:
                //管理任务节点
                //设置聊天列表节点按钮显示
                send_menu.setVisibility(View.GONE);
                if(ifport==true){
                    ifport=false;
                }else{
                ifport=true;
                }
                adapter2.notifyDataSetChanged();
                break;
            case R.id.button1:
                //发送消息
                cons_send.setVisibility(View.GONE);
                ifback=false;
                postMessage();
                break;
            case R.id.btn_b1:
                //关闭节点编辑界面
                cons_setport.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_b2:
                int position= (int) btn_b2.getTag();
                long mid=list2.get(position).mid;
                String text=btn_b2.getText().toString().trim();
                //判断执行什么操作
                if(text.equals("删除")){
                    //执行节点删除操作
                    manageJD(mid,false);
                }else{
                    //执行节点添加操作
                    manageJD(mid,true);
                }
                break;
            case R.id.t3:
                //前往知识总结页面
                Intent intent=new Intent(this,ZSZJ_Activity.class);
                intent.putExtra("tid",tid);
                startActivity(intent);
                break;
            case R.id.iv_file1:
                //选取图片资源文件
                file_type=1;
                App.showLoadingMask(this);
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                intent1.setType("image/*");
                startActivityForResult(intent1, 200);
                break;
            case R.id.iv_file2:
                //选取其他资源文件
                file_type=2;
                App.showLoadingMask(this);
                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT);
                intent3.addCategory(Intent.CATEGORY_OPENABLE);
                intent3.setType("application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document" +
                        "|application/vnd.ms-powerpoint|application/vnd.openxmlformats-officedocument.presentationml.presentation|application/pdf");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent3.putExtra(Intent.EXTRA_MIME_TYPES,
                            new String[]{Const.DOC,Const.DOCX, Const.PPT, Const.PPTX,Const.PDF});
                }
                startActivityForResult(intent3, 200);

                break;

        }
    }

    private int file_type=1;//文件类型
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode== Activity.RESULT_OK&&data!=null){
            Uri uri=data.getData();
            try {
                InputStream inputStream=this.getContentResolver().openInputStream(uri);
                byte[] bytes=new byte[inputStream.available()];
                inputStream.read(bytes);
                //将读出的字节传到qn服务器
                App.postImgToQnServer(bytes, new Funcs.CallbackInterface() {
                    @Override
                    public void onCallback(Object obj) {
                        if(obj==null){
                            //上传失败
                            Funcs.showtoast(Task_Detail_Activity.this,"上传失败");
                            App.hideLoadingMask(Task_Detail_Activity.this);
                        }else{
                            String qnid= (String) obj;
                            if(file_type==1){
                                files.put("file1",qnid);
                            }else{
                                files.put("file2",qnid);
                            }

                            App.hideLoadingMask(Task_Detail_Activity.this);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        App.hideLoadingMask(Task_Detail_Activity.this);

    }

    //删除添加节点请求
   void manageJD(long mid,boolean t){
        btn_b2.setEnabled(false);
        String url;
       String title=et_title.getText().toString().trim();
       String content=et_content.getText().toString().trim();
       final JSONObject jsonObject=new JSONObject();
       try {
           jsonObject.put(Const.Field_Table_Message.port_title,title);
           jsonObject.put(Const.Field_Table_Message.port_content,content);
       } catch (JSONException e) {
           e.printStackTrace();
       }

       HttpEntity entity=new StringEntity(jsonObject.toString(),HTTP.UTF_8);

       if(t){
            //添加节点
            url=Funcs.servUrlWQ("port","mid="+mid+"&t=添加");
            if(title.length()==0||content.length()==0){
                btn_b2.setEnabled(true);
                Funcs.showtoast(this,"节点信息不完整！");
                return;
            }
        }else{
            //删除节点
            url=Funcs.servUrlWQ("port","mid="+mid+"&t=删除");
        }

       App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               JSONObject js=Funcs.bytetojson(responseBody);
               if(js!=null){
                   try {
                       int code=js.getInt(Const.Key_Resp.Code);
                       if(code==200){
                           Funcs.showtoast(Task_Detail_Activity.this,"发送成功!");
                           getMessageData();
                       }else{
                           Funcs.showtoast(Task_Detail_Activity.this,"错误代码");
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }

               btn_b2.setEnabled(true);
               cons_setport.setVisibility(View.INVISIBLE);
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
               Funcs.showtoast(Task_Detail_Activity.this,"获取数据失败");
               btn_b2.setEnabled(true);
               cons_setport.setVisibility(View.INVISIBLE);
           }
       });
    }


    //提交 消息字符串 和@ 的人的id
    void postMessage(){

        //TODO 推送测试
//        Intent intent=new Intent(this, Test_TS_Activity.class);
//        startActivity(intent);
        String text=edt_text.getText().toString();

        edt_text.setText("");
        at_list.clear();

        if(text.length()==0)return;
        send_message.setEnabled(false);
        //记录需要@的人
        for(int i=0;i<list1.size();i++){
            final int uid=list1.get(i).uid;
            String name=list1.get(i).name;
            if(text.contains("@"+name)){
                //包含此人id,将其添加到集合中
                at_list.add(new HashMap(){{
                    put(Const.Field_Table_User.Uid,uid);
                }});
            }
        }

        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.addmessage,"uid="+App.user.uid+"&tid="+tid);

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Field_Table_Message.text,text);
            jsonObject.put(Const.Field_Table_Message.ats,new JSONArray(at_list));
        } catch (JSONException e) {
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
                            Funcs.showtoast(Task_Detail_Activity.this,"发送成功!");
                            //刷新数据
                            getMessageData();
                        }else{
                            Funcs.showtoast(Task_Detail_Activity.this,"错误代码");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                send_message.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                send_message.setEnabled(true);
                Funcs.showtoast(Task_Detail_Activity.this,"获取数据失败");
            }
        });


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        edt_s=s.toString();
        edt_text.setSelection(edt_s.length());

        if(edt_s.substring(start,count+start).equals("@")){
            //输入字符串包含@弹出成员选择框
            App.showLoadingMask(this);
            //得到聊天组人员名单
            getMember();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    class Container{
        TextView name;//人名
    }

    //选择@的list适配
    class PickListAdapter extends BaseAdapter{

        Container container;

        @Override
        public int getCount() {
            return list1.size();
        }

        @Override
        public Object getItem(int position) {
            return list1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            if(view==null){
                container=new Container();
                view= LayoutInflater.from(Task_Detail_Activity.this).inflate(R.layout.only_name_listitem,null);
                container.name=view.findViewById(R.id.pick_tx);

                view.setTag(container);
            }else container= (Container) view.getTag();

            User data=list1.get(position);
            container.name.setText(data.name);
            return view;
        }
    }

     class MContainer{
        ImageView iv_head;//头像
         TextView tv_name,tv_time,tv_content;//名字，时间，内容
         ImageView iv_file1,iv_file2;//附加文件1,2

         ConstraintLayout cons_port;//节点线
         TextView tv_port;//节点名称

         ImageView iv_port;//节点设置按钮
     }

     class MessageListAdapter extends BaseAdapter{

         MContainer container;
         @Override
         public int getCount() {
             return list2.size();
         }

         @Override
         public Object getItem(int position) {
             return list2.get(position);
         }

         @Override
         public long getItemId(int position) {
             return position;
         }

         @Override
         public boolean isEnabled(int position) {
             return  false;
         }

         @Override
         public View getView(final int position, View view, ViewGroup parent){
             if(view==null){
                 view=LayoutInflater.from(getBaseContext()).inflate(R.layout.message_item,null);
                 container=new MContainer();
                 container.iv_head=view.findViewById(R.id.iv_head);
                 container.iv_file1=view.findViewById(R.id.iv_file1);
                 container.iv_file2=view.findViewById(R.id.iv_file2);

                 container.tv_name=view.findViewById(R.id.tv_name);
                 container.tv_time=view.findViewById(R.id.tv_time);
                 container.tv_content=view.findViewById(R.id.tv_content);

                 container.cons_port=view.findViewById(R.id.cons_port);
                 container.tv_port=view.findViewById(R.id.tv_port);
                 container.iv_port=view.findViewById(R.id.iv_port);
                 view.setTag(container);
             }else container= (MContainer) view.getTag();

             User.Message message=list2.get(position);

             Picasso.with(getBaseContext()).load(Funcs.qnUrl(message.qn_head)).placeholder(R.drawable.message_head).into(container.iv_head);
             //TODO 图片和文件资源获取
             container.tv_name.setText(message.send_people);
             container.tv_time.setText(message.send_time);
             container.tv_content.setText(message.content);

             final String port_title=message.port_title;//节点名称
             if(port_title!=null){
                 container.cons_port.setVisibility(View.VISIBLE);
                 container.tv_port.setText(port_title);
             }else{
                 container.cons_port.setVisibility(View.GONE);
             }

             container.iv_port.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     //显示添加节点或者删除节点对话框
                     managePort(port_title,position);
                 }
             });

             if(ifport){
                 container.iv_port.setVisibility(View.VISIBLE);
                 //设置按钮背景
                 if(port_title!=null){
                     container.iv_port.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                 }else{
                     container.iv_port.setImageDrawable(getResources().getDrawable(R.drawable.circle_white));
                 }
             }else{
                 container.iv_port.setVisibility(View.INVISIBLE);
             }
             //判断文件类型显示及点击显示事件
             if(message.file1!=null){
                 container.iv_file1.setVisibility(View.VISIBLE);
             }else{
                 container.iv_file1.setVisibility(View.INVISIBLE);
             }
             if(message.file2!=null){
                 container.iv_file2.setVisibility(View.VISIBLE);
             }else{
                 container.iv_file2.setVisibility(View.INVISIBLE);
             }

             return view;
         }
     }

     //添加或删除任务节点
     void managePort(String port,int position){

        if(port!=null){
            //是否删除任务节点
            et_title.setText(port);
            et_content.setText(list2.get(position).port_content);
            et_title.setEnabled(false);
            et_content.setEnabled(false);
            btn_b2.setText("删除");
        }else{
            //是否添加任务节点
            et_title.setText("");
            et_content.setText("");
            et_title.setEnabled(true);
            et_content.setEnabled(true);
            btn_b2.setText("添加");
        }

        btn_b2.setTag(position);

        cons_setport.setVisibility(View.VISIBLE);
     }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&ifback){
            cons_send.setVisibility(View.GONE);
            ifback=false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
