package win.panyong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import win.panyong.mapper.CommonMapper;
import win.panyong.util.AppCache;
import win.panyong.util.RedisUtil;

public class BaseService {
    @Autowired
    protected AppCache appCache;
    @Autowired
    protected RedisUtil redisUtil;
    @Autowired
    protected DataSourceTransactionManager transactionManager;
    @Autowired
    protected CommonMapper commonMapper;
}
