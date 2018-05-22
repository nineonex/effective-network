package cc.seedland.inf.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xuchunlei on 2017/11/16.
 */

public class SeedInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        // 请求
        Request request = chain.request();
        String method = request.method();

        // 响应
        if(!Networkit.isNetworkConnected()) {
            throw new IOException(Networkit.getNetworkError());
        }else {
            try{
                Response response = chain.proceed(request);
                String contentType = response.header("Content-Type");

                if(contentType != null) {
                    if(contentType.contains("application/json")) {  // 处理json
                        String raw = response.body().string();
                        BeanWrapper wrapper = GsonHolder.getInstance().fromJson(raw, BeanWrapper.class);
                        if(wrapper.checkSign() && wrapper.code == Networkit.RESPONSE_CODE_SUCCESS) {
                            MediaType mediaType = response.body().contentType();
                            JsonElement bodyObj = wrapper.data == null ? new JsonObject() : wrapper.data;
                            bodyObj.getAsJsonObject().addProperty("raw", raw);
                            ResponseBody body = ResponseBody.create(mediaType, GsonHolder.getInstance().toJson(bodyObj));
                            return response.newBuilder()
                                    .code(response.code())
                                    .body(body)
                                    .build();
                        }else {
                            throw new IOException(wrapper.message);
                        }
                    }else if(contentType.contains("image/jpeg")) { // 处理图片
                        return response;
                    }
                }
                // 其余情况不予处理
                throw new IOException(Networkit.getServerError());

            }catch (Exception e) {
                throw new IOException(Networkit.getServerError());
            }
        }
    }

}
