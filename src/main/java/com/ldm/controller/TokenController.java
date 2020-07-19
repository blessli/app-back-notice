package com.ldm.controller;

import com.ldm.aop.Action;
import com.ldm.util.CacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lidongming
 * @ClassName TokenController.java
 * @Description TODO
 * @createTime 2020年07月19日 18:13:00
 */
@Slf4j
@RestController
public class TokenController {

    @Autowired
    private CacheHelper cacheHelper;

    @Action(name = "获取access_token")
    @GetMapping("/getAccessToken")
    public String getAccessToken() throws Exception {
        log.info("获取access_token");
        return cacheHelper.getAccessToken();
    }
}
