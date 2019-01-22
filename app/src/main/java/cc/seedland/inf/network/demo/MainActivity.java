package cc.seedland.inf.network.demo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cc.seedland.inf.network.GsonHolder;
import cc.seedland.inf.network.JsonCallback;
import cc.seedland.inf.network.Networkit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView getResultV;
    private TextView postResultV;
    private TextView signPostResultV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_perform_get).setOnClickListener(this);
        findViewById(R.id.main_perform_post).setOnClickListener(this);
        findViewById(R.id.main_perform_sign_post).setOnClickListener(this);

        getResultV = findViewById(R.id.main_get_result);
        postResultV = findViewById(R.id.main_post_result);
        signPostResultV = findViewById(R.id.main_sign_post_result);


//        Networkit.init(getApplication(), "test", "hay8qwz");

        // 不验签
        Networkit.init(getApplication());

        final ImageView imv = findViewById(R.id.main_imv);
        OkGo.<Bitmap>get("http://img.sj33.cn/uploads/allimg/201402/7-140223103130591.png")
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        imv.setImageBitmap(response.body());
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_perform_get:
                OkGo.<PayMethodBean>get("https://test-open.seedland.cc/unipay/rest/1.0/pay/supports")
                        .params("channel_id", 282591)
                        .params("merchant_id", "5188611100000000")
                        .execute(new JsonCallback<PayMethodBean>(PayMethodBean.class) {
                            @Override
                            public void onSuccess(Response<PayMethodBean> response) {
                                getResultV.setText(response.body().toString());
                            }
                            @Override
                            public void onError(Response<PayMethodBean> response) {
                                super.onError(response);

                                String msg = "unknown error";
                                if(response != null && response.getException() != null) {
                                    msg = response.getException().getMessage();

                                }
                                getResultV.setText(msg);
                            }
                        });
                break;
            case R.id.main_perform_post:

                OkGo.<CustomerRspBean>post("http://msales-test.seedland.cc:8014/FrameWeb/FrameService/Api.ashx?option=func&funcid=tokerCustomerUnSubscriptionListA")
//                        .tag("external")
//                        .upJson(GsonHolder.getInstance().toJson(req))
                .upJson("{\"_datatype\":\"text\",\"_param\":{\"Level\":\"\",\"MemberID\":\"\",\"ProjectID\":\"c72d570c-bc7c-11e7-8be1-005056bda220\",\"Search\":\"\",\"Status\":\"\",\"TimeFilter\":\"\",\"TimeValue\":\"0\",\"_dataid\":\"mSiteCustomerUnSubscriptionListA_Select\",\"_pageindex\":\"1\",\"_pagesize\":\"10\",\"orderby\":\"order by CatchTime DESC\",\"AuthCompanyID\":\"ede1b679-3546-11e7-a3f8-5254007b6f02\",\"JobCode\":\"xszj\",\"JobOrgID\":\"4D05F2AC-B429-8B8D-AC39-EFFE12FECE1A\",\"OrgID\":\"4D05F2AC-B429-8B8D-AC39-EFFE12FECE1A\",\"ProductID\":\"ee3b2466-3546-11e7-a3f8-5254007b6f02\",\"TeamOrgID\":\"4D05F2AC-B429-8B8D-AC39-EFFE12FECE1A\",\"UserID\":\"aa587a4b-7531-11e7-97a2-005056bda220\",\"projectID\":\"c72d570c-bc7c-11e7-8be1-005056bda220\"}}")
                        .execute(new JsonCallback<CustomerRspBean>(CustomerRspBean.class) {
                            @Override
                            public void onSuccess(Response<CustomerRspBean> response) {
                                postResultV.setText(response.body().customers.size() + " customers");
                            }
                        });

                break;
            case R.id.main_perform_sign_post:
                Map<String, String> params = new HashMap<>();
                params.put("channel_id", "282591");
                params.put("trade_no", "1111111");
                params.put("merchant_id", "2088921759575141");
                params.put("product_name", "商品名称：哈奇礼物");
                params.put("buyer_name", "tom");
                params.put("currency", "CNY");
                params.put("order_amount", "999999");
                params.put("mobile", "15902000000");
                params.put("client_type", "0");
                params.put("notify_url", "http://xxxxx/notify/index");

                Map<String, String> signParams = signPrivate("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBANun6k8jJy" +
                        "2huFCucfSgIrlurwyetPJcvKyUCYrSKvGT5NjVpsy+N/xm5yiqOzj8XysO2CF1A5Fzn/nk5aDBMdx9BM8vEPMsTbzlrhm3eP67IMG+YBtq" +
                        "U6WI/YX4D2guDxSF/p5lAFuqI7O8l2COJTGwYXo3qqx6E6XVuQ99B+dXAgMBAAECgYEAmIGHHxbrFrWXwPy9RfkA4vpEM2DlhPh6TuAhl+6" +
                        "/vibO1vXP74uKV4YirIs0vyYJ9V1DFkemCJDc26XfALPiJ0igzAmSPWe5dQ7mqBf6BaA1X5S/4+Zx4AvVY4rg5hsSWULctyAzANcNRulE98Wy" +
                        "OdZ3C0Ssf2OHmIXIlbu+92ECQQDvePx7gIZO9jjvp4yr3csM/DbSEvfjsjf5BjTQ6neNb9cBIUVr7AIxxeNDS4dRpOwpToRUAiC7VNlEc/4mgj" +
                        "4ZAkEA6tDOjvtFahdoMD8Qv9gc0Hqfo0QTudF+rb4i9f3yAvm04Ecvsc6XR0tzbQVQSAu+8mHAEkQ7UFgLEOL5KpEe7wJBANECV9uzIYZpgOgq5" +
                        "KxcuIxs1awkwhcJxbCjqhVtj0rzAkUKNP0sz/2BKgniMgkgWL70uKpZ8RePxtHoKzqREoECQQDVp556zLixOpELbSahWFOHgjukw3mrVqoMDngjGa" +
                        "hN+sUQWNVV1OMi9M0WwoH0u/NG+ZhZRootpZ6UA+GxUJAzAkEAoJwZrmetqnuBbWL41PhoLrD3yn2BJDcvWuwkVBUEdZl5IsPhcHPvYFm3f2DI++" +
                        "7dn6Ulfft4vMv0qaNAXwNKhw==", params);

                OkGo.<PayCallBean>post("https://test-open.seedland.cc/unipay/rest/1.0/pay?pay_type=alipay.app")
                        .tag("seedland")
                        .upJson(GsonHolder.getInstance().toJson(signParams))
                        .execute(new JsonCallback<PayCallBean>(PayCallBean.class) {
                            @Override
                            public void onSuccess(Response<PayCallBean> response) {
                                signPostResultV.setText(response.body().toString());
                            }

                            @Override
                            public void onError(Response<PayCallBean> response) {
                                super.onError(response);
                                String msg = "unknown error";
                                if(response != null && response.getException() != null) {
                                    msg = response.getException().getMessage();

                                }
                                signPostResultV.setText(msg);
                            }
                        });
                break;
        }
    }

    static Map<String, String> signPrivate(String keyContent, Map<String, String> params) {
        Map<String, String> result = new TreeMap<>();
        result.putAll(params);
        try {
            PrivateKey key = loadPrivateKey(keyContent);
            Signature signer = Signature.getInstance("SHA1WithRSA");
            signer.initSign(key);

            String signQuery = generateQueryString(result, false);
            Log.d("MainActivity","before sign ----> " + signQuery);
            signer.update(signQuery.getBytes());
            result.put("sign", Base64.encodeToString(signer.sign(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("sign failed");
        }
        return result;

    }

    /**
     * 从字符串中加载私钥<br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param keyConent
     * @return
     * @throws Exception
     */
    private static PrivateKey loadPrivateKey(String keyConent) throws Exception
    {
        byte[] buffer = Base64.decode(keyConent, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    /**
     * 生成请求参数地址
     * @param params
     * @return
     */
    private static String generateQueryString(Map<String, String> params, boolean encodeFlag) {
        StringBuilder paramSb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(TextUtils.isEmpty(value)) { // 去除值为空的参数
                continue;
            }
            paramSb.append(encodeFlag ? encode(key) : key);
            paramSb.append("=");
            paramSb.append(encodeFlag ? encode(value) : value);
            paramSb.append("&");
        }
        if(paramSb.length() > 0) {
            paramSb.deleteCharAt(paramSb.length() - 1);
        }

        return paramSb.toString();

    }

    private static String encode(String value) {
        return Uri.encode(value, "UTF-8");
    }

    private void testPassport() {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", "18810057981");
        params.put("password", "12345678");

        OkGo.<LoginBean>post("https://test-open.seedland.cc/passport/api/rest/1.0/login/password?")
                .params(params)
                .execute(new JsonCallback<LoginBean>(LoginBean.class) {
                    @Override
                    public void onSuccess(Response<LoginBean> response) {
                        postResultV.setText(response.body().toString());
                    }

                    @Override
                    public void onError(Response<LoginBean> response) {
                        super.onError(response);

                        String msg = "unknown error";
                        if(response != null && response.getException() != null) {
                            msg = response.getException().getMessage();

                        }
                        postResultV.setText(msg);
                    }
                });
    }
}
