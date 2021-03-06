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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
            if (!temp.text().equals("")) {
                msg = msg + temp.text() + "\n";
            }
        }

        elements = document.select("div[id=root]").select("div[class=mapTop___2VZCl]").select("div[class=descText___Ui3tV]");
        for (Element temp : elements) {
            if (!temp.text().equals("")) {
                msg = msg + temp.text() + "\n";
            }
        }

        msg = msg + "------------\n";

        String jsonstr = document.select("script[id=getAreaStat]").get(0).data();
        jsonstr = jsonstr.substring(jsonstr.indexOf("["), jsonstr.length() - 11);

        JSONArray jsonArray = new JSONArray(jsonstr);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            msg = msg + jsonObject.getString("provinceName")
                    + " 确认" + jsonObject.getInt("confirmedCount")
                    + "例，疑似" + jsonObject.getInt("suspectedCount")
                    + "例，死亡" + jsonObject.getInt("deadCount")
                    + "例，治愈" + jsonObject.getInt("curedCount")
                    + "例\n";
        }

        return msg;
    }


    public String get(String city) {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String msg = "";

        Document document = Jsoup.parse(htmlStr);

        Elements elements = document.select("div[id=root]").select("div[class=mapTop___2VZCl]").select("p");
        for (Element temp : elements) {
            if (!temp.text().equals("")) {
                msg = msg + temp.text() + "\n";
            }
        }

        elements = document.select("div[id=root]").select("div[class=mapTop___2VZCl]").select("div[class=descText___Ui3tV]");
        for (Element temp : elements) {
            if (!temp.text().equals("")) {
                msg = msg + temp.text() + "\n";
            }
        }

        msg = msg + "------------\n";

        String jsonstr = document.select("script[id=getAreaStat]").get(0).data();
        jsonstr = jsonstr.substring(jsonstr.indexOf("["), jsonstr.length() - 11);

        JSONArray jsonArray = new JSONArray(jsonstr);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("provinceName").equals(city) || jsonObject.getString("provinceShortName").equals(city)) {
                msg = msg + jsonObject.getString("provinceName")
                        + " 确认" + jsonObject.getInt("confirmedCount")
                        + "例，疑似" + jsonObject.getInt("suspectedCount")
                        + "例，死亡" + jsonObject.getInt("deadCount")
                        + "例，治愈" + jsonObject.getInt("curedCount")
                        + "例。\n------------\n";
                JSONArray cityarray = jsonObject.getJSONArray("cities");
                for (int j = 0; j < cityarray.length(); j++) {
                    JSONObject cityObject = cityarray.getJSONObject(j);
                    msg = msg + cityObject.getString("cityName")
                            + " 确认" + cityObject.getInt("confirmedCount")
                            + "例，疑似" + cityObject.getInt("suspectedCount")
                            + "例，死亡" + cityObject.getInt("deadCount")
                            + "例，治愈" + cityObject.getInt("curedCount")
                            + "例\n";
                }
                msg = msg + jsonObject.getString("comment");
            }
        }

        return msg;
    }

    public String getNews() {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String news = "";

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

        if (index > 5) {
            news = "数量太大，请前往 https://3g.dxy.cn/newh5/view/pneumonia 查看";
        } else {
            Document document = Jsoup.parse(htmlStr);
            String msg = document.select("script[id=getTimelineService]").get(0).data();
            msg = msg.substring(msg.indexOf("["), msg.length() - 11);
            JSONArray jsonArray = new JSONArray(msg);

            for (int i = 0; i < index; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                news = news + jsonObject.getString("title")
                        + "\n" + jsonObject.getString("pubDateStr")
                        + "\n" + jsonObject.getString("summary")
                        + "\n来源:" + jsonObject.getString("infoSource")
                        + "\n--------------------\n";
            }

        }

        return news;
    }

    public String isTrue(String msg) {
        String url = "https://vp.fact.qq.com/searchresult?title=" + msg;
        OkHttpClient client = new OkHttpClient();
        String body = null;

        String result = "";

        Request request = new Request.Builder().url(url).get().build();
        try {

            Response response = client.newCall(request).execute();
            body = new String(response.body().bytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(body);

        if (jsonObject.getInt("total") == 0) {
            result = "查询结果为0，试试别的关键词？";
        } else {
            JSONArray jsonArray = jsonObject.getJSONArray("content");
            int flag = 3;
            if (jsonObject.getInt("total") <= 3) {
                flag = jsonObject.getInt("total");
            }

            for (int i = 0; i < flag; i++) {
                JSONObject msgins = jsonArray.getJSONObject(i).getJSONObject("_source");

                result = result
                        + msgins.getString("title")
                        + "\n" + msgins.getString("result")
                        + "\n" + msgins.getString("abstract")
                        + "\n来源:" + msgins.getString("source");
                if (msgins.has("oriurl")) {
                    result = result + "\n链接:" + msgins.getString("oriurl");
                }
                result = result + "\n---------------\n";
            }

        }

        return result;
    }

    public String getImg(String appDirectory) throws Exception {
        String url = "https://3g.dxy.cn/newh5/view/pneumonia";
        String htmlStr = downloadHtml(url);
        String msg = "";

        Document document = Jsoup.parse(htmlStr);
        String imgurl = document.select("div[id=root]").select("img[class=mapImg___3LuBG]").get(0).attr("src");

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
