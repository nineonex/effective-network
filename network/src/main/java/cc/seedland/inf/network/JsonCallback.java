package cc.seedland.inf.network;


import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.Type;

/**
 * Created by xuchunlei on 2017/11/10.
 */

public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Class<T> clazz;
    private Type type;

    public JsonCallback(Class<T> clz) {
        this.clazz = clz;
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {

        if(clazz != null) {
            return GsonHolder.getInstance().fromJson(response.body().string(), clazz);
        }else if(type != null) {
            return GsonHolder.getInstance().fromJson(response.body().string(), type);
        }
        throw new IllegalArgumentException("either clazz or type can not be null");

    }
}
