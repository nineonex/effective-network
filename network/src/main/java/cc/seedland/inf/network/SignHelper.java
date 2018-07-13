package cc.seedland.inf.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/22 16:02
 * 描述 ：
 **/
class SignHelper {

    // MD5算法生成字符串时补位使用
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final Map<String, String> FIXED_PARAMS = new HashMap<>();
    private static String KEY; // 用于签名公共参数的key

    private SignHelper() {

    }

    static void init(Context context, String channel, String key) {
        if(TextUtils.isEmpty(channel)) {
            throw new IllegalArgumentException("channel should not be null or empty");
        }

        if(TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key should not be null or empty");
        }

        FIXED_PARAMS.put("channel", channel);
        FIXED_PARAMS.put("device_type", Build.MANUFACTURER + "-" + Build.MODEL);
        FIXED_PARAMS.put("device_mac", getMacAddress());
        FIXED_PARAMS.put("device_imei", getDeviceId(context));
        KEY = key;

    }

    /**
     * 公共参数签名
     * @return 签名后的query参数字符串,格式为key1=value1&key2=value2&...
     */
    static String signPublic(Map<String, String> params) {
        Map<String, String> result = new TreeMap<>();
        result.putAll(FIXED_PARAMS);
        result.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("client_ip", getLocalIpAddress());
        result.putAll(params);

        // 用于签名的字符串
        String signQuery = generateQueryString(result, false).concat("&").concat(KEY);

        return generateQueryString(result, true)
                .concat("&")
                .concat(encode("auth"))
                .concat("=")
                .concat(encode(MD5(signQuery)));
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

    private static String getDeviceId(Context context) {
        // 优先从设置获取
        String deviceId = obtainFromSettings(context);

        // 再次生成
        if(deviceId.length() == 0) {
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtil.d("DeviceUtil.getDeviceId:android_id---->" + androidId);
            try {
                if(androidId != null && androidId.length() != 0) {
                    deviceId = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                }else {
                    final String macAddress = getMacAddress();
                    if(macAddress.length() != 0) {
                        deviceId = UUID.nameUUIDFromBytes(macAddress.getBytes("utf8")).toString();
                    }
                }

            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                LogUtil.w("failed to generate device id");
            }
            if(deviceId.length() == 0) { // 使用随机UUID
                deviceId = UUID.randomUUID().toString();
            }

            // 保存
            saveToSettings(context, deviceId);
        }

        return deviceId;
    }

    /**
     * 获取设备IP地址，不唯一，因此动态获取
     */
    private static String getLocalIpAddress() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            LogUtil.e("DeviceUtil.getLocalIpAddress" + ex.toString());
        }
        return ip;
    }

    // 获取网卡地址
    private static String getMacAddress() {
        String address = "";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res = new StringBuilder();
                for (byte b : macBytes) {
                    res.append(String.format("%02X:", b));
                }

                if (res.length() > 0) {
                    res.deleteCharAt(res.length() - 1);
                }
                address = res.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: 2017/11/13 此处应上报日志
        }
        LogUtil.d("SignHelper.getMacAddress:find device mac address ----> " + address);
        return address;
    }

    private static void saveToSettings(Context context, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("seed-network", Context.MODE_PRIVATE).edit();
        editor.putString("device_id", value);
        editor.apply();
    }

    private static String obtainFromSettings(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences("seed-network", Context.MODE_PRIVATE);
        return prefs.getString("device_id", "");
    }

    private static String MD5(String s) {

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
