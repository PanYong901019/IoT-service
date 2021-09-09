package win.panyong.util.authority;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
    /**
     * token过期时间，单位秒
     */
    private Integer tokenExpireTime;
    /**
     * token加密密钥
     */
    private String secretKey;


    public Integer getTokenExpireTime() {
        return tokenExpireTime;
    }

    public void setTokenExpireTime(Integer tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
