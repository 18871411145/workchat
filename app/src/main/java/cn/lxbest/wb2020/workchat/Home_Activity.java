package cn.lxbest.wb2020.workchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lxbest.wb2020.workchat.DevTD.TestData1;
import cn.lxbest.wb2020.workchat.Model.User;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;

/**主页activity*/
public class Home_Activity extends AppCompatActivity implements OnRefreshListener, OnLoadMoreListener, View.OnClickListener {


    Button btn_add_task;
    ImageView image_head;

    SmartRefreshLayout refreshLayout;

    RecyclerView recyclerView;
    List<User.Task> list=new ArrayList<>();
    RecyclerViewAdapter adapter=new RecyclerViewAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        init();
        //recyclerview添加布局格式
        StaggeredGridLayoutManager _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        btn_add_task.setOnClickListener(this);
        image_head.setOnClickListener(this);
        getData(false);
    }

    void init(){
        btn_add_task=findViewById(R.id.add_pro_btn);
        image_head=findViewById(R.id.head_imageView);
        refreshLayout=findViewById(R.id.refresh);
        recyclerView=findViewById(R.id.task_recycle);
    }

    //TODO 重新加载跟加载更多url
    void getData(boolean b){
        list=new ArrayList<>();
        String url=null;
        if(b){
            url=Funcs.servUrl(Const.Key_Resp_Path.task);
        }else{
            url=Funcs.servUrl(Const.Key_Resp_Path.task);
        }

        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                parseData(jsonObject);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                JSONObject jsonObject= TestData1.getHomeTask();
                parseData(jsonObject);
                }else Funcs.showtoast(Home_Activity.this,"获取数据失败");
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
                recyclerView.setAdapter(adapter);
               finishRL();

            }else{
                Funcs.showtoast(this,"数据错误");
                finishRL();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //关闭加载
    void finishRL(){
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout){
        getData(false);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//        getData(true);
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.add_pro_btn:
                Intent intent=new Intent(this,Add_Task_Activity.class);
                startActivity(intent);
                break;
            case R.id.head_imageView:
                Intent intent1=new Intent(this,Wode_Activity.class);
                startActivity(intent1);
                break;
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title;
        TextView content;


         public RecyclerViewHolder(@NonNull View itemView) {
             super(itemView);
             imageView=itemView.findViewById(R.id.task_image);
             title=itemView.findViewById(R.id.task_title);
             content=itemView.findViewById(R.id.task_content);
         }
     }


     class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{

         public RecyclerViewAdapter() {
         }

         @NonNull
         @Override
         public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
             View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.taskcardview,null);
             RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(view);
             return recyclerViewHolder;
         }

         @Override
         public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
             final User.Task data=list.get(position);
             Picasso.with(getBaseContext()).load(Funcs.qnUrl(data.qnid)).placeholder(R.drawable.home_head).into(holder.imageView);
             holder.title.setText(data.title);
             holder.content.setText(data.content);

             holder.imageView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent=new Intent(Home_Activity.this,Task_Detail_Activity.class);
                     intent.putExtra(Const.Field_Table_Task.tid,data.id);
                     intent.putExtra(Const.Field_Table_Task.title,data.title);
                     intent.putExtra(Const.Field_Table_Task.content,data.content);
                     startActivity(intent);
                 }
             });
         }

         @Override
         public int getItemCount() {
             return list.size();
         }
     }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
