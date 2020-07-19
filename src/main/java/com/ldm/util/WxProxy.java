package com.ldm.util;

import com.alibaba.fastjson.JSON;
import com.ldm.entity.AccessToken;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author lidongming
 * @ClassName WxProxy.java
 * @Description 微信代理
 * @createTime 2020年07月19日 17:52:00
 */
@Service
public class WxProxy {
    /**
     * @title 获取接口调用凭证
     * @description 获取小程序全局唯一后台接口调用凭据（access_token）。调用绝大多数后台接口时都需使用 access_token，开发者需要进行妥善保存。
     * @author lidongming
     * @updateTime 2020/4/8 23:05
     */
    public AccessToken getAccessToken() throws Exception {

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxa2456aa6cbac869c&secret=6626b587b45f7a6d3e93988f89979fb8";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        AccessToken accessToken= JSON.parseObject(response.toString(),AccessToken.class);
        System.out.println(accessToken);
        return accessToken;
    }
}
