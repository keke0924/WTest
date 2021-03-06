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

        //???????????? ????????????
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);

        String[] array = te1.split(" ");
        String local = array[1];
        Log.d("local2",local);

        if(local.equals("?????????")){
            getAirData();
        }else if(local.equals("?????????")){
            getAirData2();
        }

        //getData2();



//????????????
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

        //?????? ??????
        SimpleDateFormat real_time = new SimpleDateFormat("yyyyMMdd");//????????????(ex.20220313)
        Date time = new Date();
        String base_date = real_time.format(time);
        //String base_date ="20220317";

        //?????? ??????
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE,-1);
        Log.d("yester", String.valueOf(calendar));


        SimpleDateFormat f2 = new SimpleDateFormat("Hmm");
        String base_time = f2.format(time);
        Log.d("time2",base_time);
        int t = Integer.parseInt(base_time);
        Log.d("t", String.valueOf(t));


        //?????? API??? ????????? 40?????? ???????????? ???.
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
            //24???~24???40??? -> ?????? 11??? 30??? ?????????.
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

        //????????? ???????????? ??????
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=%EB%AF%B8%EC%84%B8%EB%A8%BC%EC%A7%80";

        //???????????? ????????????
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
//                    msg = elements.get(0).text();//??????
//                    msg3 = elements.get(3).text();//??????
//                    msg4 = elements.get(6).text();//??????
//                    msg5 = elements.get(9).text();//??????
//                    msg6 = elements.get(12).text();//??????
//                    msg7 = elements.get(15).text();//??????
//                    msg8 = elements.get(18).text();//??????
//                    msg9 = elements.get(21).text();//??????
//                    msg10 = elements.get(24).text();//??????
//                    msg11 = elements.get(27).text();//??????
//                    msg12 = elements.get(30).text();//??????
//                    msg13 = elements.get(33).text();//??????
//                    msg14 = elements.get(36).text();//??????
//                    msg2 = elements.get(39).text();//??????
//                    msg15 = elements.get(42).text();//??????
//                    msg16 = elements.get(45).text();//??????
//                    msg17 = elements.get(48).text();//??????
                    //Log.d("msg",msg8);
                    TextView air = findViewById(R.id.air);
                    //air.setText(msg8);

                    //???????????? ???????????? url
                    String url2 = "https://m.search.naver.com/search.naver?where=m&sm=mtb_etc&mra=blQ3&qvt=0&query=%EC%B6%A9%EB%82%A8%20%EB%AF%B8%EC%84%B8%EB%A8%BC%EC%A7%80";
                    Document doc2 = null;
                    doc2 = Jsoup.connect(url2).get();
                    Elements element = doc2.select("div.map_life.ct15 div.lcl_lst span");

                    //??????
                    str1 = element.get(1).text();
                    Log.d("str1", str1);

                    //??????
                    String str2;
                    str2 = element.get(3).text();
                    Log.d("str2", str2);

                    //??????
                    String str3;
                    str3 = element.get(5).text();
                    Log.d("str3", str3);

                    //??????
                    String str4;
                    str4 = element.get(7).text();
                    Log.d("str4", str4);

                    //?????? ?????? test. ????????? ???.
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                    Date now = new Date();
                    String nowTime = sdf1.format(now);

                    //??????????????? ui????????? ?????? runOn~~?????????.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (local.equals("?????????")) {
                                air.setText(str1);
                            } else if (local.equals("?????????")) {
                                air.setText(str2);
                            } else if (local.equals("?????????")) {
                                air.setText(str3);
                            } else if (local.equals("?????????")) {
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
        //???????????? ????????????
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("text",te1);

        String[] array = te1.split(" ");
        String local = array[0];
        Log.d("local2",local);

        String[] array2 = te1.split(" ");
        String local2 = array2[1];
        Log.d("local3",local2);

        if(local.equals("????????????")){
            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
        }
//        else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
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
        //???????????? ????????????
        TextView tex = (TextView) findViewById(R.id.tx);
        String te1 = tex.getText().toString();
        Log.d("texttt",te1);

        String[] array = te1.split(" ");
        String local = array[0];
        Log.d("localll2",local);

        String[] array2 = te1.split(" ");
        String local2 = array2[1];
        Log.d("localll3",local2);

        if(local.equals("????????????")){
            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
        }
//        else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
//        }else if(local.equals("???????????????")){
//            qurl = "http://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureSidoLIst?sidoName=??????&searchCondition=HOUR&pageNo=1&numOfRows=100&returnType=json&serviceKey=hjnA51g4D5Jh5pMY%2BL17qEO87IpWw2ZtkiEspqyL9J57fOGtZztzdvGTWgS0dx19sDxNR4G4URiEfN1kHMuSPA%3D%3D";
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


    //???,?????? -> ?????? ????????????
    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // ?????? ??????(km)
        double GRID = 5.0; // ?????? ??????(km)
        double SLAT1 = 30.0; // ?????? ??????1(degree)
        double SLAT2 = 60.0; // ?????? ??????2(degree)
        double OLON = 126.0; // ????????? ??????(degree)
        double OLAT = 38.0; // ????????? ??????(degree)
        double XO = 43; // ????????? X??????(GRID)
        double YO = 136; // ????????? Y??????(GRID)


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
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //?????? ?????? ????????? ??? ??????
                ;
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MainActivity.this, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????


        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(MainActivity.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "???????????? ????????????!", Toast.LENGTH_LONG).show();
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
//                        // ?????? ??????
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

            return "?????? ?????????";
        }


        //?????????????????? ????????? ?????? ??????
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


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("aaa", "onActivityResult : GPS ????????? ?????????");
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



    //api Json??????
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
                        obsrValue="??????";
                    }
                    else if(obsrValue.equals("1")){
                        obsrValue="???";
                    }
                    else if(obsrValue.equals("2")){
                        obsrValue="???/???";
                    }
                    else if(obsrValue.equals("3")){
                        obsrValue="???";
                    }
                    else if(obsrValue.equals("5")){
                        obsrValue="?????????";
                    }
                    else if(obsrValue.equals("6")){
                        obsrValue="??????????????????";
                    }
                    else if(obsrValue.equals("7")){
                        obsrValue="?????????";
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
                    //category = "??????";
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
                    //category = "1?????? ?????????";
                    TextView t4 = findViewById(R.id.tx4);
                    //t4.setText("1?????? ?????????: "+obsrValue);
                    String finalObsrValue6 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t4.setText(finalObsrValue6 + "mm");
                        }
                    });
                }
                else if(category.equals("T1H")){
                    //category = "??????";
                    TextView t5 = findViewById(R.id.tx5);
                    Log.d("ob",obsrValue);

                    String finalObsrValue5 = obsrValue;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t5.setText(finalObsrValue5 + "??C");
                        }
                    });
                    //T1H.setText(obsrValue);
                }
                else if(category.equals("WSD")){
                    //category = "??????";
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
            String e0 = store.get(0).toString();//????????????
            String e1 = store.get(1).toString();//??????
            String e2 = store.get(2).toString();//?????????
            String e3 = store.get(3).toString();//??????
            String e7 = store.get(7).toString();//??????
            Log.d("list0",e0);
            Log.d("list1",e1);

            double wnd;

            //wnd = Double.parseDouble(e7);
            wnd = Double.parseDouble(e7);
            Log.d("wnd", String.valueOf(wnd));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(e0.equals("??????") && wnd<9){
                        imageView.setImageResource(R.drawable.good);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //api Json??????
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

//                if(cityname.equals("?????????")){
//                    Log.d("chpm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }
//                else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
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

            if(local2.equals("?????????")){
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
//            else if(local2.equals("?????????")){
//                Log.d("chdust",cut[11]);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String ch = cut[11];
//                        TextView textView = findViewById(R.id.air);
//                        textView.setText(ch);
//                    }
//                });
//            }else if(local2.equals("?????????")){
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
//            String e0 = store.get(0).toString();//????????????
//            String e1 = store.get(1).toString();//??????
//            String e2 = store.get(2).toString();//?????????
//            String e3 = store.get(3).toString();//??????
//            String e7 = store.get(7).toString();//??????
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
//                    if(e0.equals("??????") && wnd<9){
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

//                if(cityname.equals("?????????")){
//                    Log.d("chpm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }
//                else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
//                    Log.d("aspm",pm10);
//                }else if(cityname.equals("?????????")){
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

            if(local2.equals("?????????")){
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
//            String e0 = store.get(0).toString();//????????????
//            String e1 = store.get(1).toString();//??????
//            String e2 = store.get(2).toString();//?????????
//            String e3 = store.get(3).toString();//??????
//            String e7 = store.get(7).toString();//??????
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
//                    if(e0.equals("??????") && wnd<9){
//                        imageView.setImageResource(R.drawable.good);
//                    }
//                }
//            });




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}


