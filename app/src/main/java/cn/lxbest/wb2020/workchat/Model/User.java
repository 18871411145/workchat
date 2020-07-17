package cn.lxbest.wb2020.workchat.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;

/**用户基本信息，其他模型共有部分*/
public class User {

    public int uid;
    public String qnid;
    public String name;
    public int sex;//1：男； 2：女
    public int age;//年龄
    public String email;
    public String mobile;
    public int permission;//权限
    public String company;//公司
    public String department;//部门
    public String position;//职位
    public long last_login;//最后登陆时间



    public User(){
    }
    public User(JSONObject jsonObject){
        try{
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.Uid))uid=jsonObject.getInt(Const.Field_Table_User.Uid);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.qnid))qnid=jsonObject.getString(Const.Field_Table_User.qnid);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.Name))name=jsonObject.getString(Const.Field_Table_User.Name);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.Perm)) permission=jsonObject.getInt(Const.Field_Table_User.Perm);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.Email)) email=jsonObject.getString(Const.Field_Table_User.Email);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.phone)) mobile=jsonObject.getString(Const.Field_Table_User.phone);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.Sex)) sex=jsonObject.getInt(Const.Field_Table_User.Sex);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.company)) company=jsonObject.getString(Const.Field_Table_User.company);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.bumen)) department=jsonObject.getString(Const.Field_Table_User.bumen);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.zhiwei)) position=jsonObject.getString(Const.Field_Table_User.zhiwei);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_User.LastLogin)) last_login=jsonObject.getLong(Const.Field_Table_User.LastLogin);
        }catch (Exception e){

        }
    }

    /**主页项目列表模型*/
    public static class Task {
        public int id;//task id
        public  String qnid;//图片id
        public int adder;//添加任务组的人的id
        public long addtime;//添加时间
        public String title;//任务主题
        public String content;//任务内容
        public List<String> ms=new ArrayList();//参与人的id集合


        public Task(JSONObject jsonObject){
            try{
                if(Funcs.jsonItemValid(jsonObject, Const.Field_Table_Task.tid)) id=jsonObject.getInt(Const.Field_Table_Task.tid);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Task.qnid)) qnid=jsonObject.getString(Const.Field_Table_Task.qnid);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Task.adder)) adder=jsonObject.getInt(Const.Field_Table_Task.adder);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Task.addtime))addtime=jsonObject.getLong(Const.Field_Table_Task.addtime);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Task.title))title=jsonObject.getString(Const.Field_Table_Task.title);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Task.content))content=jsonObject.getString(Const.Field_Table_Task.content);
                if(Funcs.jsonItemValid(jsonObject,"member")){
                    String member=jsonObject.getString("member");
                    String[] strs=member.split(",");
                    for(String s:strs){
                        ms.add(s);
                    }
                }
            }catch (Exception e){

            }
        }
    }

    /**消息列表模型*/
    public static class Message {
        public long mid;//消息id
        public String qn_head;//头像的七牛id
        public String file1;//图片id
        public String file2;//文件id

        public String send_people;//发起人姓名
        public  String send_time;//发送时间
        public String content;//发送文字内容


        //如果该聊天记录存在节点
        public String port_title;//节点名称
        public String port_content;//节点内容

        public Message(JSONObject jsonObject){
            try {
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.mid))mid=jsonObject.getLong(Const.Field_Table_Message.mid);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.qn_head))qn_head=jsonObject.getString(Const.Field_Table_Message.qn_head);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.file1))file1=jsonObject.getString(Const.Field_Table_Message.file1);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.file2))file2=jsonObject.getString(Const.Field_Table_Message.file2);

                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.send_people)) send_people=jsonObject.getString(Const.Field_Table_Message.send_people);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.send_time))send_time=jsonObject.getString(Const.Field_Table_Message.send_time);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.text))content=jsonObject.getString(Const.Field_Table_Message.text);


                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.port_title)) port_title=jsonObject.getString(Const.Field_Table_Message.port_title);
                if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_Message.port_content)) port_content=jsonObject.getString(Const.Field_Table_Message.port_content);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
