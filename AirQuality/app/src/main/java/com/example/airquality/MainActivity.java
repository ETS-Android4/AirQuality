package com.example.airquality;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private static TextView textViewco;
    private static TextView textViewco2;
    private static TextView textViewTemp;
    private static TextView textViewcoHum;
    private static TextView infoPanel;
    private static TextView textViewlpg;
    private static TextView textViewpropan;
    private static TextView textViewdust;
    private static TextView textstatusCO;
    private static TextView textstatusCO2;
    private static TextView textstatusTemp;
    private static TextView textstatusHum;
    private static TextView textstatusLPG;
    private static TextView textstatusPropan;
    private static TextView textstatusDust;
    private static ImageView imageViewCo;
    private static ImageView imageViewCo2;
    private static ImageView imageViewtemp;
    private static ImageView imageViewhum;
    private static ImageView imageViewlpg;
    private static ImageView imageViewpropan;
    private static ImageView imageViewdust;
    private static LinearLayout linearLayout1;//co
    private static LinearLayout linearLayout2;//co2
    private static LinearLayout linearLayout3;//teplota
    private static LinearLayout linearLayout4;//vlhkost
    private static LinearLayout linearLayout5;//lpg
    private static LinearLayout linearLayout6;//propan
    private static LinearLayout linearLayout7;//prach

    //hodnoty
    private static String co;
    private static String co2;
    private static String temp;
    private static String hum;
    private static String status;
    private static String time;
    private String mojeID;
    private static String notifiTime;
    int casovac = 0;
    public static boolean stav = true;
    private static boolean visible = true;
    private static String lpg;
    private static String propan;
    private static String dust;
    //boolean status
    private static boolean bco;
    private static boolean bco2;
    private static boolean btemp;
    private static boolean bhum;
    private static boolean bdust;
    private static boolean blpg;
    private static boolean bpropan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(haveNetworkConnection()){
            setContentView(R.layout.activity_main);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            databaseReference = firebaseData.getReference();
            linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
            linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
            linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
            linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
            linearLayout5 = (LinearLayout) findViewById(R.id.linearLayout5);
            linearLayout6 = (LinearLayout) findViewById(R.id.linearLayout6);
            linearLayout7 = (LinearLayout) findViewById(R.id.linearLayout7);
            textViewlpg = (TextView) findViewById(R.id.textviewlpg);
            textViewdust = (TextView) findViewById(R.id.textviewdust);
            textViewpropan = (TextView) findViewById(R.id.textviewpropan);
            textViewco = (TextView) findViewById(R.id.textviewco);
            textViewco2 = (TextView) findViewById(R.id.textviewco2);
            textViewTemp = (TextView) findViewById(R.id.textviewTeplota);
            textViewcoHum = (TextView) findViewById(R.id.textviewVlhkost);
            infoPanel = (TextView) findViewById(R.id.info_panel);
            imageViewCo = (ImageView) findViewById(R.id.imgCoStatus);
            imageViewCo2 = (ImageView) findViewById(R.id.imageco2status);
            imageViewtemp = (ImageView) findViewById(R.id.imagetemperaturestatus);
            imageViewhum = (ImageView) findViewById(R.id.imagehumiditystatus);
            imageViewlpg = (ImageView) findViewById(R.id.imagelpgstatus);
            imageViewpropan = (ImageView) findViewById(R.id.imeagepropanstatus);
            imageViewdust = (ImageView) findViewById(R.id.imageduststatus);
            textstatusCO = (TextView) findViewById(R.id.textstatusco);
            textstatusCO2 = (TextView) findViewById(R.id.textstatusco2);
            textstatusTemp = (TextView) findViewById(R.id.textstautstemp);
            textstatusHum = (TextView) findViewById(R.id.textstatushum);
            textstatusLPG = (TextView) findViewById(R.id.textstavlpg);
            textstatusPropan = (TextView) findViewById(R.id.textviewstavpropanstav);
            textstatusDust = (TextView) findViewById(R.id.textviewstavduststav);
            createNotification();
            getActuallData();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("No wifi conection.");
            progressDialog.setCancelable(true);
            progressDialog.show();
           new Thread(new Runnable() {
               @Override
               public void run() {
                  try {
                      synchronized (this){
                          wait(2000);
                          progressDialog.dismiss();
                          wait(1000);
                          finish();
                      }
                  } catch (Exception e){
                      e.printStackTrace();
                  }
               }
           }).start();

        }
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void getActuallData() {

        databaseReference.child("Data").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (returnStatus()==true) {
                    String value = snapshot.getValue().toString();
                    String[] array = null;
                    array = value.split(",");
                    co = array[1];
                    co2 = array[2];
                    lpg = array[3];
                    propan = array[4];
                    temp = array[5];
                    hum = array[6];
                    dust = array[7];
                    status = array[8];
                    time = array[10];
                    //time
                    String cas = "";
                    String minuty = "";
                    String sekundy = "";
                    minuty = time.substring(2, 5);
                    sekundy = time.substring(5, 8);
                    System.out.println(sekundy + " sekundy");
                    cas = time.substring(0, 2);
                    notifiTime = cas + minuty + sekundy;
                    //CO
                    if(co.equals("-987.654")){
                        textViewco.setText("Error Sensor");

                        infoPanel.setText("Problem with sensor !");
                        imageViewCo.setBackgroundColor(getResources().getColor(R.color.orange));
                        textstatusCO.setText("Error");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look like sensor MQ07 dont work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100,builder.build());
                    }
                    if(co.equals(" INF")){
                        infoPanel.setBackgroundColor(getResources().getColor(R.color.warning));
                        infoPanel.setText("Leave this room immediately !");
                        imageViewCo.setBackgroundColor(getResources().getColor(R.color.warning));
                        textstatusCO.setText("Dangerous");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("CO - Health threatening " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("CO level - Health threatening in few minutes. Dont stay in this room/open windows."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100,builder.build());
                        bco = false;
                    }
                    else {
                        int pom_status = Integer.parseInt(status);
                        double pom_co = Double.parseDouble(co);
                        if (pom_status != 2) {
                            textViewco.setText("Heating!");
                        } else {
                            textViewco.setText(pom_co + " ppm");
                        }
                        if (pom_co < 6.0) {
                            infoPanel.setBackgroundColor(getResources().getColor(R.color.green));
                            infoPanel.setText("Healthy Air Quality");
                            imageViewCo.setBackgroundColor(getResources().getColor(R.color.green));
                            textstatusCO.setText("Excellent");
                            bco = true;
                        } else if (pom_co > 6.0 && pom_co < 35) {
                            infoPanel.setBackgroundColor(getResources().getColor(R.color.orange));
                            infoPanel.setText("Low health Risk");
                            imageViewCo.setBackgroundColor(getResources().getColor(R.color.orange));
                            textstatusCO.setText("Low");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("CO - Low health Risk " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("CO level " + pom_co + " - low health risk. Physical symptoms after 6-8 hours." +
                                            "CO level - low health risk. Physical symptoms after 6-8 hours.CO level - low health risk. Physical symptoms after 6-8 hours."))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100,builder.build());
                            bco = false;
                         } else if (pom_co > 35 && pom_co < 400) {
                            infoPanel.setBackgroundColor(getResources().getColor(R.color.red));
                            infoPanel.setText("High health Risk");
                            textstatusCO.setText("Bad");
                            imageViewCo.setBackgroundColor(getResources().getColor(R.color.red));
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("CO - High health Risk " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("CO level " + pom_co + " - High health risk. Physical symptoms in 45 minutes. Unconscious in 2 hours. Fatal in 2-3 hours."))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100,builder.build());
                            bco = false;
                        }
                        else {
                            infoPanel.setBackgroundColor(getResources().getColor(R.color.warning));
                            infoPanel.setText("Leave this room immediately !");
                            imageViewCo.setBackgroundColor(getResources().getColor(R.color.warning));
                            textstatusCO.setText("Dangerous");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("CO - Health threatening " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("CO level " + pom_co + " - Health threatening in few minutes. Dont stay in this room/open windows."))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100,builder.build());
                            bco = false;
                        }
                    }
                    //temperature
                    if(temp.equals("-987.654")) {
                        textViewTemp.setText("Error Sensor");
                        imageViewtemp.setBackgroundColor(getResources().getColor(R.color.orange));
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look like sensor DHT11 dont work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    }
                    if(temp.equals(" INF")){
//                        double pom_temp = Double.parseDouble(temp);
//                        textViewTemp.setText(pom_temp + "C");
                        imageViewtemp.setBackgroundColor(getResources().getColor(R.color.red));
                        textstatusTemp.setText("Error");
                        infoPanel.setBackgroundColor(getResources().getColor(R.color.warning));
                        infoPanel.setText("Too hot, bad Air Condition !");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Temperature too high !" + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Temperature too high !"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                        btemp = false;
                    } else {
                        double pom_temp = Double.parseDouble(temp);
                        textViewTemp.setText(pom_temp + "C");
                        if(pom_temp < 16){
                            imageViewtemp.setBackgroundColor(getResources().getColor(R.color.ice));
                             textstatusTemp.setText("Cold");
//                            infoPanel.setBackgroundColor(getResources().getColor(R.color.ice));
//                            infoPanel.setText("Cold not good for Human Health !");
                            btemp = false;
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
//                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Temperature too low " + notifiTime)
//                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Temperature " + pom_temp + " is very cold !"))
//                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
//                            notificationManagerCompat.notify(100, builder.build());
                        } else if (pom_temp > 36) {
                            imageViewtemp.setBackgroundColor(getResources().getColor(R.color.red));
                            textstatusTemp.setText("Warm");
                            infoPanel.setBackgroundColor(getResources().getColor(R.color.red));
                            infoPanel.setText("Too hot, bad Air Condition !");
                            btemp = false;
                        } else {
                            imageViewtemp.setBackgroundColor(getResources().getColor(R.color.green));
                            textstatusTemp.setText("Excellent");
                            infoPanel.setBackgroundColor(getResources().getColor(R.color.green));
                            infoPanel.setText("Healthy Air Quality !");
                            btemp = true;
                        }
                    }
                    //humidity
                    if(hum.equals("-987.654")) {
                        textViewcoHum.setText("Error Sensor");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look like sensor DHT11 dont work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                        imageViewhum.setBackgroundColor(getResources().getColor(R.color.orange));
                        textstatusHum.setText("Error");
                    }
                    if(hum.equals(" INF")){
                        imageViewhum.setBackgroundColor(getResources().getColor(R.color.red));
                        textstatusHum.setText("High");
                        bhum = false;
                    } else {
                        double pom_hum = Double.parseDouble(hum);
                        textViewcoHum.setText(pom_hum + "%");
                        if(pom_hum > 60){
                            imageViewhum.setBackgroundColor(getResources().getColor(R.color.red));
                            textstatusHum.setText("High");
//                            infoPanel.setBackgroundColor(getResources().getColor(R.color.red));
//                            infoPanel.setText("Problem with Humidity !");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Humidity too high " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Humidity level " + pom_hum +" too high, warning health risk !"))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            bhum = false;
                        } else if (pom_hum <= 30){
                            imageViewhum.setBackgroundColor(getResources().getColor(R.color.red));
                            textstatusHum.setText("Low");
//                            infoPanel.setBackgroundColor(getResources().getColor(R.color.red));
//                            infoPanel.setText("Problem with Humidity !");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Humidity too low " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Humidity level " + pom_hum +" too low, risk - respiration problem !"))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            bhum = false;
                        } else {
                            imageViewhum.setBackgroundColor(getResources().getColor(R.color.green));
                            textstatusHum.setText("Excellent");
                            bhum = true;
                        }
                    }

                    if (co2.equals("-987,654")){
                        imageViewCo2.setBackgroundColor(getResources().getColor(R.color.orange));
                        textstatusCO2.setText("Error");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look like sensor MQ135 do not work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    }
                    if(co2.equals(" INF")){
                        bco2 = false;
                        imageViewCo2.setBackgroundColor(getResources().getColor(R.color.warning));
                        textstatusCO2.setText("Very Bad");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("CO2 critical value " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Big health risk - leave room immediately!"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    } else {
                        double pom_co2 = Double.parseDouble(co2);
                        textViewco2.setText(pom_co2 + " ppm");
                        if(pom_co2 <= 600) {
                            textstatusCO2.setText("Excellent");
                            imageViewCo2.setBackgroundColor(getResources().getColor(R.color.green));
                            bco2 = true;
                        } else if (pom_co2 > 600 && pom_co2 <= 1000){
                            imageViewCo2.setBackgroundColor(getResources().getColor(R.color.orange));
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("CO2 low risk  " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Low health risk " + pom_co2 + "- open windows for fresh air ! "))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100, builder.build());
                            textstatusCO2.setText("Low");
                            bco2 = false;
                        } else {
                            bco2 = false;
                            imageViewCo2.setBackgroundColor(getResources().getColor(R.color.warning));
                            textstatusCO2.setText("Dangerous");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("CO2 big risk " + pom_co2 + "- leave room immediately!"))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100, builder.build());
                        }
                    }

                    if(dust.equals("-987,654")){
                        imageViewdust.setBackgroundColor(getResources().getColor(R.color.orange));
                        textstatusDust.setText("Error");
                        textViewdust.setText("Error Sensor");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look like dust sensor do not work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    }
                    if(dust.equals(" INF")){
                        bdust = false;
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dust value too high " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Hazardous dust concentration , Healt alert everyone may experience more serious health effects !"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                        imageViewdust.setBackgroundColor(getResources().getColor(R.color.warning));
                        textstatusDust.setText("Dangerous");
                    } else {
                        double pom_dust = Double.parseDouble(dust);
                        if(pom_dust >= 0){
                             textViewdust.setText(pom_dust + " u/m3");
                             if(pom_dust > 300){
                                 bdust = false;
                                 imageViewdust.setBackgroundColor(getResources().getColor(R.color.warning));
                                 textstatusDust.setText("Dangerous");
                                 NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                         .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dangerous dust value " + notifiTime)
                                         .setStyle(new NotificationCompat.BigTextStyle().bigText("Dust " + pom_dust + " u/m3 Everyone may experience more serious health effects."))
                                         .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                 NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                                 notificationManagerCompat.notify(100, builder.build());
                             } else if (pom_dust > 50 && pom_dust <= 150){
                                 bdust = false;
                                 imageViewdust.setBackgroundColor(getResources().getColor(R.color.orange));
                                 textstatusDust.setText("Low");
                                 NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                         .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dust value out off norm " + notifiTime)
                                         .setStyle(new NotificationCompat.BigTextStyle().bigText("Dust " + pom_dust + " u/m3 Members of sensitive groups may experience health effects."))
                                         .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                 NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                                 notificationManagerCompat.notify(100, builder.build());
                             } else if (pom_dust > 150 && pom_dust <= 300){
                                 bdust = false;
                                 imageViewdust.setBackgroundColor(getResources().getColor(R.color.red));
                                 textstatusDust.setText("High");
                                 NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                         .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dust value too high " + notifiTime)
                                         .setStyle(new NotificationCompat.BigTextStyle().bigText("Dust " + pom_dust + " u/m3 Members of sensitive groups may experience health effects."))
                                         .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                 NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                                 notificationManagerCompat.notify(100, builder.build());
                             } else {

                                 bdust = true;
                                 imageViewdust.setBackgroundColor(getResources().getColor(R.color.green));
                                 textstatusDust.setText("Excellent");
                             }
                        }
                    }

                    if(lpg.equals("-987,654")){
                        textstatusLPG.setText("Error");
                        imageViewlpg.setBackgroundColor(getResources().getColor(R.color.orange));
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look sensor MQ02 do not work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    }
                    if(lpg.equals(" INF")){
                        blpg = false;
                        textstatusLPG.setText("Dangerous");
                        imageViewlpg.setBackgroundColor(getResources().getColor(R.color.warning));
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dangerous value of LPG " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Leave this room immediately."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());

                    } else {
                        double pom_lpg = Double.parseDouble(lpg);
                        textViewlpg.setText(pom_lpg + " ppm");
                        if(pom_lpg > 1600){
                            blpg = false;
                            textstatusLPG.setText("Explosion");
                            imageViewlpg.setBackgroundColor(getResources().getColor(R.color.red));
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dangerous value of LPG " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("There are big risk of explosion and health problem ! Ppm value of LPG : " + pom_lpg))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100, builder.build());
                        } else {
                            blpg = true;
                            textstatusLPG.setText("Excellent");
                            imageViewlpg.setBackgroundColor(getResources().getColor(R.color.green));
                        }
                    }

                    if(propan.equals("-987,654")){
                        textstatusPropan.setText("Error");
                        imageViewpropan.setBackgroundColor(getResources().getColor(R.color.orange));
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Problem with sensor " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Ouups...Its look sensor MQ02 do not work correctly."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    }
                    if(propan.equals(" INF")){
                        bpropan = false;
                        textstatusPropan.setText("Dangerous");
                        imageViewpropan.setBackgroundColor(getResources().getColor(R.color.warning));
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dangerous value of propan " + notifiTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Leave this room immediately !"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(100, builder.build());
                    } else {
                        double pom_propan = Double.parseDouble(propan);
                        textViewpropan.setText(pom_propan + " ppm");
                        if(pom_propan > 5){
                            bpropan = false;
                            textstatusPropan.setText("Bad");
                            imageViewpropan.setBackgroundColor(getResources().getColor(R.color.red));
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "lembitA")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp).setContentTitle("Dangerous value of propan " + notifiTime)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Warning big health risk big value of propan " + pom_propan + " ppm "))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(100, builder.build());
                        } else {
                            bpropan = true;
                            textstatusPropan.setText("Excellent");
                            imageViewpropan.setBackgroundColor(getResources().getColor(R.color.green));
                        }
                    }


                    if(bco==true && bco2 == true && bdust == true && bhum == true && btemp == true
                    && blpg==true && bpropan==true){
                        infoPanel.setBackgroundColor(getResources().getColor(R.color.green));
                        infoPanel.setText("Healthy Air Quality !");
                    } else {
                        infoPanel.setBackgroundColor(getResources().getColor(R.color.orange));
                        infoPanel.setText("Problem with Air Quality !");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void createNotification(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "studentChannel";
            String description = "Chanel for student notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("lembitA", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static boolean returnStatus(){
        return stav;
    }

    public static void setOn(){
        stav = true;
    }

    public static void setOff(){
        stav = false;
        Thread thread = new Thread();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setOn();
            }
        }, 10000);
    }

    public void nextData(View view) {
        if(visible){
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            linearLayout4.setVisibility(View.GONE);
            //
            linearLayout5.setVisibility(View.VISIBLE);
            linearLayout6.setVisibility(View.VISIBLE);
            linearLayout7.setVisibility(View.VISIBLE);
            visible = false;
        } else {
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            linearLayout4.setVisibility(View.VISIBLE);
            //
            linearLayout5.setVisibility(View.GONE);
            linearLayout6.setVisibility(View.GONE);
            linearLayout7.setVisibility(View.GONE);
            visible = true;
        }
    }

    public void openCO2(View view) {
        setOff();
        Intent intent = new Intent(MainActivity.this, OxidCo2.class);
        startActivity(intent);
    }

    public void openTemperature(View view) {
        setOff();
        Intent intent = new Intent(MainActivity.this, Temperature.class);
        startActivity(intent);
    }

    public void openHumidity(View view) {
        setOff();
        Intent intent = new Intent(this,Humidity.class);
        startActivity(intent);
    }

    public void openOxid(View view) {
        setOff();
        Intent intent = new Intent(MainActivity.this, OxidCO.class);
        startActivity(intent);
    }

    public void openLPG(View view) {
        setOff();
        Intent intent = new Intent(MainActivity.this, LPG.class);
        startActivity(intent);
    }

    public void openPropan(View view) {
        setOff();
        Intent intent = new Intent(MainActivity.this, Propan.class);
        startActivity(intent);
    }

    public void openDust(View view) {
        setOff();
        Intent intent = new Intent(MainActivity.this, Dust.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goback:
                finish();
                break;
            case R.id.Co:
                Intent coIntent = new Intent(this,OxidCO.class);
                startActivity(coIntent);
                break;
            case R.id.Co2:
                Intent co2Intent = new Intent(this,OxidCo2.class);
                startActivity(co2Intent);
                break;
            case R.id.temp:
                Intent tempIntent = new Intent(this,Temperature.class);
                startActivity(tempIntent);
                break;
            case R.id.hum:
                Intent humIntent = new Intent(this,Humidity.class);
                startActivity(humIntent);
                break;
            case R.id.dust:
                Intent dustIntent = new Intent(this,Dust.class);
                startActivity(dustIntent);
                break;
            case R.id.propan:
                Intent propanIntent = new Intent(this,Propan.class);
                startActivity(propanIntent);
                break;
            case R.id.lpg:
                Intent lpgIntent = new Intent(this,LPG.class);
                startActivity(lpgIntent);
                break;
            case R.id.exit:
                this.finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}