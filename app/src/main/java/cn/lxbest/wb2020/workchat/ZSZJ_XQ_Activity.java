package cn.lxbest.wb2020.workchat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.lxbest.wb2020.workchat.tool.Funcs;

public class ZSZJ_XQ_Activity extends AppCompatActivity {

    TextView tv_title,tv_content;//标题内容文本

    Button btn_xg,btn_sc;//修改删除按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zszj_xq_activity);
        Funcs.setMyActionBar(this,"知识总结");

        //初始化控件

    }
}
