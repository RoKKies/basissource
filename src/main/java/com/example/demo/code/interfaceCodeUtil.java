package com.example.demo.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @Author : LiYuSheng
 * @Date : Created on 10:47 2018/10/26 0026.
 */
public class interfaceCodeUtil {

    private static String createSign(String param1, String param2, String param3, String signkey) {
        //创建一个保存参数K,V的map
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("akey1", param1);
        paramMap.put("ckey2", param2);
        paramMap.put("bkey3", param3);
        //用于排序的list
        List<String> paramList = new ArrayList<>();
        for (String key : paramMap.keySet()) {
            paramList.add(key);
        }
        //对list进行排序
        Collections.sort(paramList);
        StringBuffer signsb = new StringBuffer();
        //将排序后的参数组成字符串
        for (String str : paramList) {
            if (paramMap.get(str) == null || paramMap.get(str).isEmpty() || paramMap.get(str).equals("")) {
                continue;
            }
            signsb.append("&" + str + "=" + paramMap.get(str));
        }
        //添加私钥
        signsb.append(signkey);
        //减去首位&符号
        String signStr = signsb.toString().substring(1, signsb.toString().length());
        System.out.println(signStr);
        return signStr;
    }

    private static String getMD5(byte[] source) {
        String s = null;
        char[] hexDigits = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            // MD5 的计算结果是一个 128 位的长整数，
            byte[] tmp = md.digest();
            // 用字节表示就是 16 个字节
            // 每个字节用 16 进制表示的话，使用两个字符，
            char[] str = new char[16 * 2];
            // 所以表示成 16 进制需要 32 个字符
            // 表示转换结果中对应的字符位置
            int k = 0;
            // 从第一个字节开始，对 MD5 的每一个字节
            for (int i = 0; i < 16; i++) {
                // 转换成 16 进制字符的转换
                // 取第 i 个字节
                byte byte0 = tmp[i];
                // 取字节中高 4 位的数字转换,
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                // >>>
                // 为逻辑右移，将符号位一起右移
                // 取字节中低 4 位的数字转换
                str[k++] = hexDigits[byte0 & 0xf];
            }
            // 换后的结果转换为字符串
            s = new String(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    private static String getResponse(String requsetUrl, String content) {
        try {
            URL url = new URL(requsetUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            // 使用 URL 连接进行输出
            httpConn.setDoOutput(true);
            // 使用 URL 连接进行输入
            httpConn.setDoInput(true);
            // 忽略缓存
            httpConn.setUseCaches(false);
            // 设置URL请求方法
            httpConn.setRequestMethod("POST");
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.close();
            BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String readLine;
            StringBuffer responseSb = new StringBuffer();
            while ((readLine = responseReader.readLine()) != null) {
                responseSb.append(readLine);
            }
            return responseSb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static void main(String[] args) {
        connect("111", "222", "333", "abcde");
    }

    public static void connect(String param1, String param2, String param3, String signkey) {
        StringBuilder param = new StringBuilder();
        param.append("akey1=" + param1 + "&");
        param.append("bkey2=" + param2 + "&");
        param.append("ckey3=" + param3 + "&");
        String sn = createSign(param1, param2, param3, signkey);
        param.append("sn=" + getMD5(sn.getBytes()) + "");
        getResponse("http://test.com", param.toString());
    }
}
