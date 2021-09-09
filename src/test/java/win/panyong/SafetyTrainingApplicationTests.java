package win.panyong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import win.panyong.service.CommonService;

@SpringBootTest
class SafetyTrainingApplicationTests {

    @Autowired
    CommonService commonService;

    @Test
    void contextLoads() throws Exception {
    }

}
