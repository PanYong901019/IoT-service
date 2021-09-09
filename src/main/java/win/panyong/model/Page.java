package win.panyong.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page<T> {
    /**
     * 开始条
     */
    private final int since = 0;
    /**
     * 当前页
     */
    private int pageNo = 1;
    /**
     * 每页条数
     */
    private int limit = 10;
    /**
     * 共多少条
     */
    private int totalCount;
    /**
     * 共多少页
     */
    private int totalPage;
    /**
     * 查询条件
     */
    private Map<String, String> search = new HashMap<>();
    /**
     * 数据
     */
    private List<T> data;

    public Page() {
    }

    public Page(int pageNo) {
        this.pageNo = pageNo;
    }

    public Page(int pageNo, int limit) {
        this.pageNo = pageNo;
        this.limit = limit;
    }

    public Page(int pageNo, int limit, Map<String, String> search) {
        this.pageNo = pageNo;
        this.limit = limit;
        this.search = search;
    }

    public int getPageNo() {
        return pageNo;
    }

    public Page<T> setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public int getSince() {
        return (pageNo - 1) * limit;
    }

    public int getLimit() {
        return limit;
    }

    public Page<T> setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public Page<T> setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.totalPage = (totalCount + this.limit - 1) / this.limit;
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public Map<String, String> getSearch() {
        return search;
    }

    public Page<T> setSearch(Map<String, String> search) {
        this.search = search;
        return this;
    }

    public Page<T> putSearch(String key, String value) {
        this.search.put(key, value);
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public Page<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    public String getLimitSql() {
        return this.getLimit() != -1 ? ("LIMIT " + this.getSince() + "," + this.getLimit()) : "";
    }
}
