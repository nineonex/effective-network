package cc.seedland.inf.network.demo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/07/30 10:03
 * 描述 ：
 **/
public class CustomerRspBean {
    @SerializedName("List")
    public List<CustomerBean> customers;
}
