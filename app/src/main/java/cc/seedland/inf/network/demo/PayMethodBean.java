package cc.seedland.inf.network.demo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cc.seedland.inf.network.BaseBean;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/23 14:03
 * 描述 ：
 **/
public class PayMethodBean extends BaseBean {

    @SerializedName("expire_time")
    public int expireTime;
    @SerializedName("support_pay_type")
    public List<MethodItemBean> methods;

    public static class MethodItemBean {
        @SerializedName("pay_type")
        public String type;
        @SerializedName("pay_name")
        public String name;
        @SerializedName("merchant_id")
        public String mechantId;
        public String toast;
        public int status;
        @SerializedName("pay_ico")
        public String icon;
    }
}
