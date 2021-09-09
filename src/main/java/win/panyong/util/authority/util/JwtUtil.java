package win.panyong.util.authority.util;


import win.panyong.util.AppException;
import win.panyong.util.authority.AuthConfig;
import win.panyong.util.authority.bean.AuthUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Autowired
    private AuthConfig authConfig;


    /**
     * @param @param  token
     * @param @return 设定文件
     * @return boolean    返回类型
     * @throws
     * @Title: verify
     * @Description: 校验token是否正确
     */
    public boolean verify(String token) {
        try {
            String secret = getClaim(token, Constant.ACCOUNT) + authConfig.getSecretKey();
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            throw new AppException("权限异常");
        }
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return
     */
    public String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * @param @param  account
     * @param @param  role
     * @param @param  currentTimeMillis
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: sign
     * @Description: 生成签名，并设置过期时间
     */
    public String sign(AuthUser authUser, String currentTimeMillis) {
        String account = authUser.getPhoneNumber();

        // 帐号加JWT私钥加密
        String secret = account + authConfig.getSecretKey();
        // 此处过期时间，单位：毫秒,设为7天
        Date date = new Date(System.currentTimeMillis() + authConfig.getTokenExpireTime() * 1000);
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim(Constant.USERID, authUser.getId() + "")
                .withClaim(Constant.ACCOUNT, account)
                .withClaim(Constant.NAME, authUser.getName())
                .withClaim(Constant.COMPANYID, authUser.getCompanyId() + "")
                .withClaim(Constant.DEPTID, authUser.getDeptId() + "")
                .withClaim(Constant.ROLE, authUser.getRoleNames())
                .withClaim(Constant.ROLEID, authUser.getRoleIds())
                .withClaim(Constant.CURRENT_TIME_MILLIS, currentTimeMillis)
                .withClaim(Constant.NOTICECOUNT, authUser.getNoticeCount() + "")
                .withExpiresAt(date)
                .sign(algorithm);
    }


}
