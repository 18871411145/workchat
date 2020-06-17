package cn.lxbest.wb2020.workchat.Model;

import org.json.JSONObject;

import cn.lxbest.wb2020.workchat.tool.Const;
import cn.lxbest.wb2020.workchat.tool.Funcs;

public class ZSZJ {
    public int zid;//id
    public String ztitle;//标题
    public String zcontent;//内容
    public String adder;//添加人
    public long adder_time;//添加时间(long值)

    public ZSZJ(JSONObject jsonObject) {
        try {
            if(Funcs.jsonItemValid(jsonObject, Const.Field_Table_ZSZJ.zid)) zid=jsonObject.getInt(Const.Field_Table_ZSZJ.zid);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_ZSZJ.ztitle)) ztitle=jsonObject.getString(Const.Field_Table_ZSZJ.ztitle);
            if (Funcs.jsonItemValid(jsonObject,Const.Field_Table_ZSZJ.zcontent)) zcontent=jsonObject.getString(Const.Field_Table_ZSZJ.zcontent);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_ZSZJ.adder)) adder=jsonObject.getString(Const.Field_Table_ZSZJ.adder);
            if(Funcs.jsonItemValid(jsonObject,Const.Field_Table_ZSZJ.add_time)) adder_time=jsonObject.getLong(Const.Field_Table_ZSZJ.add_time);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
