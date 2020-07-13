package cn.lxbest.wb2020.workchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.OptionsPickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lxbest.wb2020.workchat.DevTD.TestData1;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Reg_activity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_name,edt_mobile, edit_position;

    TextView text_company,text_department;

    Button btn_reg;

    OptionsPickerView pickerView;

    int state=1;

    List<String> list_dep=new ArrayList<>();//所有部门集合
    List<String> list_com=new ArrayList<>();//所有注册公司

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Funcs.setMyActionBar(this,"注册");
        setContentView(R.layout.reg_activity);

        init();

        text_department.setOnClickListener(this);
        btn_reg.setOnClickListener(this);
        text_company.setOnClickListener(this);

        pickerView=new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v){
                if(state==2){
                    text_department.setText(list_dep.get(options1));
                }else {
                    text_company.setText(list_com.get(options1));
                }

            }
        }).build();



    }


    void init(){
        edt_name=findViewById(R.id.name_EditText);
        edt_mobile=findViewById(R.id.mobile_EditText);
        text_company=findViewById(R.id.company_tv);
        text_department=findViewById(R.id.department_TextView);
        edit_position=findViewById(R.id.edit_TextView);

        btn_reg=findViewById(R.id.btn_reg);
    }

    //参数为公司名称
    void getData(final String c){
        btnEnable(false);
        list_dep=new ArrayList<>();
        String url=null;
        if(c==null){
            url=Funcs.servUrl(Const.Key_Resp_Path.gs_bumen);
        }else{
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.gs_bumen,"gs="+c);
        }
        App.http.get(url, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseData(jsonObject);
                }
                btnEnable(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){

                if(App.env== Const.Env.DEV_TD){
                    if(c!=null){
                        parseData(TestData1.getDap_Data());
                    }else{
                        parseData(TestData1.getcom_Data());
                    }

                }else{
                    Funcs.showtoast(Reg_activity.this,"获取部门信息失败");
                }

                btnEnable(true);
            }
        });
    }



    void parseData(JSONObject data){

        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray jsonArray=data.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    if(jsonObject.has(Const.Field_Table_User.bumen)){
                        list_dep.add(jsonObject.getString(Const.Field_Table_User.bumen));
                    }else{
                        list_com.add(jsonObject.getString(Const.Field_Table_User.company));
                    }
                }

                showpick();

            }else{
                Funcs.showtoast(this,"获取失败");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //显示pickview，此方法必须在state定义后使用
    void showpick(){
        if(state==1){
            pickerView.setPicker(list_com);
        }else{
            pickerView.setPicker(list_dep);
        }
        pickerView.show();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.company_tv:
                state=1;
                //得到公司列表
                if(list_com.size()==0){
                getData(null);
                }else{
                    showpick();
                }
                break;
            case R.id.department_TextView:
                //得到公司下部门
                if(text_company.getText().toString().length()==0){
                    Funcs.showtoast(this,"请先选择公司");
                }else{
                    state=2;
                    getData(text_company.getText().toString());
                }
                break;
            case R.id.btn_reg:
                //注册按钮
                postRegData();
                break;
        }

    }



    void postRegData(){
        String url=Funcs.servUrl(Const.Key_Resp_Path.reg);
        String name=edt_name.getText().toString();
        String mobile=edt_mobile.getText().toString();
        String company=text_company.getText().toString();
        String dep=text_department.getText().toString();
        String pos= edit_position.getText().toString();


            if(name.length()==0){
            Funcs.showtoast(this,"姓名不能为空");
            return;
            }
            if(mobile.length()==0){
                Funcs.showtoast(this,"电话不能为空");
                return;
            }

            if(company.length()==0){
                Funcs.showtoast(this,"公司不能为空");
                return;
            }

            if(dep.length()==0){
                Funcs.showtoast(this,"部门不能为空");
                return;
            }

            if(pos.length()==0){
                Funcs.showtoast(this,"职位不能为空");
                return;
            }
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Field_Table_User.Name,name);
            jsonObject.put(Const.Field_Table_User.phone,mobile);
            jsonObject.put(Const.Field_Table_User.company,company);
            jsonObject.put(Const.Field_Table_User.bumen,dep);
            jsonObject.put(Const.Field_Table_User.zhiwei,pos);

            HttpEntity entity=new StringEntity(jsonObject.toString(), HTTP.UTF_8);

            App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject jsonObject1=Funcs.bytetojson(responseBody);
                    if(jsonObject1!=null){
                        parseRegData(jsonObject1);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(App.env==Const.Env.DEV_TD){
                        Funcs.showtoast(Reg_activity.this,"注册成功,请等待部门管理审核");
                    }else Funcs.showtoast(Reg_activity.this,"注册失败");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void parseRegData(JSONObject data){
        try {
            int code=data.getInt(Const.Key_Resp.Code);
            if(code==200){
                Funcs.showtoast(Reg_activity.this,"注册成功,请等待部门管理审核");
            }else Funcs.showtoast(Reg_activity.this,"注册失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //按键可用
    void btnEnable(boolean b){
        btn_reg.setEnabled(b);

        text_company.setEnabled(b);
        text_department.setEnabled(b);
    }
}
