package cc.seedland.inf.network;


import com.lzy.okgo.callback.AbsCallback;

import org.json.JSONObject;


/**
 * Created by xuchunlei on 2017/11/10.
 */

public abstract class JsonCallback extends AbsCallback<JSONObject> {

    @Override
    public JSONObject convertResponse(okhttp3.Response response) throws Throwable {
        String json = response.body().string();
        return new JSONObject(json);
    }
}
