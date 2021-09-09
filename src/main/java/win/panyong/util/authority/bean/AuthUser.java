package win.panyong.util.authority.bean;

import win.panyong.utils.DateUtil;

import java.io.Serializable;
import java.util.Date;

public class AuthUser implements Serializable {

    private static final long serialVersionUID = 6149874378638672616L;

    private Long id;

    private String phoneNumber;

    private Long deptId;
    private String deptName;

    private Long companyId;

    private String name;

    private Integer noticeCount;

    /**
     * 多个角色id使用逗号分隔
     */
    private String roleIds;

    /**
     * 多个角色名称使用逗号分隔
     */
    private String roleNames;

    private String password;

    private Date createTime;
    private String createTimeString;

    private String isActive;

    public AuthUser() {
    }

    public AuthUser(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public AuthUser(Long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public AuthUser(Long id, String phoneNumber, Long deptId, Long companyId, String name, Integer noticeCount, String roleIds, String roleNames) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.deptId = deptId;
        this.companyId = companyId;
        this.name = name;
        this.noticeCount = noticeCount;
        this.roleIds = roleIds;
        this.roleNames = roleNames;
    }

    public Long getId() {
        return id;
    }

    public AuthUser setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AuthUser setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Long getDeptId() {
        return deptId;
    }

    public AuthUser setDeptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public String getDeptName() {
        return deptName;
    }

    public AuthUser setDeptName(String deptName) {
        this.deptName = deptName;
        return this;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public AuthUser setCompanyId(Long companyId) {
        this.companyId = companyId;
        return this;
    }

    public String getName() {
        return name;
    }

    public AuthUser setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getNoticeCount() {
        return noticeCount;
    }

    public AuthUser setNoticeCount(Integer noticeCount) {
        this.noticeCount = noticeCount;
        return this;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public AuthUser setRoleIds(String roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public AuthUser setRoleNames(String roleNames) {
        this.roleNames = roleNames;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeString() {
        return DateUtil.getDateString(createTime,"yyyy-MM-dd HH:mm:ss");
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
