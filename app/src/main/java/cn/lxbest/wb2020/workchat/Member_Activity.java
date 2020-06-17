package cn.lxbest.wb2020.workchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lxbest.wb2020.workchat.DevTD.TestData1;
import cn.lxbest.wb2020.workchat.Model.User;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;

/**员工申请界面*/
public class Member_Activity extends AppCompatActivity {

    int uid=0;
    ListView listView;
    List<User> list=new ArrayList<>();
    ListAdapter adapter=new ListAdapter();

    int page=-1;
    Button btn_agree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        Funcs.setMyActionBar(this,"员工申请");

        Intent intent=getIntent();
        if(intent.hasExtra(Const.Field_Table_User.Uid)){
        uid=getIntent().getIntExtra(Const.Field_Table_User.Uid,0);
        }

        listView=findViewById(R.id.list_view);
        btn_agree=findViewById(R.id.button);

        btn_agree.setVisibility(View.VISIBLE);
        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //批准选中员工
                if(page>=0){
                    postMember(page);
                }else
                    Funcs.showtoast(Member_Activity.this,"请先选择员工");
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                page=position;
                adapter.notifyDataSetChanged();
            }
        });

        getData();
    }


    void postMember(int i){
        btn_agree.setEnabled(false);
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.agree_member,"uid="+list.get(i).uid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                parseData(jsonObject);
                }

                btn_agree.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(Member_Activity.this,"批准失败，请检查网络");
                btn_agree.setEnabled(true);
            }
        });
    }

    void parseData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                Funcs.showtoast(this,"批准成功");
                //刷新列表
                getData();
            }else{
                Funcs.showtoast(this,"批准失败");
            }
        }catch (Exception e){

        }
    }

    void getData(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.task,"uid="+uid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                parseResData(jsonObject);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getMemberData();
                    parseResData(jsonObject);
                }else{
                    Funcs.showtoast(Member_Activity.this,"连接失败，请检查网络连接");
                }
            }
        });
    }

    void parseResData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=data.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<jsonArray.length();i++){
                    list.add(new User(jsonArray.getJSONObject(i)));
                }
                //适配器设置
                listView.setAdapter(adapter);
            }else{

            }
        }catch (Exception e){

        }
    }

    class Container{
        TextView text_name,text_mobile,text_department,text_position;
    }

    class ListAdapter extends BaseAdapter {

        Container container=new Container();

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view==null){
                container=new Container();
                view= LayoutInflater.from(Member_Activity.this).inflate(R.layout.member_list_item,null);
                container.text_name=view.findViewById(R.id.member_name);
                container.text_mobile=view.findViewById(R.id.member_mobile);
                container.text_department=view.findViewById(R.id.member_department);
                container.text_position=view.findViewById(R.id.member_position);
                view.setTag(container);
            }else container= (Container) view.getTag();
            User data=list.get(position);

            container.text_name.setText(data.name);
            container.text_mobile.setText(data.mobile);
            container.text_department.setText(data.department);
            container.text_position.setText(data.position);

            //改变颜色也表示该行被选中。
            container.text_name.setTextColor(getResources().getColor(R.color.deepgray));
            container.text_mobile.setTextColor(getResources().getColor(R.color.deepgray));
            container.text_department.setTextColor(getResources().getColor(R.color.deepgray));
            container.text_position.setTextColor(getResources().getColor(R.color.deepgray));
            if(position==page){
            container.text_name.setTextColor(getResources().getColor(R.color.black));
                container.text_mobile.setTextColor(getResources().getColor(R.color.black));
                container.text_department.setTextColor(getResources().getColor(R.color.black));
                container.text_position.setTextColor(getResources().getColor(R.color.black));
            }

            return view;
        }
    }
}
