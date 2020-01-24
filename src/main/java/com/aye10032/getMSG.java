package com.aye10032;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getMSG {

    public getMSG() {

    }

    public String get() {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String msg = "";

        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[id=root]").select("div[class=mapTop___2VZCl]").select("p");
        for (Element temp : elements) {
            msg = msg + temp.text() + "\n";
        }

        msg = msg + "--------------\n";

        elements = document.select("div[id=root]").select("div[class=areaBlock1___3V3UU]");

        for (Element temp : elements) {
            String city = temp.select("p[class=subBlock1___j0DGa]").text();
            String sure = temp.select("p[class=subBlock2___E7-fW]").text();
            String notsure = temp.select("p[class=subBlock3___3mcDz]").text();
            if (sure.equals("")) {
                sure = "0";
            }
            if (notsure.equals("")) {
                notsure = "0";
            }
            msg = msg + city + " 确认" + sure + "例，疑似" + notsure + "例\n";
        }

        return msg;
    }


    public String getCity(String city) {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String msg = "";

        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[id=root]").select("div[class=fold___xVOZX]");
        for (Element temp : elements) {
            String cityflag = temp.select("div[class=areaBlock1___3V3UU]").select("p[class=subBlock1___j0DGa]").text();
            if (cityflag.equals(city)) {
                String cityname = temp.select("div[class=areaBlock1___3V3UU]").select("p[class=subBlock1___j0DGa]").text();
                String sure = temp.select("div[class=areaBlock1___3V3UU]").select("p[class=subBlock2___E7-fW]").text();
                String notsure = temp.select("div[class=areaBlock1___3V3UU]").select("p[class=subBlock3___3mcDz]").text();
                if (sure.equals("")) {
                    sure = "0";
                }
                if (notsure.equals("")) {
                    notsure = "0";
                }
                msg = msg + cityname + " 目前确认" + sure + "例，疑似" + notsure + "例。其中：\n----------------\n";
                Elements citys = temp.select("div[class=areaBlock2___27vn7]");
                for (Element cityinfo : citys) {
                    if (cityinfo.select("p[class=subBlock1___j0DGa]").text().equals("")) {
                        msg = msg + cityinfo.text();
                    } else {
                        cityname = cityinfo.select("p[class=subBlock1___j0DGa]").text();
                        sure = cityinfo.select("p[class=subBlock2___E7-fW]").text();
                        notsure = cityinfo.select("p[class=subBlock3___3mcDz]").text();
                        if (sure.equals("")) {
                            sure = "0";
                        }
                        if (notsure.equals("")) {
                            notsure = "0";
                        }
                        msg = msg + cityname + " 确认" + sure + "例，疑似" + notsure + "例\n";
                    }
                }
            }
        }

        return msg;
    }

    public String getNews() {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String news = "";

//        System.out.println(htmlStr);

        Document document = Jsoup.parse(htmlStr);
        String msg = document.select("script[id=getTimelineService]").get(0).data();
        msg = msg.substring(msg.indexOf("["), msg.length() - 11);
        JSONArray jsonArray = new JSONArray(msg);

        JSONObject jsonObject = jsonArray.getJSONObject(0);
        news = jsonObject.getString("title")
                + "\n" + jsonObject.getString("pubDateStr")
                + "\n" + jsonObject.getString("summary")
                + "\n来源:" + jsonObject.getString("infoSource");


        return news;
    }

    public String getNews(int index) {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String news = "";

        if (index > 5){
            news = "数量太大，请前往 https://3g.dxy.cn/newh5/view/pneumonia 查看";
        }else {
            Document document = Jsoup.parse(htmlStr);
            String msg = document.select("script[id=getTimelineService]").get(0).data();
            msg = msg.substring(msg.indexOf("["), msg.length() - 11);
            JSONArray jsonArray = new JSONArray(msg);

            for (int i = 0; i < index; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                news = jsonObject.getString("title")
                        + "\n" + jsonObject.getString("pubDateStr")
                        + "\n" + jsonObject.getString("summary")
                        + "\n来源:" + jsonObject.getString("infoSource")
                        + "\n--------------------";
            }

        }

        return news;
    }

    public String getImg(String appDirectory) throws Exception {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String msg = "";

        Document document = Jsoup.parse(htmlStr);
        String imgurl = document.select("div[id=root]").select("div[class=mapBox___qoGhu]").select("img[class=mapImg___3LuBG]").get(0).attr("src");

        URL img = new URL(imgurl);
        HttpURLConnection conn = (HttpURLConnection) img.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();
        byte[] data = readInputStream(inStream);
        File imageFile = new File(appDirectory + "\\image\\newmap.jpg");
        FileOutputStream outStream = new FileOutputStream(imageFile);
        outStream.write(data);
        outStream.close();

        return imgurl;
    }

    private String downloadHtml(String url) {
        String body = null;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "PostmanRuntime/7.16.3")
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Host", "3g.dxy.cn")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        try {

            Response response = client.newCall(request).execute();
            body = new String(response.body().bytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    private byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

}
