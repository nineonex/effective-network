package cc.seedland.inf.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/07/30 08:59
 * 描述 ：
 **/
class SignInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(!Networkit.isNetworkConnected()) {
            throw new IOException(Networkit.getNetworkError());
        }

        // 请求
        Request request = chain.request();
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
        return chain.proceed(request);
    }
}
