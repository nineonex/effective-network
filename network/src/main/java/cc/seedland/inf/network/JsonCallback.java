package cc.seedland.inf.network;


import com.lzy.okgo.callback.AbsCallback;

/**
 * Created by xuchunlei on 2017/11/10.
 */

abstract class JsonCallback<T extends BaseBean> extends AbsCallback<T> {

    private Class<T> clazz;

    public JsonCallback(Class<T> clz) {
        this.clazz = clz;
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {

        return GsonHolder.getInstance().fromJson(response.body().string(), clazz);
    }
}
