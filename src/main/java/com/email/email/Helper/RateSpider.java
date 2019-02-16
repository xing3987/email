package com.email.email.Helper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@RestController
public class RateSpider {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    public static Map<String,String> rateMap=getRateMap();

    public static Double getExchange(String changefrom,String changeto){
        //Map<String,String> rateMap=getRateMap();
        if(rateMap!=null){
            String fromStr=rateMap.get(changefrom);
            String toStr=rateMap.get(changeto);
            if(fromStr==null || toStr==null || fromStr.length() == 0 || toStr.length() == 0){
                return null;
            }else {
                try{
                    Double fromDou=Double.parseDouble(fromStr);
                    Double toDou=Double.parseDouble(toStr);
                    return 1/fromDou*toDou;
                }catch (Exception e){
                    return null;
                }
            }
        }
        return null;
    }

    public static Double doubleFormat(Double value, Integer size){
        int doublesize=1;
        for(int i=1;i<=size;i++){
            doublesize=doublesize*10;
        }
        value = (double) Math.round(value * doublesize) / doublesize;
        return value;
    }


    @Scheduled(cron = "* * 0/12 * * ?")
    public void resetRateMap() throws Exception{
        Map<String,String> newRateMap=getRateMap();
        if(newRateMap!=null && newRateMap.size()>0){
            rateMap=newRateMap;
        }
    }

    public static Map<String,String> getRateMap(){
        String url ="http://hl.anseo.cn/";//from url
        Map params = new HashMap();//params

        try {
            String result =net(url, params, "GET").trim();
            String res="<li.*?>1 人民币 =(.*?)<a.*?>.*?</a>(.*?)&.*?<br>";
            Pattern pattern = Pattern.compile(res);
            Matcher matcher = pattern.matcher(result);
            String code1="";
            String code2="";
            Map<String,String> map=new HashMap<>();
            while (matcher.find()) {
                code1 = matcher.group(1).trim();
                code2 = matcher.group(2).trim();
                code2=code2.replaceAll("\\(|\\)", "");
                map.put(code2,code1);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param strUrl webAddress
     * @param params
     * @param method
     * @return  rusult
     * @throws Exception
     */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    out.writeBytes(urlencode(params));
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //translate map to param
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
