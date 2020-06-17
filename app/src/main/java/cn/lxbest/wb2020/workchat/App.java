package cn.lxbest.wb2020.workchat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import cn.lxbest.wb2020.workchat.Model.User;
import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;
import cz.msebera.android.httpclient.Header;

public class App extends Application {

    private static final String TAG = "Init";

    public static String deviceId;

    public static int screenWidth, screenHeight;

    public static AsyncHttpClient http;

    public static Context context;

    public static User user = new User();

    public static int env= Const.Env.DEV_TD;

    public static  UploadManager uploadManager;
    public static HashMap<String, Object> qnToken = new HashMap<>(); //token, time

    public App() {
        http=new AsyncHttpClient();
        http.addHeader("dataType", "json");
        http.addHeader("User-Agent", "jpark");
//        http.setTimeout(1500);
        http.setMaxRetriesAndTimeout(3, 1500);//该设置需要最低1s才有效

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //一下变量必须等类的构造完成后才有实例调用，不然会报空指针
        context=getApplicationContext();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        initCloudChannel(this);
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "init cloudchannel success");
                //获取deviceIds
                deviceId=pushService.getDeviceId();
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    public static Activity getActivity(Context context){
        while(!(context instanceof  Activity) && context instanceof ContextWrapper){
            context = ((ContextWrapper)context).getBaseContext();
        }
        return (Activity)context;
    }
    public static ViewGroup getRootView(Activity act){
        return act.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public static void showLoadingMask(ViewGroup parent){
        if (parent==null) return;
        ViewGroup v = (ViewGroup) App.getActivity(parent.getContext()).getLayoutInflater().inflate(R.layout.view_loading_mask, null);
        v.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
//        v.setTop(0); v.setLeft(0);
        parent.addView(v);
    }

    //弹出读取loading界面
    public static void showLoadingMask(Activity act) {
        if (act==null) return;
        ViewGroup p = getRootView(act);
        showLoadingMask(p);
    }

    //取消读取loading界面
    public static void hideLoadingMask(Activity act) {
        if (act==null) return;
        ViewGroup p = getRootView(act);
        hideLoadingMask(p);
    }
    public static void hideLoadingMask(ViewGroup parent) {
        if (parent==null) return;
        parent.removeView(parent.findViewById(R.id.cl_loading_mask));
    }

    //登录时将json数据放入user对象
    public static void putJsonToUser(JSONObject data){
        try{
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.Uid)) App.user.uid=data.getInt(Const.Field_Table_User.Uid);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.Name)) App.user.name=data.getString(Const.Field_Table_User.Name);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.Sex)) App.user.sex=data.getInt(Const.Field_Table_User.Sex);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.Age)) App.user.age=data.getInt(Const.Field_Table_User.Age);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.phone)) App.user.mobile=data.getString(Const.Field_Table_User.phone);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.Email)) App.user.email=data.getString(Const.Field_Table_User.Email);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.department)) App.user.department=data.getString(Const.Field_Table_User.department);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.position)) App.user.position=data.getString(Const.Field_Table_User.position);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.Perm)) App.user.permission=data.getInt(Const.Field_Table_User.Perm);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.LastLogin)) App.user.last_login=data.getLong(Const.Field_Table_User.LastLogin);
        }catch (Exception e){

        }
    }

    //得到储存类SharedPreferences
    public  static SharedPreferences sharedPreferences(String tag) {
        return context.getSharedPreferences(Const.domain+"."+tag, Context.MODE_PRIVATE);
    }

    public static boolean isFirstRun() {
        SharedPreferences prefs = sharedPreferences("app");
        boolean fr = prefs.getBoolean("first_run", true);
        return fr;
    }

    public static void setFirstRun() {
        sharedPreferences("app").edit().putBoolean("first_run", false).commit();
    }

    //将user里的信息存到preference里以备下次登录直接使用
    public static void putUserToPreference(){
        SharedPreferences.Editor editor=sharedPreferences(Const.Key_SharedPref.Account).edit();
        editor.putInt(Const.Field_Table_User.Uid,App.user.uid);
        editor.putString(Const.Field_Table_User.AvatarQnid,App.user.qnid);
        editor.putString(Const.Field_Table_User.Name,App.user.name);
        editor.putInt(Const.Field_Table_User.Perm,App.user.permission);
        editor.putString(Const.Field_Table_User.department,App.user.department);
        editor.putString(Const.Field_Table_User.position,App.user.position);
        editor.putString(Const.Field_Table_User.Email,App.user.email);
        editor.putString(Const.Field_Table_User.phone,App.user.mobile);
        editor.putInt(Const.Field_Table_User.Sex,App.user.sex);
        editor.putInt(Const.Field_Table_User.Age,App.user.age);
        editor.putLong(Const.Field_Table_User.LastLogin,App.user.last_login);
        editor.commit();
    }

    //清除preference
    public static void cleanUserPref() {
        sharedPreferences(Const.Key_SharedPref.Account).edit().clear().commit();
    }

    //是否可以向preference取数据
    public static boolean refreshUserFromPreference(){
        Long lastlogin=sharedPreferences(Const.Key_SharedPref.Account).getLong(Const.Field_Table_User.LastLogin,-1);
        if(new Date().getTime()-lastlogin>=Const.TS_1_HOUR){
            return false;
        }else{
            App.user.uid=sharedPreferences(Const.Key_SharedPref.Account).getInt(Const.Field_Table_User.Uid,-1);
            App.user.qnid=sharedPreferences(Const.Key_SharedPref.Account).getString(Const.Field_Table_User.AvatarQnid,null);
            App.user.name=sharedPreferences(Const.Key_SharedPref.Account).getString(Const.Field_Table_User.Name,null);
            App.user.sex=sharedPreferences(Const.Key_SharedPref.Account).getInt(Const.Field_Table_User.Sex,-1);
            App.user.age=sharedPreferences(Const.Key_SharedPref.Account).getInt(Const.Field_Table_User.Age,-1);
            App.user.mobile=sharedPreferences(Const.Key_SharedPref.Account).getString(Const.Field_Table_User.phone,null);
            App.user.email=sharedPreferences(Const.Key_SharedPref.Account).getString(Const.Field_Table_User.Email,null);
            App.user.department=sharedPreferences(Const.Key_SharedPref.Account).getString(Const.Field_Table_User.department,null);
            App.user.position=sharedPreferences(Const.Key_SharedPref.Account).getString(Const.Field_Table_User.position,null);
            App.user.permission=sharedPreferences(Const.Key_SharedPref.Account).getInt(Const.Field_Table_User.Perm,-1);
            App.user.last_login=sharedPreferences(Const.Key_SharedPref.Account).getLong(Const.Field_Table_User.LastLogin,-1);
            return true;
        }

    }

    //退出登录
    public static void logout(Activity activity){
        Intent intent=new Intent(activity,Login_activity.class);
        //清除App里面的用户数据
        App.user=new User();
        cleanUserPref();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    //external storage permission request
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //检查权限并打开
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



    public static boolean qnTokenLegal() {
        return !(Funcs.isNull(qnToken.get("token")) || (new Date().getTime() - (long) qnToken.get("time") >= 1 * 60 * 60 * 1000));
    }

    public static void getQnTokenIfExpired(final Funcs.CallbackInterface callbackInterface) {
        if (!qnTokenLegal()) {
            http.get(Funcs.servUrl(Const.Key_Resp_Path.QnToken), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String tk = null;
                    try {
                        JSONObject jo = Funcs.bytetojson(responseBody);
                        if (jo != null) {
                            tk = jo.getString("tk");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    qnToken.put("token", tk);
                    qnToken.put("time", new Date().getTime());
                    if (callbackInterface != null) callbackInterface.onCallback(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (callbackInterface != null) callbackInterface.onCallback(null);
                }
            });
        } else if (callbackInterface != null) callbackInterface.onCallback(null);
    }


    //向七牛传图片
    public static void postImgToQnServer(final byte[] bytes, final Funcs.CallbackInterface callbackInterface) {
        if (uploadManager==null) uploadManager = new UploadManager();
        getQnTokenIfExpired(new Funcs.CallbackInterface() {
            @Override
            public void onCallback(Object obj) {
                if (!App.qnTokenLegal()){
                    Funcs.showtoast(context,"Token 错误，请重启APP重试");
                    if (callbackInterface != null) callbackInterface.onCallback(null);
                }else {
                    uploadManager.put(bytes, null, App.qnToken.get("token").toString(), new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()){
                                Funcs.showtoast(context,"上传成功!");
                                String qnid = null;
                                try {
                                    qnid=info.response.getString("key");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (callbackInterface != null) callbackInterface.onCallback(qnid);
                            }else {
                                if (callbackInterface != null) callbackInterface.onCallback(null);
                                Funcs.showtoast(context,"上传失败，请刷新重试");
                            }
                        }
                    }, null);
                }
            }
        });
    }




}
