package win.panyong.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import win.panyong.service.BaseService;
import win.panyong.service.CommonService;

@Service("commonService")
public class CommonServiceImpl extends BaseService implements CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

}
