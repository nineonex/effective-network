package cc.seedland.inf.network;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * 数据包裹类
 * <p>
 *     封装非data数据
 * </p>
 * Created by xuchunlei on 2017/11/16.
 */

class BeanWrapper {
    /** 返回码  */
    @SerializedName(value = "error_code", alternate = {"errcode"})
    public int code;

    /** 返回消息 */
    @SerializedName(value = "error_message", alternate = {"errmsg"})
    public String message;

    // 时间戳
    private long timestamp;
    // 签名
    private String sign;

    /** Json格式的data字段 */
    public JsonElement data;

    /**
     * 检验签名
     * @return
     */
    public boolean checkSign() {
        // TODO: 2018/5/21 客户端检验签名 
//        return ApiUtil.checkSign(timestamp, sign);
        return true;
    }
}
