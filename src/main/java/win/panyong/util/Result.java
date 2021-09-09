package win.panyong.util;

import win.panyong.utils.ObjectUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class Result {
    private final Integer rspCode;
    private final String rspInfo;
    private final LinkedHashMap<String, Object> rspResult;

    private Result(Builder builder) {
        this.rspCode = builder.rspCode;
        this.rspInfo = builder.rspInfo;
        this.rspResult = builder.rspResult;
    }

    public static Builder builder() {
        return new Builder();
    }

    private Map<String, Object> getResultObject() {
        Map<String, Object> result = new LinkedHashMap<String, Object>() {{
            put("rspCode", rspCode);
            put("rspInfo", rspInfo);
            put("rspResult", rspResult);
        }};
        return result;
    }

    public String toJsonString() {
        return ObjectUtil.mapToJsonString(getResultObject());
    }

    public static class Builder {
        private final LinkedHashMap<String, Object> rspResult = new LinkedHashMap<>();
        private Integer rspCode = 0;
        private String rspInfo = "fail";

        public Builder() {
        }

        public Builder rspCode(Integer rspCode) {
            this.rspCode = rspCode;
            return this;
        }

        public Builder rspInfo(String rspInfo) {
            this.rspInfo = rspInfo;
            return this;
        }

        public Builder putData(String key, Object value) {
            this.rspResult.put(key, value);
            return this;
        }

        public Result build() {
            return new Result(this);
        }

        public String buildJsonString() {
            return this.build().toJsonString();
        }
    }
}



