package com.example.wtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static int TO_GPS = 1;
    final static String Tag = "WeatherProject";

    private ArrayList<String> store = new ArrayList<String>();
    private ArrayList<String> store2 = new ArrayList<String>();
    private ArrayList<String> store3 = new ArrayList<String>();
    private SwipeRefreshLayout mysrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }
        getData();

        //텍스트뷰 읽어오기
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);

        String[] array = te1.split(" ");
        String local = array[1];
        Log.d("local2",local);

        if(local.equals("천안시")){
            getAirData();
        }else if(local.equals("아산시")){
            getAirData2();
        }

        //getData2();



//새로고침
        mysrl = findViewById(R.id.srl);
        mysrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData();
                getAirData();
                //getData2();

                mysrl.setRefreshing(false);
            }

        });

    }



    public void getData(){

        gpsTracker = new GpsTracker(MainActivity.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);


        LatXLngY tmp = convertGRID_GPS(TO_GPS, latitude, longitude);
        Log.e(">>", "x = " + tmp.x + ", y = " + tmp.y);
        //Log.d("xxx", String.valueOf(tmp.x));

        String pageNo = "1";
        String numOfRows = "1000";

        //오늘 날짜
        SimpleDateFormat real_time = new SimpleDateFormat("yyyyMMdd");//현재날짜(ex.20220313)
        Date time = new Date();
        String base_date = real_time.format(time);
        //String base_date ="20220317";

        //어제 날짜
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE,-1);
        Log.d("yester", String.valueOf(calendar));


        SimpleDateFormat f2 = new SimpleDateFormat("Hmm");
        String base_time = f2.format(time);
        Log.d("time2",base_time);
        int t = Integer.parseInt(base_time);
        Log.d("t", String.valueOf(t));


        //날씨 API는 매시간 40분에 업데이트 됨.
        if(t>=0040 && t<140){
            //base_date = real_time.format(calendar.getTime());
            base_time = "0030";

        }
        else if(t>=140 && t<240){
            base_time = "0130";
        }
        else if(t>=240 && t<340){
            base_time = "0230";
        }
        else if(t>=340 && t<440){
            base_time = "0330";
        }
        else if(t>=440 && t<540){
            base_time = "0430";
        }
        else if(t>=540 && t<640){
            base_time = "0530";
        }
        else if(t>=640 && t<740){
            base_time = "0630";
        }
        else if(t>=740 && t<840){
            base_time = "0730";
        }
        else if(t>=840 && t<940){
            base_time = "0830";
        }
        else if(t>=940 && t<1040){
            base_time = "0930";
        }
        else if(t>=1040 && t<1140){
            base_time = "1030";
        }
        else if(t>=1140 && t<1240){
            base_time = "1130";
        }
        else if(t>=1240 && t<1340){
            base_time = "1230";
        }
        else if(t>=1340 && t<1440){
            base_time = "1330";
        }
        else if(t>=1440 && t<1540){
            base_time = "1430";
        }
        else if(t>=1540 && t<1640){
            base_time = "1530";
        }
        else if(t>=1640 && t<1740){
            base_time = "1630";
        }
        else if(t>=1740 && t<1840){
            base_time = "1730";
        }
        else if(t>=1840 && t<1940){
            base_time = "1830";
        }
        else if(t>=1940 && t<2040){
            base_time = "1930";
        }
        else if(t>=2040 && t<2140){
            base_time = "2030";
        }
        else if(t>=2140 && t<2240){
            base_time = "2130";
        }
        else if(t>=2240 && t<2340){
            base_time = "2230";
        }
        else if(t>=2340 && t<2440){
            base_time = "2330";
        }
        else{
            Calendar day = Calendar.getInstance();
            //24시~24시40분 -> 전날 11시 30분 데이터.
            day.add(Calendar.DATE , -1);
            String beforeDate = new java.text.SimpleDateFormat("yyyyMMdd").format(day.getTime());
            Log.d("beforeDate",beforeDate);
            base_date = beforeDate;
            base_time = "2330";
        }

        //Log.d("yyyyy",base_date);

        double d = tmp.x;
        DecimalFormat df = new DecimalFormat("0");
        String result = df.format(d);
        Log.d("result",result);
        String nx = result;

        double d2 = tmp.y;
        //DecimalFormat decimalFormat = new DecimalFormat("0");
        String result2 = df.format(d2);
        Log.d("result2",result2);
        String ny = result2;

        String qurl ="http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D"+"&pageNo="+pageNo+"numOfRows="+numOfRows+"&dataType=JSON"+
                "&base_date="+base_date+
                "&base_time="+base_time+
                "&nx="+nx+
                "&ny="+ny;

        TextView ttx;
        ttx = (TextView)findViewById(R.id.ttx);
        ttx.setText(base_date+"/"+base_time);

//        String url2 = "https://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D&returnType=json&numOfRows=100&pageNo=1&sidoName=%EC%B6%A9%EB%82%A8&searchCondition=HOUR";


        new Thread(){

            public void run() {
                try {

                    URL url = new URL(qurl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    InputStream is = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String result;
                    while((result = br.readLine())!=null){
                        sb.append(result+"\n");
                    }
                    result = sb.toString();
                    Log.d("tag2",result);
                    JsonParse(result);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getData2(){

        //네이버 미세먼지 주소
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=%EB%AF%B8%EC%84%B8%EB%A8%BC%EC%A7%80";

        //텍스트뷰 읽어오기
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);

        String[] array = te1.split(" ");
        String local = array[1];
        Log.d("local2",local);


        new Thread(){
            @Override
            public void run() {

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Document doc = null;
                //Document doc2 = null;
                TextView textView = findViewById(R.id.time);
                String str1;
                String msg, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17;
                try {
//                    doc = Jsoup.connect(url).get();
//                    Elements elements = doc.select(".detail_box td");
//                    msg = elements.get(0).text();//서울
//                    msg3 = elements.get(3).text();//경기
//                    msg4 = elements.get(6).text();//인천
//                    msg5 = elements.get(9).text();//강원
//                    msg6 = elements.get(12).text();//세종
//                    msg7 = elements.get(15).text();//충북
//                    msg8 = elements.get(18).text();//충남
//                    msg9 = elements.get(21).text();//대전
//                    msg10 = elements.get(24).text();//경북
//                    msg11 = elements.get(27).text();//경남
//                    msg12 = elements.get(30).text();//대구
//                    msg13 = elements.get(33).text();//울산
//                    msg14 = elements.get(36).text();//부산
//                    msg2 = elements.get(39).text();//전북
//                    msg15 = elements.get(42).text();//전남
//                    msg16 = elements.get(45).text();//광주
//                    msg17 = elements.get(48).text();//제주
                    //Log.d("msg",msg8);
                    TextView air = findViewById(R.id.air);
                    //air.setText(msg8);

                    //충청남도 미세먼지 url
                    String url2 = "https://m.search.naver.com/search.naver?where=m&sm=mtb_etc&mra=blQ3&qvt=0&query=%EC%B6%A9%EB%82%A8%20%EB%AF%B8%EC%84%B8%EB%A8%BC%EC%A7%80";
                    Document doc2 = null;
                    doc2 = Jsoup.connect(url2).get();
                    Elements element = doc2.select("div.map_life.ct15 div.lcl_lst span");

                    //천안
                    str1 = element.get(1).text();
                    Log.d("str1", str1);

                    //공주
                    String str2;
                    str2 = element.get(3).text();
                    Log.d("str2", str2);

                    //보령
                    String str3;
                    str3 = element.get(5).text();
                    Log.d("str3", str3);

                    //아산
                    String str4;
                    str4 = element.get(7).text();
                    Log.d("str4", str4);

                    //현재 시간 test. 지워야 함.
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                    Date now = new Date();
                    String nowTime = sdf1.format(now);

                    //스레드에서 ui변경을 위해 runOn~~사용함.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (local.equals("천안시")) {
                                air.setText(str1);
                            } else if (local.equals("공주시")) {
                                air.setText(str2);
                            } else if (local.equals("보령시")) {
                                air.setText(str3);
                            } else if (local.equals("아산시")) {
                                air.setText(str4);
                            }
                            textView.setText(nowTime);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getAirData(){
        String qurl = null;
        //텍스트뷰 읽어오기
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);

        String[] array = te1.split(" ");
        String local = array[0];
        Log.d("local2",local);

        String[] array2 = te1.split(" ");
        String local2 = array2[1];
        Log.d("local3",local2);

        if(local.equals("충청남도")){
            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=충남&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
        }
//        else if(local.equals("서울특별시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=서울&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("부산광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=부산&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("대구광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=대구&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("인천광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=인천&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("광주광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=광주&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }


        String finalQurl = qurl;
        new Thread(){

            public void run() {

                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {

                    URL url = new URL(finalQurl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    InputStream is = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String result;
                    while((result = br.readLine())!=null){
                        sb.append(result+"\n");
                    }
                    result = sb.toString();
                    Log.d("tag2",result);
                    JsonParse2(result);



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void getAirData2(){
        String qurl = null;
        //텍스트뷰 읽어오기
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("texttt",te1);

        String[] array = te1.split(" ");
        String local = array[0];
        Log.d("localll2",local);

        String[] array2 = te1.split(" ");
        String local2 = array2[1];
        Log.d("localll3",local2);

        if(local.equals("충청남도")){
            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=충남&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
        }
//        else if(local.equals("서울특별시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=서울&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("부산광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=부산&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("대구광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=대구&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("인천광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=인천&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("광주광역시")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=광주&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }


        String finalQurl = qurl;
        new Thread(){

            public void run() {

                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    URL url = new URL(finalQurl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    InputStream is = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String result;
                    while((result = br.readLine())!=null){
                        sb.append(result+"\n");
                    }
                    result = sb.toString();
                    Log.d("tag2",result);
                    JsonParse3(result);



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //위,경도 -> 격자 좌표변환
    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기준점 Y좌표(GRID)


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GPS) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }



    class LatXLngY
    {
        public double lat;
        public double lng;

        public double x;
        public double y;

    }




    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "새로고침 해주세요!", Toast.LENGTH_LONG).show();
            //gpsTracker = new GpsTracker(MainActivity.this);
            //getData();
//            double latitude2 = gpsTracker.getLatitude();
//            double longitude2 = gpsTracker.getLongitude();
//            String lalo = latitude2+","+longitude2;
//            addresses.add(lalo);
//            final Handler handler = new Handler(Looper.getMainLooper());
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
//                handler.postDelayed(new Runnable() {
//                    @Override public void run() {
//                        // 코드 작성
//                        checkRunTimePermission();
//
//
//                        getData();
//                        getData2();
//                        //handler.postDelayed(this,500);
//                    }
//                },2000);
//
//
//            }

            return "주소 미발견";
        }


        //지오코더에서 필요한 값만 추출
        String cut[] = addresses.get(0).toString().split(" ");
        for(int i=0; i<cut.length;i++){
            Log.d("cut",i+cut[i]);
        }

        TextView text;
        text=findViewById(R.id.tx);
        text.setText(cut[1] + " " + cut[2]);

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("aaa", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



    //api Json파싱
    public void JsonParse(String str) {
        //String str1 = null;
        JSONObject obj;
        //ArrayList<String> array = new ArrayList<String>();
        try {
            store.clear();
            obj = new JSONObject(str);
            JSONObject response = obj.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");

            JSONArray arr = items.getJSONArray("item");
            for(int i = 0;i<arr.length();i++){

                JSONObject job = arr.getJSONObject(i);
                String category = job.getString("category");
                String obsrValue = job.optString("obsrValue");
                Log.d("category",category);
                Log.d("obsrValue",obsrValue);


                if(category.equals("PTY")){
                    if(obsrValue.equals("0")){
                        obsrValue="없음";
                    }
                    else if(obsrValue.equals("1")){
                        obsrValue="비";
                    }
                    else if(obsrValue.equals("2")){
                        obsrValue="비/눈";
                    }
                    else if(obsrValue.equals("3")){
                        obsrValue="눈";
                    }
                    else if(obsrValue.equals("5")){
                        obsrValue="빗방울";
                    }
                    else if(obsrValue.equals("6")){
                        obsrValue="빗방울눈날림";
                    }
                    else if(obsrValue.equals("7")){
                        obsrValue="눈날림";
                    }

                    TextView t2 = findViewById(R.id.tx2);
                    String finalObsrValue = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t2.setText(finalObsrValue);
                        }
                    });
                    //str1=obsrValue;
                }
                if(category.equals("REH")){
                    //category = "습도";
                    TextView t3 = findViewById(R.id.tx3);
                    String finalObsrValue5 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t3.setText(finalObsrValue5 + "%");
                        }
                    });
                    //array[0] = obsrValue;
                }
                else if(category.equals("RN1")){
                    //category = "1시간 강수량";
                    TextView t4 = findViewById(R.id.tx4);
                    //t4.setText("1시간 강수량: "+obsrValue);
                    String finalObsrValue6 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t4.setText(finalObsrValue6 + "mm");
                        }
                    });
                }
                else if(category.equals("T1H")){
                    //category = "기온";
                    TextView t5 = findViewById(R.id.tx5);
                    Log.d("ob",obsrValue);

                    String finalObsrValue5 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t5.setText(finalObsrValue5 + "ºC");
                        }
                    });
                    //T1H.setText(obsrValue);
                }
                else if(category.equals("WSD")){
                    //category = "풍속";
                    TextView t9 = findViewById(R.id.tx9);
                    String finalObsrValue1 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t9.setText(finalObsrValue1 + "m/s");
                        }
                    });
                }

                //Log.d("array", String.valueOf(array));
                store.add(obsrValue);
                //Log.d("ArrayList", String.valueOf(store));
            }

            ImageView imageView;
            imageView = findViewById(R.id.image);

            Log.d("ArrayList", String.valueOf(store));
            String e0 = store.get(0).toString();//강수형태
            String e1 = store.get(1).toString();//습도
            String e2 = store.get(2).toString();//강수량
            String e3 = store.get(3).toString();//기온
            String e7 = store.get(7).toString();//풍속
            Log.d("list0",e0);
            Log.d("list1",e1);

            double wnd;

            //wnd = Double.parseDouble(e7);
            wnd = Double.parseDouble(e7);
            Log.d("wnd", String.valueOf(wnd));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(e0.equals("없음") && wnd<9){
                        imageView.setImageResource(R.drawable.good);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //api Json파싱
    public void JsonParse2(String str) {
        //String str1 = null;
        JSONObject obj;

        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);
        String[] array2 = te1.split(" ");
        String local2 = array2[1];
        Log.d("local3",local2);

        try {
            store2.clear();
            store3.clear();
            obj = new JSONObject(str);
            JSONObject response = obj.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONArray arr = body.getJSONArray("items");
            for(int i = 0;i<arr.length();i++){

                JSONObject job = arr.getJSONObject(i);

                String cityname = job.getString("cityName");
                String pm10 = job.getString("pm10Value");

                Log.d("cityname",cityname);
                Log.d("pm10",pm10);

//                if(cityname.equals("천안시")){
//                    Log.d("chpm",pm10);
//                }else if(cityname.equals("아산시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("공주시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("금산군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("논산시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("당진시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("보령시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("부여군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("서산시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("서천군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("예산군")){
//                    Log.d("aspm",pm10);
//                }
//                else if(cityname.equals("청양군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("태안군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("홍성군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("계룡시")){
//                    Log.d("aspm",pm10);
//                }

                store2.add(pm10);
                Log.d("store2", String.valueOf(store2));

                store3.add(cityname);
                Log.d("store3", String.valueOf(store3));



            }
            String cut[] = store2.toString().split(",");
            for(int j=0; j<cut.length;j++){
                Log.d("dust",j+cut[j]);
            }

            String cut2[] = store3.toString().split(",");
            Log.d("citt",cut2[10]);

            if(local2.equals("천안시")){
                Log.d("chdust",cut[10]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String ch = cut[10] + cut2[10];
                        TextView textView = findViewById(R.id.air);
                        textView.setText(ch);
                    }
                });
            }
//            else if(local2.equals("청양군")){
//                Log.d("chdust",cut[11]);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String ch = cut[11];
//                        TextView textView = findViewById(R.id.air);
//                        textView.setText(ch);
//                    }
//                });
//            }else if(local2.equals("아산시")){
//                Log.d("chdust",cut[8]);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String ch = cut[8];
//                        TextView textView = findViewById(R.id.air);
//                        textView.setText(ch);
//                    }
//                });
//            }
//            ImageView imageView;
//            imageView = findViewById(R.id.image);
//
//            Log.d("ArrayList", String.valueOf(store));
//            String e0 = store.get(0).toString();//강수형태
//            String e1 = store.get(1).toString();//습도
//            String e2 = store.get(2).toString();//강수량
//            String e3 = store.get(3).toString();//기온
//            String e7 = store.get(7).toString();//풍속
//            Log.d("list0",e0);
//            Log.d("list1",e1);
//
//            double wnd;
//
//            //wnd = Double.parseDouble(e7);
//            wnd = Double.parseDouble(e7);
//            Log.d("wnd", String.valueOf(wnd));
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(e0.equals("없음") && wnd<9){
//                        imageView.setImageResource(R.drawable.good);
//                    }
//                }
//            });




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void JsonParse3(String str) {
        //String str1 = null;
        JSONObject obj;

        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);
        String[] array2 = te1.split(" ");
        String local2 = array2[1];
        Log.d("local3",local2);

        try {
            store2.clear();
            obj = new JSONObject(str);
            JSONObject response = obj.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONArray arr = body.getJSONArray("items");
            for(int i = 0;i<arr.length();i++){

                JSONObject job = arr.getJSONObject(i);

                String cityname = job.getString("cityName");
                String pm10 = job.getString("pm10Value");

                Log.d("cityname",cityname);
                Log.d("pm10",pm10);

//                if(cityname.equals("천안시")){
//                    Log.d("chpm",pm10);
//                }else if(cityname.equals("아산시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("공주시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("금산군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("논산시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("당진시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("보령시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("부여군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("서산시")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("서천군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("예산군")){
//                    Log.d("aspm",pm10);
//                }
//                else if(cityname.equals("청양군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("태안군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("홍성군")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("계룡시")){
//                    Log.d("aspm",pm10);
//                }

                store2.add(pm10);
                Log.d("store2", String.valueOf(store2));



            }
            String cut[] = store2.toString().split(",");
            for(int j=0; j<cut.length;j++){
                Log.d("dust",j+cut[j]);
            }


            String cut2[] = store3.toString().split(",");
            Log.d("citt",cut2[10]);

            if(local2.equals("아산시")){
                Log.d("asdust",cut[8]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String ch = cut[8] + cut2[8];
                        TextView textView = findViewById(R.id.air);
                        textView.setText(ch);
                    }
                });
            }
//            ImageView imageView;
//            imageView = findViewById(R.id.image);
//
//            Log.d("ArrayList", String.valueOf(store));
//            String e0 = store.get(0).toString();//강수형태
//            String e1 = store.get(1).toString();//습도
//            String e2 = store.get(2).toString();//강수량
//            String e3 = store.get(3).toString();//기온
//            String e7 = store.get(7).toString();//풍속
//            Log.d("list0",e0);
//            Log.d("list1",e1);
//
//            double wnd;
//
//            //wnd = Double.parseDouble(e7);
//            wnd = Double.parseDouble(e7);
//            Log.d("wnd", String.valueOf(wnd));
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(e0.equals("없음") && wnd<9){
//                        imageView.setImageResource(R.drawable.good);
//                    }
//                }
//            });




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}


