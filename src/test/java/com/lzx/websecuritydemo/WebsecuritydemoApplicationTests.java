package com.lzx.websecuritydemo;

import com.lzx.websecuritydemo.util.BCryptUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebsecuritydemoApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void test(){
        System.out.println(BCryptUtil.encode("admin"));
    }

}
