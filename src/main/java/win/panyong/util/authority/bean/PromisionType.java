package win.panyong.util.authority.bean;

import win.panyong.util.AppCache;
import win.panyong.utils.StringUtil;

import java.util.Arrays;

/**
 * Created by pan on 2019/4/20 12:22 PM
 */
public enum PromisionType {
    /*
        在配置文件中配置角色id 例如: adminRoleId=1
        系统中角色展示为ordinal值小的，逻辑中角色权限为合集
     */
    ADMIN("admin") {
        @Override
        public Integer getRoleId() {
            return Integer.parseInt(AppCache.getConfigValue("adminRoleId", "0"));
        }
    },
    DEPARTMENT_ADMIN("departmentAdmin") {
        @Override
        public Integer getRoleId() {
            return Integer.parseInt(AppCache.getConfigValue("departmentAdminRoleId", "0"));
        }
    },
    STAFF("staff") {
        @Override
        public Integer getRoleId() {
            return 0;
        }
    };

    private final String type;

    PromisionType(String type) {
        this.type = type;
    }

    public static PromisionType getPromisionTypeByRoleIds(String roleIds) {
        if (!StringUtil.invalid(roleIds)) {
            for (PromisionType promisionType : PromisionType.values()) {
                if (Arrays.stream(roleIds.split(",")).anyMatch(roleId -> promisionType.getRoleId() == Integer.parseInt(roleId))) {
                    return promisionType;
                }
            }
        }
        return PromisionType.STAFF;
    }

    public abstract Integer getRoleId();

    public String getType() {
        return type;
    }
}
