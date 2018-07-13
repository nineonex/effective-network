package cc.seedland.inf.network;

import com.lzy.okgo.model.Response;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/23 11:02
 * 描述 ：
 **/
public class SeedCallback<B extends BaseBean> extends JsonCallback<B> {

    public SeedCallback(Class<B> clz) {
        super(clz);
    }

    @Override
    public void onSuccess(Response<B> response) {

    }
}
