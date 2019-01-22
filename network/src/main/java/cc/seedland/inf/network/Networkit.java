package cc.seedland.inf.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/*
 * <pre>
 *     作者: xuchunlei
 *     联系方式: xuchunlei@seedland.cc / QQ:22003950
 *     时间: 2018/05/21
 *     描述: 网络套件
 * </pre>
 */
public class Networkit {

    public static final String TAG = "Networkit";

    /** 请求返回码-成功 */
    public static final int RESPONSE_CODE_SUCCESS = 0;

    /** OkGo参数-连接／读写等待时间 */
    private static final long WAITTING_MILLISECONDS = 10000;

    private static final Networkit INSTANCE = new Networkit();

    private static Application APP;
    private static String ERROR_NETWORK;
    private static String ERROR_SERVER;
    private static String ERROR_DEFAULT;
    private static String HOST;

    private Networkit() {

    }

    public static void init(Application app) {
        init(app, null);
    }


    public static void init(Application app, Map<String, String> signParams) {
        if(app == null) {
            throw new IllegalArgumentException("your application is in wrong state, please restart and retry");
        }
        if(APP == null) {
            APP = app;
            HOST = app.getString(R.string.host);
            ERROR_NETWORK = APP.getString(R.string.error_network);
            ERROR_SERVER = APP.getString(R.string.error_server);
            ERROR_DEFAULT = APP.getString(R.string.error_default);

            // 初始化OkGo
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if(signParams != null && !signParams.isEmpty()) { // 有签名参数
                builder.addInterceptor(new SignInterceptor());
                SignHelper.init(app, signParams);
            }
            builder.addInterceptor(new SeedInterceptor());
            if(BuildConfig.API_ENV) {
                // 日志支持
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("seeldand-network");
                loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
                loggingInterceptor.setColorLevel(Level.INFO);
                builder.addInterceptor(loggingInterceptor);
            }

            builder.readTimeout(WAITTING_MILLISECONDS, TimeUnit.MILLISECONDS)      // 全局读取超时时间
                    .writeTimeout(WAITTING_MILLISECONDS, TimeUnit.MILLISECONDS)     // 全局写入超时时间
                    .connectTimeout(WAITTING_MILLISECONDS, TimeUnit.MILLISECONDS);  // 全局连接超时时间

            // 信任所有的Https证书
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.put("X-proxy-Version", "Seedland Android Networkit(" + BuildConfig.VERSION_NAME + ")");

            OkGo.getInstance().init(app)
                    .setOkHttpClient(builder.build())
                    .setCacheMode(CacheMode.NO_CACHE)
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    .setRetryCount(3)
                    .addCommonHeaders(headers);
        }

    }

    /**
     * 获取完整地址
     * @param path
     * @return
     */
    public static String generateFullUrl(String path) {
        return HOST.concat(path);
    }

    /**
     * 网络是否连接
     * @return
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) APP.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getNetworkError() {
        return ERROR_NETWORK;
    }

    public static String getServerError() {
        return ERROR_SERVER;
    }

    public static String getDefaultError() {
        return ERROR_DEFAULT;
    }
}
