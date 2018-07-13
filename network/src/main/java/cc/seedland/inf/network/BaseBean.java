package cc.seedland.inf.network;

import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 作者 ： 徐春蕾
 * 联系方式 ： xuchunlei@seedland.cc / QQ:22003950
 * 时间 ： 2018/05/23 10:22
 * 描述 ：请求数据对象基类
 **/
public class BaseBean {
    public String raw;
    private Bundle args;

    /**
     * 转换成参数
     * @return
     */
    public Bundle toArgs() {

        if(args == null) {
            args = new Bundle();
            Field[] fields = getClass().getFields();
            for(Field field : fields){
                if(!field.getDeclaringClass().isAssignableFrom(BaseBean.class) && !field.isSynthetic()) { // isSynthetic()方法排除"$change"属性
                    try {
                        String key = field.getName();
                        if(!key.equals("serialVersionUID")) {
                            Type type = field.getType();
                            if(type.equals(String.class)) {
                                args.putString(key, field.get(this) + "");
                            } else if(type.equals(Integer.TYPE)) {
                                args.putInt(key, field.getInt(this));
                            } else {
                                args.putString(key, field.get(this) + "");
                            }

                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return args;
    }

    @Override
    public String toString() {
        LogUtil.d(Networkit.TAG, "BaseBean.toString:" + raw);
        return raw;
    }

}
