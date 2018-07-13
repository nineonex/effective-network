package cc.seedland.inf.network.demo;

import cc.seedland.inf.network.BaseBean;

/**
 * Created by xuchunlei on 2017/11/16.
 */

public class LoginBean extends BaseBean {

    public int uid;
    public String mobile;
    public String nickname;
    /** 登录token */
    public String token;

    @Override
    public String toString() {
        return "uid=" + uid + "\n" + "mobile=" + mobile + "\n" + "nickname=" + nickname + "\n" + "token=" + token;
    }
}
