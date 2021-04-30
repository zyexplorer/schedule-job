package com.xxl.job.core.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * description
 *
 * @author ZY
 * @date 2020年12月28日 16:25
 **/
@Slf4j
public class HttpRequestUtil {

    private static final CloseableHttpClient HTTP_CLIENT;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(20);
        cm.setDefaultMaxPerRoute(50);
        HTTP_CLIENT = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 发送GET请求
     *
     * @param url        目的地址
     * @param parameters 请求参数，Map类型。
     * @return 远程响应结果
     */
    public static <T>T sendGet(String url, Map<String, String> parameters, Class<T> returnClass) {
        StringBuilder result = new StringBuilder();
        // 读取响应输入流
        BufferedReader in = null;
        // 存储参数
        StringBuffer sb = new StringBuffer();

        // 编码之后的参数
        String params;
        String fullUrl = url;
        try {
            if (parameters != null) {
                // 编码请求参数
                String encodeParams = getEncodeParams(parameters, sb);
                fullUrl = url + "?" + encodeParams;
            }

            // 创建URL对象
            java.net.URL connURL = new java.net.URL(fullUrl);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 建立实际的连接
            httpConn.connect();
            // 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : headers.keySet()) {
//                System.out.println(key + "\t：\t" + headers.get(key));
//            }
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), StandardCharsets.UTF_8));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("发送get请求异常：", e);
            log.error("send GET request error: url=" + url + ",params=" + parameters);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                log.error("close InputStream exception: " + ex);
            }
        }
        return JSON.parseObject(result.toString(), returnClass);
    }

    /**
     * 发送POST请求
     *
     * @param url        目的地址
     * @param parameters 请求参数，Map类型。
     * @return 远程响应结果
     */
    public static <T>T sendPost(String url, Map<String, String> parameters, Class<T> returnClass) {
        // 返回的结果
        StringBuilder result = new StringBuilder();
        // 读取响应输入流
        BufferedReader in = null;
        PrintWriter out = null;
        // 处理请求参数
        StringBuffer sb = new StringBuffer();
        // 编码之后的参数
        String encodeParams;
        try {
            // 编码请求参数
            encodeParams = getEncodeParams(parameters, sb);
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);

            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");

            // 设置POST方式
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);

//            httpConn.setRequestProperty("EngineCode","an6t10i9ni7459vo");
//            httpConn.setRequestProperty("EngineSecret","3rh1MF4P8c9tbmyM3mC4ftqpLFLh76lL51Ydvrwi3wwdDIZ04+agYA==");
            httpConn.setRequestProperty("Content-Type","application/json");

//            httpConn.setRequestProperty("Authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiYXBpIl0sInVzZXJfaWQiOiJmOGNmOTEzNjZhOTU4YWJiMDE2YTk1OGNkYzY1MDNmMCIsInVzZXJfbmFtZSI6IjI2NTE0NjE3Njg5ODQ0OTkiLCJzY29wZSI6WyJyZWFkIl0sIm1vYmlsZSI6ZmFsc2UsImlzQWRtaW4iOnRydWUsImV4cCI6MTU1ODU0NDM0OSwiaXNBcHBBZG1pbiI6ZmFsc2UsImF1dGhvcml0aWVzIjpbIlVTRVIiLCJBVVRIX1NZU1RFTV9NQU5BR0UiXSwianRpIjoiZjA0NWYyZGEtMzUxYy00MTM0LTkwZGQtYTQwY2Q1NDJiZWJkIiwiY2xpZW50X2lkIjoiYXBpIn0.jjDiJYdhE5G02i5wv3DBKQqQm47PfV5mn48BzgPYtMJDW7Io26M_epNHqCtSHR3WoxBRPAV6lNm1fRLLSRwcht6SaLhn-PXyVD1AwU0SuN4k0JtYaMFCLVBU65-D-OCVidv7_BvaLzJP5gVLKQdhAcs5hldAC3R3m-EINfnLwM_27pg8sqldc9P-SEy89_RSQaXxx_HyAgCT1jJ3NgHtskKJdF9BBLMJvqSvjyGYyBi-gxRz1efR4zRFhxP30K-YxBrhEGnGEpFoQ2OgPlR0hG_wHsrmNC8_1FqR15fAv2-cyW9-2bc6ADeMji2Lnxi933ZF-TDW4j1bfmOhscF-Rw");

            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            if (encodeParams != null) {
                out.write(encodeParams);
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), StandardCharsets.UTF_8));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error("send POST request exception: url=" + url + ",encodeParams=" + parameters + " \nexception=" + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return JSON.parseObject(result.toString(), returnClass);
    }

    public static <T>T sendGet(String url, Map headers, Map<String, String> params, Class<T> returnClass) {
        CloseableHttpResponse response = null;
        BufferedReader in;
        String result = "";
        try {
            // 设置请求参数
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("Accept", "application/json");
            if (headers != null && headers.size() > 0) {
                for (Object o : headers.entrySet()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) o;
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            response = HTTP_CLIENT.execute(httpGet);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder("");
            String line;
            String nL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line).append(nL);
            }
            in.close();
            result = sb.toString();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JSON.parseObject(result, returnClass);
    }

    public static <T>T sendPost(String url, Map headers, Map<String, Object> params, Class<T> returnClass) {
        CloseableHttpResponse response = null;
        BufferedReader in;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-type", "application/json;charset=utf-8");
            httpPost.setHeader("Accept", "application/json");

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>) headers.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            if (params != null && !params.isEmpty()) {
                httpPost.setEntity(new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8));
            }
            response = HTTP_CLIENT.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            String nL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line).append(nL);
            }
            in.close();
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JSON.parseObject(result, returnClass);
    }

    private static String getEncodeParams(Map<String, String> parameters, StringBuffer sb) throws UnsupportedEncodingException {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        for (String name : parameters.keySet()) {
            sb.append(name).append("=")
                    .append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
        }
        String tempParams = sb.toString();
        return tempParams.substring(0, tempParams.length() - 1);
    }

    /**
     * 主函数，测试请求
     *
     * @param args {"customerclss":
     *             [{
     *             "cccuscode":"01",
     *             "cccusname":"测试",
     *             "sjccuscode":"",
     *             "bccend":"1"
     *             }] }
     */
//    public static void main(String[] args) {
//        Map<String, String> parameters = Maps.newHashMap();
//        parameters.put("token", "E8747C2D-D934-4A25-A4B1-3F75858B4F8C ");
//
//        Map<String, Object> map1 = Maps.newHashMap();
//        List<Map<String, Object>> list = Lists.newArrayList();
//        Map<String, Object> map2 = Maps.newHashMap();
//        map2.put("cccuscode", "04");
//        map2.put("cccusname", "测试4");
//        map2.put("sjccuscode", "03");
//        map2.put("bccend", "0");
//        list.add(map2);
//        map1.put("customerclss", list);
//
//        parameters.put("json", JSON.toJSONString(map1));
//
//        Map result = sendGet("http://47.103.130.81/WebSite/DataOperService.asmx/customerclassAdd", parameters, Map.class);
//        System.out.println(result);
//    }


//    public static void main(String[] args) throws Exception {
//        String startDateStr = "2020-06-05";
//        String endDateStr = "2020-06-21";
//        Date startDate = DateUtil.parseDate(startDateStr, DateUtil.SHORT_PATTERN_2);
//        Date endDate = DateUtil.parseDate(endDateStr, DateUtil.SHORT_PATTERN_2);
//        int betweenDays = DateUtil.calcBetweenDays(startDate, endDate);
//
//        String url = "http://192.168.254.120:57001/api/preDeal/postDealWebBasicData";
//        for (int i = 0; i <= betweenDays; i++) {
//            Map<String, String> map = Maps.newHashMap();
//            Date date = DateUtil.getDate(startDate, i);
//            map.put("date", DateUtil.formatDate(date, DateUtil.SHORT_PATTERN_2));
//            HttpRequestUtil.sendGet(url, map, Map.class);
//        }
//        System.out.println("转移完成！");
//    }



}
