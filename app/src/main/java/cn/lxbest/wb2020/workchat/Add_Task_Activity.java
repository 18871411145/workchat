package cn.lxbest.wb2020.workchat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import com.bigkoo.pickerview.OptionsPickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lxbest.wb2020.workchat.DevTD.TestData1;
import cn.lxbest.wb2020.workchat.Model.User;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Add_Task_Activity extends AppCompatActivity implements View.OnClickListener {

    EditText et_title,et_content;

    TextView text_member,text_supervision,text_justknow;

    ImageView image1,image2,image3;

    Button btn_add_proj;//确认添加

    List<User> list_m=new ArrayList<>();//储存所有部门人员
    List<User> list_s=new ArrayList<>();//储存所有监督人员
    List<User> list_o=new ArrayList<>();//储存所有须知人员

    List<HashMap> list1=new ArrayList<>();//选中部门人员id
    List<HashMap> list2=new ArrayList<>();//选中监督人员id
    List<HashMap> list3=new ArrayList<>();//选中须知人员id

    int state=1;

    ConstraintLayout const_list;
    TextView text_title;
    ListView listView;
    Button btn_clear;

    AddListAdapter adapter=new AddListAdapter();
    //存放从qn返回的qnid
    Map<String,String> qnids=new HashMap<>();

    void init(){
        et_title=findViewById(R.id.et_title);
        et_content=findViewById(R.id.et_content);
        text_member=findViewById(R.id.member);
        text_supervision=findViewById(R.id.supervision);
        text_justknow=findViewById(R.id.justknow);

        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);

        btn_add_proj=findViewById(R.id.add_prj_btn);

        const_list=findViewById(R.id.const_list);
        text_title=findViewById(R.id.add_title);
        listView=findViewById(R.id.MD_ListView);
        btn_clear=findViewById(R.id.clear_btn);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task_activity);
        Funcs.setMyActionBar(this,"添加任务");

        init();

        App.verifyStoragePermissions(this);

        text_member.setOnClickListener(this);
        text_supervision.setOnClickListener(this);
        text_justknow.setOnClickListener(this);

        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);

        btn_add_proj.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        //点击选取人员,在对应栏显示名字(三个点名表公用一个listview)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(state==1){
                    //如果该栏中已有名字就在其后加
                    String before=text_member.getText().toString().trim();
                    //判断是否已含有存在的人
                    boolean ifhave=false;
                        if(before.contains(list_m.get(position).name)){
                            ifhave=true;
                        }
                    if(!ifhave){
                        if(before.length()==0){
                            text_member.setText(list_m.get(position).name);
                        }else{
                            text_member.setText(before+","+list_m.get(position).name);
                        }
                        final int uid=list_m.get(position).uid;
                        list1.add(new HashMap(){{
                            put(Const.Field_Table_User.Uid,uid);
                        }});
                    }

                }else if(state==2){
                    String before=text_supervision.getText().toString().trim();
                    //判断是否已含有存在的人
                    boolean ifhave=false;
                    if(before.contains(list_s.get(position).name)){
                        ifhave=true;
                    }
                    if(!ifhave){
                        if(before.length()==0){
                            text_supervision.setText(list_s.get(position).name);
                        }else{
                            text_supervision.setText(before+","+list_s.get(position).name);
                        }
                        final int uid=list_s.get(position).uid;
                        list2.add(new HashMap(){{
                            put(Const.Field_Table_User.Uid,uid);
                        }});
                    }

                }else {
                    String before=text_justknow.getText().toString().trim();
                    boolean ifhave=false;
                    if(before.contains(list_o.get(position).name)){
                        ifhave=true;
                    }
                    if(!ifhave){
                        if(before.length()==0){
                            text_justknow.setText(list_o.get(position).name);
                        }else{
                            text_justknow.setText(before+","+list_o.get(position).name);
                        }

                        final int uid=list_o.get(position).uid;
                        list3.add(new HashMap(){{
                            put(Const.Field_Table_User.Uid,uid);
                        }});
                    }

                }

                const_list.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.member:
                //点击显示员工名单点名
                state=1;
                if(list_m.size()==0){
                    getData();
                }else{
                    listView.setAdapter(adapter);
                    showPick();
                }
                break;
            case R.id.supervision:
                //点击显示监督名单点名
                state=2;
                if(list_s.size()==0){
                    getData();
                }else{
                    showPick();
                    listView.setAdapter(adapter);
                }
                break;
            case R.id.justknow:
                //点击显示须知名单点名
                state=3;
                if(list_o.size()==0){
                    getData();
                }else{
                    showPick();
                    listView.setAdapter(adapter);
                }
                break;
            case R.id.clear_btn:
                //清空之前选中的人
                const_list.setVisibility(View.INVISIBLE);
                if(state==1){
                    text_member.setText("");
                    list1=new ArrayList<>();
                }else if(state==2){
                    text_supervision.setText("");
                    list2=new ArrayList<>();
                }else{
                    text_justknow.setText("");
                    list3=new ArrayList<>();
                }
                break;
            case R.id.add_prj_btn:
                //提交人员名单
                postData();
                break;
            case R.id.image1:
                //获取图片1
                getImage(1);
                break;
            case R.id.image2:
                //获取图片2
                getImage(2);
                break;
            case R.id.image3:
                //获取图片3
                getImage(3);
                break;
        }
    }


    //提交总数据
    void postData(){
        btn_add_proj.setEnabled(false);
        String url=Funcs.servUrl(Const.Key_Resp_Path.addtask);

        String title=et_title.getText().toString().trim();
        String content=et_content.getText().toString().trim();

        JSONObject jsonObject=new JSONObject();

        try {
            //标题内容
            jsonObject.put(Const.Field_Table_Task.title,title);
            jsonObject.put(Const.Field_Table_Task.content,content);
            //人员id
            jsonObject.put("member",new JSONArray(list1));
            jsonObject.put("supervision",new JSONArray(list2));
            jsonObject.put("justknow",new JSONArray(list3));
            //图片id
            jsonObject.put("images",new JSONObject(qnids));

            HttpEntity entity=new StringEntity(jsonObject.toString(),HTTP.UTF_8);

            App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject js=Funcs.bytetojson(responseBody);
                    if(js!=null){
                        try {
                            int code=js.getInt(Const.Key_Resp.Code);
                            if(code==200){
                                Funcs.showtoast(Add_Task_Activity.this,"添加成功");
                            }else{
                                Funcs.showtoast(Add_Task_Activity.this,"错误代码");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    btn_add_proj.setEnabled(true);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    btn_add_proj.setEnabled(true);
                    Funcs.showtoast(Add_Task_Activity.this,"获取数据失败，请检查网络连接");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    ImageView img=null;
    int position=0;

    void getImage(int i){
        if(i==1){
            position=1;
            img=image1;
        }else if(i==2){
            position=2;
            img=image2;
        }else{
            position=3;
            img=image3;
        }
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"请选择图片"),22);
        App.showLoadingMask(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==22&&resultCode== Activity.RESULT_OK&&data!=null){
            Uri uri=data.getData();
            try {
                InputStream is=this.getContentResolver().openInputStream(uri);
                Bitmap bitmap= BitmapFactory.decodeStream(is);
                img.setImageBitmap(bitmap);
                InputStream inputStream=this.getContentResolver().openInputStream(uri);
                byte[] bytes=new byte[is.available()];
                inputStream.read(bytes);
                //将读出的字节传到qn服务器
                App.postImgToQnServer(bytes, new Funcs.CallbackInterface() {
                    @Override
                    public void onCallback(Object obj) {
                        if(obj==null){
                            //上传失败
                            Funcs.showtoast(Add_Task_Activity.this,"上传失败");
                        }else{
                            String qnid= (String) obj;
                            qnids.put("image"+position,qnid);

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        App.hideLoadingMask(Add_Task_Activity.this);
    }


 //TODO url需要设置
    void getData(){
        String url=null;
        if(state==1){//获取自己部门成员信息
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.member,"department="+App.user.department);
            list_m=new ArrayList<>();
        }else if(state==2){//获取需要知道的部门
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.member,"department="+App.user.department);
            list_s=new ArrayList<>();
        }else{
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.member,"department="+App.user.department);
            list_o=new ArrayList<>();
        }


        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                parseData(jsonObject);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getMemberData();
                    parseData(jsonObject);
                }else Funcs.showtoast(Add_Task_Activity.this,"连接超时，请检查网络连接");
            }
        });
    }

    void parseData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=data.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<jsonArray.length();i++){
                    if(state==1){
                        list_m.add(new User(jsonArray.getJSONObject(i)));
                    }else if(state==2){
                        list_s.add(new User(jsonArray.getJSONObject(i)));
                    }else
                        list_o.add(new User(jsonArray.getJSONObject(i)));
                }

                showPick();
                listView.setAdapter(adapter);
            }else{
                Funcs.showtoast(this,"数据错误");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //显示点名表
    void showPick(){
        const_list.setVisibility(View.VISIBLE);
        if(state==1){
            text_title.setText("请选择员工");
        }else if(state==2){
            text_title.setText("请选择监督");
        }else
            text_title.setText("请选择须知部门");
    }

    class Container{
        public TextView name;
    }

    class AddListAdapter extends BaseAdapter{

        Container container;
        @Override
        public int getCount() {
            if(state==1) {
                return list_m.size();
            }else if(state==2){
                return list_s.size();
            }else return list_o.size();
        }

        @Override
        public Object getItem(int position) {
            if(state==1) {
                return list_m.get(position);
            }else if(state==2){
                return list_s.get(position);
            }else return list_o.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            if(view==null){
                container=new Container();
                view= LayoutInflater.from(Add_Task_Activity.this).inflate(R.layout.only_name_listitem,null);
                container.name=view.findViewById(R.id.pick_tx);
                view.setTag(container);
            }else container= (Container) view.getTag();
             if(state==1){
                 container.name.setText(list_m.get(position).name);
             }else if(state==2){
                 container.name.setText(list_s.get(position).name);
             }else
                 container.name.setText(list_o.get(position).name);

            return view;
        }
    }
}
