package com.imooc.o2o.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
public class ShortNetAddressUtil {
    private static Logger log = LoggerFactory.getLogger(ShortNetAddressUtil.class);

    public static int TIMEOUT = 30 * 1000;
    public static String ENCODING = "UTF-8";
    public static String getShortURL(String originURL) {
        String tinyUrl = null;
        try {
            //百度接口
            URL url = new URL("http://dwz.cn/create.php");
            //建立连接
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            // POST Request Define:
            //设置参数
            //使用连接进行输出
            connection.setDoOutput(true);
            //连接进行输入
            connection.setDoInput(true);
            //不缓存
            connection.setUseCaches(false);
            //超时30s
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestMethod("POST");
            String postData = URLEncoder.encode(originURL.toString(), "utf-8");
            connection.getOutputStream().write(("url=" + postData).getBytes());
            connection.connect();
            //获取返回字段
            String responseStr = getResponseStr(connection);
            log.info("response string: " + responseStr);
            //获取短连接
            tinyUrl = getValueByKey_JSON(responseStr, "tinyurl");
            log.info("tinyurl: " + tinyUrl);
            connection.disconnect();
        } catch (IOException e) {
            log.error("getshortURL error:" + e.toString());
        }
        return tinyUrl;

    }
    private static String getResponseStr(HttpURLConnection connection)
            throws IOException {
        StringBuffer result = new StringBuffer();
        //获取hppt状态码
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            //如若是ok 则去除连接的输入流
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, ENCODING));
            String inputLine = "";
            while ((inputLine = reader.readLine()) != null) {
                result.append(inputLine);
            }
        }
        return String.valueOf(result);
    }
    private static String getValueByKey_JSON(String replyText, String key) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        String targetValue = null;
        try {
            node = mapper.readTree(replyText);
            targetValue = node.get(key).asText();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            log.error("getValueByKey_JSON error:" + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("getValueByKey_JSON error:" + e.toString());
        }

        return targetValue;
    }
    public static void main(String[] args) {
        getShortURL("https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login");
    }

}