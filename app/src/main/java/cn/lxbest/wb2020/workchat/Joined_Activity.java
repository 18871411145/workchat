package cn.lxbest.wb2020.workchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

public class Joined_Activity extends AppCompatActivity {

    int uid=0;
    ListView listView;
    List<User.Task> list=new ArrayList<>();

    ListAdapter adapter=new ListAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        Funcs.setMyActionBar(this,"我的讨论组");
        uid=getIntent().getIntExtra(Const.Field_Table_User.Uid,0);

        listView=findViewById(R.id.list_view);

        getData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Joined_Activity.this,Task_Detail_Activity.class);
                intent.putExtra(Const.Field_Table_Task.tid,list.get(position).id);
                intent.putExtra(Const.Field_Table_Task.title,list.get(position).title);
                intent.putExtra(Const.Field_Table_Task.content,list.get(position).content);
                startActivity(intent);
            }
        });
    }

    void getData(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.task,"uid="+uid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                parseData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getHomeTask();
                    parseData(jsonObject);
                }else{
                    Funcs.showtoast(Joined_Activity.this,"连接失败，请检查网络连接");
                }
            }
        });
    }

    void parseData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=data.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<jsonArray.length();i++){
                    list.add(new User.Task(jsonArray.getJSONObject(i)));
                }
                //适配器设置
                listView.setAdapter(adapter);
            }else{

            }
        }catch (Exception e){

        }
    }

    class Container{
        TextView text_title=null;
    }

    class ListAdapter extends BaseAdapter{

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
                view= LayoutInflater.from(Joined_Activity.this).inflate(R.layout.task_list_item,null);
                container.text_title=view.findViewById(R.id.title);
                view.setTag(container);
            }else container= (Container) view.getTag();

            User.Task data=list.get(position);
            container.text_title.setText(data.title);

            return view;
        }
    }
}
