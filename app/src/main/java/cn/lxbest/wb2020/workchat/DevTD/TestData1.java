package cn.lxbest.wb2020.workchat.DevTD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.lxbest.wb2020.workchat.tool.Const;

/**
 * 测试数据
 * */
public class TestData1 {

    /**登录信息*/
    public static JSONObject getLoginData(){
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(Const.Key_Resp.Code,200);
            JSONObject data=new JSONObject();

            data.put(Const.Field_Table_User.Uid,1212121212);
            data.put(Const.Field_Table_User.qnid,1212121212);
            data.put(Const.Field_Table_User.Name,"小华");
            data.put(Const.Field_Table_User.Sex,1);
            data.put(Const.Field_Table_User.Age,22);
            data.put(Const.Field_Table_User.phone,"13112345678");
            data.put(Const.Field_Table_User.Email,"1231231231@qq.com");
            data.put(Const.Field_Table_User.bumen,"设计部");
            data.put(Const.Field_Table_User.zhiwei,"美术设计");
            data.put(Const.Field_Table_User.LastLogin,new Date().getTime());

            jsonObject.put(Const.Key_Resp.Data,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;
    }

    /**得到对公司所有部门名称*/
    public static JSONObject getDap_Data(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
            List<HashMap<String,Object>> list=new ArrayList<>();

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.bumen,"销售部");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.bumen,"设计部");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.bumen,"财务部");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.bumen,"管理部");
            }});

            JSONArray jsonArray=new JSONArray(list);
            jsonObject.put(Const.Key_Resp.Data,jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**得到公司集合*/
    public static JSONObject getcom_Data(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
            List<HashMap<String,Object>> list=new ArrayList<>();

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.company,"baidu");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.company,"beirui");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.company,"ali");
            }});

            JSONArray jsonArray=new JSONArray(list);
            jsonObject.put(Const.Key_Resp.Data,jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**主页task测试数据*/

    public static  JSONObject getHomeTask(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);

            List<HashMap<String,Object>> list=new ArrayList<>();

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Task.tid,1231231231);
                put(Const.Field_Table_Task.title,"任务1");
                put(Const.Field_Table_Task.content,"这次开会决定要开一个新的项目。。。");

            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Task.tid,1231231231);
                put(Const.Field_Table_Task.title,"任务2");
                put(Const.Field_Table_Task.content,"这次开会决定要开一个新的项目。。。这次开会决定要开一个新的项目。。。");

            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Task.tid,1231231231);
                put(Const.Field_Table_Task.title,"任务3");
                put(Const.Field_Table_Task.content,"这次开会决定要开一个新的项目。。。");

            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Task.tid,1231231231);
                put(Const.Field_Table_Task.title,"任务4");
                put(Const.Field_Table_Task.content,"这次开会决定要开一个新的项目。。。这次开会决定要开一个新的项目。。。");

            }});

            JSONArray jsonArray=new JSONArray(list);
            jsonObject.put(Const.Key_Resp.Data,jsonArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    //验证码时候ok WiFi连接是否符合要求
    public static JSONObject getOK() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    //人员名单
    public static JSONObject getMemberData(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);

            List<HashMap<String,Object>> list=new ArrayList<>();

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.Name,"jack");
                put(Const.Field_Table_User.phone,"13112345678");
                put(Const.Field_Table_User.bumen,"销售部");
                put(Const.Field_Table_User.zhiwei,"售后办理");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.Name,"tom");
                put(Const.Field_Table_User.phone,"13112345678");
                put(Const.Field_Table_User.bumen,"设计部");
                put(Const.Field_Table_User.zhiwei,"程序设计");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_User.Name,"marry");
                put(Const.Field_Table_User.phone,"13112345678");
                put(Const.Field_Table_User.bumen,"财务部");
                put(Const.Field_Table_User.zhiwei,"资金规划");
            }});

            JSONArray jsonArray=new JSONArray(list);
            jsonObject.put(Const.Key_Resp.Data,jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //获取当前任务的消息列表
    public static JSONObject getMessage() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);

            List<HashMap<String,Object>> list=new ArrayList<>();

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Message.mid,1);
               put(Const.Field_Table_Message.send_people,"张三");
               put(Const.Field_Table_Message.send_time,"2020/2/23-12:23");
               put(Const.Field_Table_Message.text,"@小王,下午到我办公室来一趟。");
               put(Const.Field_Table_Message.port_title,"任务开始");
                put(Const.Field_Table_Message.port_content,"个部门开始准备相关材料");
                put(Const.Field_Table_Message.file1,"123");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Message.mid,2);
                put(Const.Field_Table_Message.send_people,"小王");
                put(Const.Field_Table_Message.send_time,"2020/2/23-12:40");
                put(Const.Field_Table_Message.text,"知道了。");
                put(Const.Field_Table_Message.file1,"123");
                put(Const.Field_Table_Message.file2,"123");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Message.mid,3);
                put(Const.Field_Table_Message.send_people,"张三");
                put(Const.Field_Table_Message.send_time,"2020/2/23-12:23");
                put(Const.Field_Table_Message.text,"@小王,下午到我办公室来一趟。");
                put(Const.Field_Table_Message.port_title,"任务初步完成");
                put(Const.Field_Table_Message.port_content,"准备测试上架");
                put(Const.Field_Table_Message.file2,"123");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_Message.mid,4);
                put(Const.Field_Table_Message.send_people,"小王");
                put(Const.Field_Table_Message.send_time,"2020/2/23-12:40");
                put(Const.Field_Table_Message.text,"知道了。");
            }});


            JSONArray jsonArray=new JSONArray(list);
            jsonObject.put(Const.Key_Resp.Data,jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    //仅仅代表响应成功
    public static JSONObject JustOk() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    //知识总结测试数据
    public static JSONObject getZSZJData() {
        JSONObject jsonObject=new JSONObject();
        List<HashMap<String,Object>> list=new ArrayList<>();

        try {
            jsonObject.put(Const.Key_Resp.Code,200);

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_ZSZJ.zid,1);
                put(Const.Field_Table_ZSZJ.ztitle,"知识总结1");
                put(Const.Field_Table_ZSZJ.zcontent,"1231");
                put(Const.Field_Table_ZSZJ.adder,"小王");
                put(Const.Field_Table_ZSZJ.add_time,new Date().getTime());
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_ZSZJ.zid,2);
                put(Const.Field_Table_ZSZJ.ztitle,"知识总结2");
                put(Const.Field_Table_ZSZJ.zcontent,"123123");
                put(Const.Field_Table_ZSZJ.adder,"小张");
                put(Const.Field_Table_ZSZJ.add_time,new Date().getTime());
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_ZSZJ.zid,3);
                put(Const.Field_Table_ZSZJ.ztitle,"知识总结3");
                put(Const.Field_Table_ZSZJ.zcontent,"asd");
                put(Const.Field_Table_ZSZJ.adder,"小明");
                put(Const.Field_Table_ZSZJ.add_time,new Date().getTime());
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_Table_ZSZJ.zid,4);
                put(Const.Field_Table_ZSZJ.ztitle,"知识总结4");
                put(Const.Field_Table_ZSZJ.zcontent,"zxcxz");
                put(Const.Field_Table_ZSZJ.adder,"小红");
                put(Const.Field_Table_ZSZJ.add_time,new Date().getTime());
            }});

            jsonObject.put(Const.Key_Resp.Data,new JSONArray(list));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return  jsonObject;
    }
}
