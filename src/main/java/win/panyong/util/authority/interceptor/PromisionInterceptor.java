package win.panyong.util.authority.interceptor;

import win.panyong.util.AppException;
import win.panyong.util.RedisUtil;
import win.panyong.util.Result;
import win.panyong.util.authority.AuthConfig;
import win.panyong.util.authority.Promision;
import win.panyong.util.authority.bean.AuthUser;
import win.panyong.util.authority.util.Constant;
import win.panyong.util.authority.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import win.panyong.utils.DateUtil;
import win.panyong.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by pan on 2019/2/12 11:20 AM
 */
public class PromisionInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PromisionInterceptor.class);
    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Promision authority = ((HandlerMethod) handler).getMethod().getAnnotation(Promision.class);

            if (authority != null) {
                String token = request.getHeader(Constant.REQUEST_AUTH_HEADER);
                if (!StringUtil.invalid(token)) {
                    //开始token校验
                    if (jwtUtil.verify(token)) {//验证成功，验证失败抛出异常，交由上层进行捕获
                        String account = jwtUtil.getClaim(token, Constant.ACCOUNT);
                        String role = jwtUtil.getClaim(token, Constant.ROLE);
//                    String deviceType = NetworkUtil.getDeviceType();
                        String deviceType = "PC";
                        String tokenKey = Constant.PREFIX_AUTH_TOKEN + deviceType + "_" + account;
                        if (token.equals(redisUtil.get(tokenKey))) {//根据redis中的token控制过期时间，2个小时之内没有调用记录，则改token失效
                            //刷新redis token过期时间
                            if (deviceType.equals("PC") || deviceType.equals("Unknown")) {
                                redisUtil.set(tokenKey, token);
                                redisUtil.expire(tokenKey, authConfig.getTokenExpireTime());
                            }
                            Long userId = Long.parseLong(jwtUtil.getClaim(token, Constant.USERID));
                            Long deptId = Long.parseLong(jwtUtil.getClaim(token, Constant.DEPTID));
                            Long companyId = Long.parseLong(jwtUtil.getClaim(token, Constant.COMPANYID));
                            String roleIds = jwtUtil.getClaim(token, Constant.ROLEID);
                            String name = jwtUtil.getClaim(token, Constant.NAME);
                            AuthUser authUser = new AuthUser(userId, account, deptId, companyId, name, 0, roleIds, role);
                            request.setAttribute("authUser", authUser);
                            String[] userRoleArr = roleIds.split(",");
                            if (authority.value().length == 0 || Arrays.stream(authority.value()).anyMatch(promisionType -> StringUtil.isHave(promisionType.getRoleId().toString(), userRoleArr))) {
                                return true;
                            } else {
                                String date = DateUtil.getDateString(new Date(), "【yyyy-MM-dd HH:mm:ss】");
                                String uri = request.getRequestURI().substring(request.getContextPath().length());
                                System.out.println(date + "request：" + request.getMethod() + "|" + uri + "|===|权限异常，已拦截|");
                                response.setStatus(418);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write(new Result.Builder().rspCode(0).rspInfo("权限异常").buildJsonString());
                                return false;
                            }
                        } else {
                            throw new AppException(80401, "登录失效");
                        }
                    } else {
                        throw new AppException(80401, "token异常");
                    }
                } else {
                    throw new AppException(80401, "未登录");
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
