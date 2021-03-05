package com.tanhua.server.test;

import com.tanhua.commons.templates.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {

    @Autowired(required = false)
    private SmsTemplate smsTemplate;

    @Test
    public void testSms() {
        Map<String, String> stringStringMap
                = smsTemplate.sendValidateCode
                ("18207622540", "666666");
        System.out.println(stringStringMap);
    }
}