package win.panyong.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import win.panyong.service.BaseService;
import win.panyong.service.CommonService;
import win.panyong.util.AppCache;
import win.panyong.util.AppException;
import win.panyong.utils.HttpUtil;
import win.panyong.utils.ObjectUtil;

import java.util.HashMap;
import java.util.Map;

@Service("commonService")
public class CommonServiceImpl extends BaseService implements CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Override
    public Map<String, String> getBlinkerAuth() {
        try {
            String clientId = AppCache.getConfigValue("clientId", "");
            String deviceSecret = AppCache.getConfigValue("deviceSecret", "");
            Map<String, String> parameterMap = new HashMap<>() {{
                put("protocol", "mqtt");
                put("authKey", clientId);
                put("miType","light");
//                put("version","1.2.2");
            }};
            String blinkerAuthJsonString = HttpUtil.doHttpGet("https://iot.diandeng.tech/api/v1/user/device/diy/auth", parameterMap);
            JSONObject jsonObject = ObjectUtil.jsonStringToJsonObject(blinkerAuthJsonString);
            if (jsonObject.getInteger("message") == 1000) {
                Map<String, String> result = new HashMap<>();
                JSONObject detail = jsonObject.getJSONObject("detail");
                result.put("clientId", clientId);
                result.put("deviceSecret", deviceSecret);
                result.put("mqttClientId", detail.getString("deviceName"));
                result.put("host", detail.getString("host").split("//")[1]);
                result.put("port", detail.getString("port"));
                result.put("userName", detail.getString("iotId"));
                result.put("password", detail.getString("iotToken"));
                result.put("deviceName", detail.getString("iotId").split("&")[0]);
                result.put("productKey", detail.getString("productKey"));
                result.put("uuid", detail.getString("uuid"));
                result.put("subscribeTopic", "/" + result.get("productKey") + "/" + result.get("deviceName") + "/r");
                result.put("publishTopic", "/" + result.get("productKey") + "/" + result.get("deviceName") + "/s");
                return result;
            } else {
                throw new AppException(jsonObject.getString("detail"));
            }
        } catch (Exception e) {
            if (e instanceof AppException) {
                throw new AppException(e.getMessage());
            } else {
                throw new AppException("登录信息获取失败");
            }
        }
    }
}
