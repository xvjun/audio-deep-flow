package com.xujun.utils;


import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpClient {


    public static JSONObject HttpPost(String url, JSONObject json){

        //设置请求header 为 APPLICATION_FORM_URLENCODED
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject rs = HttpPost(url, json, headers);
        return rs;
    }

    public static JSONObject HttpPost(String url, JSONObject json, HttpHeaders headers){

        //使用Restemplate来发送HTTP请求
        RestTemplate restTemplate = new RestTemplate();
        // json对象
//        JSONObject jsonObject = new JSONObject();


        // 请求体，包括请求数据 body 和 请求头 headers
        HttpEntity httpEntity = new HttpEntity(json,headers);


        try {
            //使用 exchange 发送请求，以String的类型接收返回的数据
            //ps，我请求的数据，其返回是一个json
            ResponseEntity<String> strbody = restTemplate.exchange(url, HttpMethod.POST,httpEntity,String.class);
//            restTemplate.
            //解析返回的数据
            JSONObject jsTemp = JSONObject.parseObject(strbody.getBody());
            return jsTemp;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String HttpGet(String url, HttpHeaders headers){

        //使用Restemplate来发送HTTP请求
        RestTemplate restTemplate = new RestTemplate();
        // 请求体，包括请求数据 body 和 请求头 headers
        HttpEntity httpEntity = new HttpEntity(headers);
        try {
            //使用 exchange 发送请求，以String的类型接收返回的数据
            //ps，我请求的数据，其返回是一个json
            ResponseEntity<String> strbody = restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);
            //解析返回的数据
            return strbody.getBody();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**http下载*/
    public static boolean httpDownload(String httpUrl, Map<String,String> header, String saveFile){
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }

        try {
            URLConnection conn = url.openConnection();
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
//                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
