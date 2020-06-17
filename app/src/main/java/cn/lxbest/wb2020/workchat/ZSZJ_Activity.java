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
import cn.lxbest.wb2020.workchat.Model.ZSZJ;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;

/**知识总结*/
public class ZSZJ_Activity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    List<ZSZJ> list=new ArrayList<>();
    ZAdapter adapter=new ZAdapter();//适配器
    Button btn_add;//添加知识总结按钮

    int tid=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_activity);

        Funcs.setMyActionBar(this,"知识总结");

        listView=findViewById(R.id.list_view);
        btn_add=findViewById(R.id.button);

        btn_add.setVisibility(View.VISIBLE);
        btn_add.setText("添加知识点");

        btn_add.setOnClickListener(this);

        Intent intent=getIntent();
        tid=intent.getIntExtra("tid",0);

        listView.setAdapter(adapter);

        getData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击进入详情页
                Intent intent=new Intent(ZSZJ_Activity.this,ZSZJ_XQ_Activity.class);
                int zid=list.get(position).zid;
                intent.putExtra("zid",zid);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        //添加知识总结

    }

    //得到知识总结
    void getData(){
        String url=Funcs.servUrlWQ("zszj","tid="+tid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getZSZJData();
                    parseData(jsonObject);
                }else{
                Funcs.showtoast(ZSZJ_Activity.this,"数据获取失败");
                }
            }
        });
    }

    void parseData(JSONObject jsonObject){
        try{
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=jsonObject.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject js=jsonArray.getJSONObject(i);
                    list.add(new ZSZJ(js));
                }
                adapter.notifyDataSetChanged();

            }else{
                Funcs.showtoast(ZSZJ_Activity.this,"错误代码");
            }

        }catch (Exception e){

        }
    }

    class Container{
        TextView tv_title;//标题
    }

    class ZAdapter extends BaseAdapter{
        Container container;
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
                view= LayoutInflater.from(getBaseContext()).inflate(R.layout.text_list_item,null);
                container=new Container();
                container.tv_title=view.findViewById(R.id.tv_title);
                view.setTag(container);
            }else container= (Container) view.getTag();

            ZSZJ zszj=list.get(position);
            container.tv_title.setText(zszj.ztitle);
            return view;
        }
    }
}
