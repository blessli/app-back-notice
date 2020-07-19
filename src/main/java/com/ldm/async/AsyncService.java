package com.ldm.async;

import com.ldm.dao.ActivityDao;
import com.ldm.dao.SearchActivityDao;
import com.ldm.entity.AccessToken;
import com.ldm.entity.EsActivity;
import com.ldm.entity.SearchDomain;
import com.ldm.util.CacheHelper;
import com.ldm.util.DateHandle;
import com.ldm.util.RedisKeys;
import com.ldm.util.WxProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class AsyncService {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private WxProxy wxProxy;
    @Autowired
    private SearchActivityDao searchActivityDao;

    @Autowired
    private CacheHelper cacheHelper;

    @Async("asyncServiceExecutor")
    public void refreshAccessToken() throws Exception {
        cacheHelper.updateAccessToken(wxProxy.getAccessToken());
    }
}
