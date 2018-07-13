package cc.seedland.inf.network;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xuchunlei on 2017/11/16.
 */

class SeedInterceptor implements Interceptor {

//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public Response intercept(Chain chain) throws IOException {

        if(!Networkit.isNetworkConnected()) {
            throw new IOException(Networkit.getNetworkError());
        }

        // 请求
        Request request = chain.request();
        Object tag = request.tag();
        if(tag != null && tag.toString().equalsIgnoreCase("external")) { // 非sdk请求
            return chain.proceed(request);
        }

        HttpUrl url = request.url();
        String rawUrl = url.toString();
        LogUtil.d("raw url ----> " + rawUrl);
        // 获取原有参数
        Map<String, String> params = new HashMap<>();
        for(String name : url.queryParameterNames()) {
            params.put(name, url.queryParameter(name));
        }
        // 增加公共参数并签名
        String queryString = SignHelper.signPublic(params);

        HttpUrl newUrl = new HttpUrl.Builder()
                .host(url.host())
                .scheme(url.scheme())
                .encodedPath(url.encodedPath())
                .encodedFragment(url.encodedFragment())
                .build();
        String newFullUrl = newUrl.toString().concat("?").concat(queryString);
        LogUtil.d("signed url ----> " + newFullUrl);
        request = request.newBuilder().url(newFullUrl).build();

        // 处理响应
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
            // 抛出默认错误
            String msg = e.getMessage();
            if(msg != null) {
                if(msg.getBytes("GBK").length != msg.length()) { // 中文
                    throw new IOException(msg);
                }
            }
            throw new IOException(Networkit.getDefaultError());
        }
    }

}
