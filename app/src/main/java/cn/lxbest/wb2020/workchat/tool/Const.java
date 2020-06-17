package cn.lxbest.wb2020.workchat.tool;

import java.text.SimpleDateFormat;

/**
 *app常量储存类
 * */
public class Const {

    public static final String domain = "cn.lxbest.wb2019.jpark";
    public static final String server = "http://192.168.31.83:8080/workchat";
    public static final String qnserver = "http://qnyeyimg.lxbest.cn";
    public static final String contentType="application/json";


    public final static long TS_1_HOUR = 60 * 60 * 1000;
    public final static long TS_1_DAY = 24 * 60 * 60 * 1000;
    public final static long TS_1_WEEK = 7 * 24 * 60 * 60 * 1000;

    public static SimpleDateFormat Year_Month_Day=new SimpleDateFormat("yyyy-MM-dd");

    public static final String DOC = "application/msword";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String XLS = "application/vnd.ms-excel application/x-excel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PPT = "application/vnd.ms-powerpoint";
    public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String PDF = "application/pdf";


    public static String YZM="yzm";//验证码

    public static class Env {
        /**
         * 开发环境+客户端假数据
         */
        public static final int DEV_TD = 1;
        /**
         * 开发环境+服务器ok
         */
        public static final int DEV_OK = 2;
        /**
         * 生产环境
         */
        public static final int PROD = 3;

    }


    public static class Key_SharedPref {
        public static String Account = "acc"; //用于保存账户信息的key
    }

    public static class Key_Resp {
        public static String Code = "code";
        public static String Data = "data";

    }
    /**请求url*/
    public static class Key_Resp_Path{

        //向服务器拿qntoken
        public static String QnToken = "/qnuptok";
        //注册
        public static String reg="reg";
        //登录
        public static String login="login";
        //获取公司所有部门信息(带参数得到指定部门下面职位信息)
        public static String department="department";
        //获取主页task
        public static String task="task";
        //获取部门成员信息
        public static String member="member";
        //同意该员工的申请
        public static String agree_member="member";
        //得到相关成员的监督
        public static String supervision="supervision";
        //个人主页修改
        public static String person="person";
        //添加任务请求
        public static String addtask="addtask";

        //发送消息请求
        public static String sendmessage="sendmessage";
    }

    /**权限表*/
    public static class Perm{

    //部门
    public static final int charge=29000;//部门总管理
    public static final int charge_support=28000;//部门副管理

    //员工
    public static final int staff=10000; //普通员工

}

    /**消息相关字段*/
    public static class Field_Table_Message {
        public static String mid="mid";//消息id

        public static String qn_head="qn_head";//头像qnid
        public static String file1="file1";//图片id
        public static String file2="file2";//文件id

        public static String send_people="send_people";//谁发送的
        public static String send_time="send_time";//发送时间
        public static String text="text";//文本

        public static String ats="ats";//需要@人的集合

        //如果该聊天记录存在节点
        public static String port_title="port_title";//节点名称
        public static String port_content="port_content";//节点内容
    }



    /**用户相关字段表*/
    public static class Field_Table_User{

        //基本User表字段
        public static final String Uid = "uid";//用户id
        public static final String AvatarQnid = "avatar";//用户头像qnid
        public static final String Name = "name";//用户姓名
        public static final String phone = "phone"; //手机
        public static final String Perm="perm";//权限
        public static final String Email = "email";//电子邮件
        public static final String Sex="sex";//性别
        public static final String Age="age";//年龄

        public static final String company="company";//所处公司
        public static final String department="department";//所属部门
        public static final String position="position";//所在职位
        public static final String LastLogin="lastlogin";//最后登录时间

    }


    /**任务信息*/
    public static class Field_Table_Task{
         public static final String tid="tid";//任务id
         public static final String qnid="qnid";//
         public static final String title="title";
         public static final String content="content";

    }

    /**知识总结*/
    public static class Field_Table_ZSZJ{
        public static String zid="zid";//总结id
        public static String ztitle="ztitle";//总结标题
        public static String zcontent="zcontent";//总结内容
        public static String adder="adder";//添加的人
        public static String add_time="add_time";//添加时间
    }

}
