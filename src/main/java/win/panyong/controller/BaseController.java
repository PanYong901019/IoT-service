package win.panyong.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;
import win.panyong.service.CommonService;
import win.panyong.util.AppCache;
import win.panyong.util.AppException;
import win.panyong.util.RedisUtil;
import win.panyong.util.Result;
import win.panyong.util.authority.bean.AuthUser;
import win.panyong.utils.ObjectUtil;
import win.panyong.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class BaseController {
    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    protected static Integer FAIL = 0;
    protected static Integer OK = 1;
    @Autowired
    protected AppCache appCache;
    @Autowired
    protected RedisUtil redisUtil;
    @Autowired
    protected HttpSession session;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected CommonService commonService;

    protected String getRequestBody() {
        try {
            return StringUtil.InputStreamToString(request.getInputStream());
        } catch (IOException e) {
            throw new AppException("参数错误");
        }
    }

    protected String getParameter(String parameterKey) {
        return WebUtils.findParameterValue(request, parameterKey);
    }

    protected AuthUser getAuthUser() {
        return (AuthUser) request.getAttribute("authUser");
    }

    protected Long getAuthUserId() {
        return getAuthUser() == null ? null : getAuthUser().getId();
    }


    @RequestMapping(value = "/")
    String index() {
        return "redirect:/heartbeat";
    }

    @ResponseBody
    @RequestMapping(value = "/heartbeat", produces = "application/json;charset=UTF-8")
    String heartbeat() throws MqttException {
        Result.Builder resultBuilder = Result.builder();
        resultBuilder.rspCode(OK).rspInfo("success")
                .putData("serverName", "IoT-service")
                .putData("requestParameter", request.getParameterMap().isEmpty() ? null : ObjectUtil.objectToJsonString(request.getParameterMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue()[0]))));
        return resultBuilder.buildJsonString();
    }

    @ResponseBody
    @RequestMapping(value = "/checkConfig", produces = "application/json;charset=UTF-8")
    String checkConfig() {
        Map<String, String> config = AppCache.getSystemConfig();
        Result.Builder resultBuilder = Result.builder();
        resultBuilder.rspCode(OK).rspInfo("success").putData("config", config);
        return resultBuilder.buildJsonString();
    }

    @ResponseBody
    @RequestMapping(value = "/refreshConfig", produces = "application/json;charset=UTF-8")
    String refreshConfig() {
        AppCache.initSystemConfig("app.properties");
        Result.Builder resultBuilder = Result.builder();
        resultBuilder.rspCode(OK).rspInfo("success").putData("config", AppCache.getSystemConfig());
        return resultBuilder.buildJsonString();
    }

}


