package com.ldm.async;

import com.ldm.util.CacheHelper;
import com.ldm.util.WxProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncService {

    @Autowired
    private WxProxy wxProxy;

    @Autowired
    private CacheHelper cacheHelper;

    /**
     * wx：建议开发者使用中控服务器统一获取和刷新 access_token，其他业务逻辑服务器所使用的 access_token 均来自于该中控服务器，不应该各自去刷新，否则容易造成冲突，导致 access_token 覆盖而影响业务
     * @throws Exception
     */
    @Async("asyncServiceExecutor")
    public void refreshAccessToken() throws Exception {
        cacheHelper.updateAccessToken(wxProxy.getAccessToken());
    }
}
