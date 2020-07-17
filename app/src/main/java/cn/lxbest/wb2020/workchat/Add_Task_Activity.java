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

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

    TextView text_b, text_j, text_z;

    ImageView image1,image2,image3;

    Button btn_add_proj;//确认添加

    List<User> list_b =new ArrayList<>();//储存所有部门人员
    List<User> list_j =new ArrayList<>();//储存所有监督人员
    List<User> list_z =new ArrayList<>();//储存所有须知人员

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
        text_b =findViewById(R.id.member);
        text_j =findViewById(R.id.supervision);
        text_z =findViewById(R.id.justknow);

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

        text_b.setOnClickListener(this);
        text_j.setOnClickListener(this);
        text_z.setOnClickListener(this);

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
                    String before= text_b.getText().toString().trim();
                    //判断是否已含有存在的人
                    boolean ifhave=false;
                        if(before.contains(list_b.get(position).name)){
                            ifhave=true;
                        }
                    if(!ifhave){
                        if(before.length()==0){
                            text_b.setText(list_b.get(position).name);
                        }else{
                            text_b.setText(before+","+ list_b.get(position).name);
                        }
                        final int uid= list_b.get(position).uid;
                        list1.add(new HashMap(){{
                            put(Const.Field_Table_User.Uid,uid);
                        }});
                    }

                }else if(state==2){
                    String before= text_j.getText().toString().trim();
                    //判断是否已含有存在的人
                    boolean ifhave=false;
                    if(before.contains(list_j.get(position).name)){
                        ifhave=true;
                    }
                    if(!ifhave){
                        if(before.length()==0){
                            text_j.setText(list_j.get(position).name);
                        }else{
                            text_j.setText(before+","+ list_j.get(position).name);
                        }
                        final int uid= list_j.get(position).uid;
                        list2.add(new HashMap(){{
                            put(Const.Field_Table_User.Uid,uid);
                        }});
                    }

                }else {
                    String before= text_z.getText().toString().trim();
                    boolean ifhave=false;
                    if(before.contains(list_z.get(position).name)){
                        ifhave=true;
                    }
                    if(!ifhave){
                        if(before.length()==0){
                            text_z.setText(list_z.get(position).name);
                        }else{
                            text_z.setText(before+","+ list_z.get(position).name);
                        }

                        final int uid= list_z.get(position).uid;
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
                if(list_b.size()==0){
                    getData();
                }else{
                    listView.setAdapter(adapter);
                    showPick();
                }
                break;
            case R.id.supervision:
                //点击显示监督名单点名
                state=2;
                if(list_j.size()==0){
                    getData();
                }else{
                    showPick();
                    listView.setAdapter(adapter);
                }
                break;
            case R.id.justknow:
                //点击显示须知名单点名
                state=3;
                if(list_z.size()==0){
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
                    text_b.setText("");
                    list1=new ArrayList<>();
                }else if(state==2){
                    text_j.setText("");
                    list2=new ArrayList<>();
                }else{
                    text_z.setText("");
                    list3=new ArrayList<>();
                }
                break;
            case R.id.add_prj_btn:
                //提交任务
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
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.addtask,"uid="+App.user.uid);

        String title=et_title.getText().toString().trim();
        String content=et_content.getText().toString().trim();

        if(title.length()==0){
            Funcs.showtoast(this,"标题不能为空");
            return;
        }

        if(content.length()==0){
            Funcs.showtoast(this,"内容不能为空");
            return;
        }

        if(list1.size()==0){
            Funcs.showtoast(this,"成员不能为空");
            return;
        }

        JSONObject jsonObject=new JSONObject();


        try {
            //标题内容
            jsonObject.put(Const.Field_Table_Task.title,title);
            jsonObject.put(Const.Field_Table_Task.content,content);
            //人员id
            jsonObject.put("b",new JSONArray(list1));
            jsonObject.put("j",new JSONArray(list2));
            jsonObject.put("z",new JSONArray(list3));
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
                byte[] buffer=new byte[4096];
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                int n=0;
                while ((n=inputStream.read(buffer))!=-1){
                    output.write(buffer,0,n);
                }
                byte[] bytes=output.toByteArray();
                inputStream.close();
                output.close();
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
        if(state==1){//获取部门人员
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.member,"m="+1+"&b="+App.user.department+"&c="+App.user.company);
            list_b =new ArrayList<>();
        }else if(state==2){//获取监督人员
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.member,"m="+2+"&c="+App.user.company);
            list_j =new ArrayList<>();
        }else{
            //获取须知人员
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.member,"m="+3+"&c="+App.user.company);
            list_z =new ArrayList<>();
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
                        list_b.add(new User(jsonArray.getJSONObject(i)));
                    }else if(state==2){
                        list_j.add(new User(jsonArray.getJSONObject(i)));
                    }else
                        list_z.add(new User(jsonArray.getJSONObject(i)));
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
                return list_b.size();
            }else if(state==2){
                return list_j.size();
            }else return list_z.size();
        }

        @Override
        public Object getItem(int position) {
            if(state==1) {
                return list_b.get(position);
            }else if(state==2){
                return list_j.get(position);
            }else return list_z.get(position);
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
                 container.name.setText(list_b.get(position).name);
             }else if(state==2){
                 container.name.setText(list_j.get(position).name);
             }else
                 container.name.setText(list_z.get(position).name);

            return view;
        }
    }
}
