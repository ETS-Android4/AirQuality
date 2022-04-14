package com.example.airquality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class Dust extends AppCompatActivity {

    private FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private static String dust;
    private static String time;
    private static String date;
    private BarChart barChart;
    private static TextView dateDust;
    private static TextView textCoMaxMinDust;
    // private static Calendar calendar;
    private static int day;
    private static String id;
    private static int month;
    private static int year;
    private static String status;
    private static String currentDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static Button buttonChangeDate;
    private static int numberOfValue = 0;
    private boolean ukoncenie = false;
    private static String pom_string_time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust);
        barChart = findViewById(R.id.barChartDust);
        buttonChangeDate = findViewById(R.id.buttonChangeDateDust);
        textCoMaxMinDust = findViewById(R.id.textCoMaxMinDust);
        dateDust = findViewById(R.id.dateDust);
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        month = month + 1;
        String pom_month = "";
        if(month < 10){
            pom_month = "0" + month;
        } else {
            pom_month = month + "";
        }
        String pom_day_this = "";
        if(day < 10){
            pom_day_this = "0" + day;
        } else {
            pom_day_this = day + "";
        }
        currentDate = year + "-" + pom_month + "-" + pom_day_this;
        System.out.println(currentDate + " moj datum");
        databaseReference = firebaseData.getReference();
        dateDust.setText(currentDate);
        buttonChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Dust.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear += 1;
                                String pom = "";
                                if(monthOfYear < 10){
                                    pom = "0" + monthOfYear;
                                } else {
                                    pom = monthOfYear + "";
                                }
                                String pom_day = "";
                                if(dayOfMonth < 10){
                                    pom_day = "0" + dayOfMonth;
                                } else {
                                    pom_day = dayOfMonth + "";
                                }
                                dateDust.setText("");
                                dateDust.setText(year + "-" + pom + "-" + pom_day);
                                MainActivity.setOff();
                                getActuallData();
                            }
                        }, year, month, day);
                datePickerDialog.show();
                //buttonChangeDate.setVisibility(View.GONE);
            }
        });
        getActuallData();
    }

    private boolean jeDuplikant(String mojcas){
        if(pom_string_time.equals(mojcas)){
            return true;
        } else {
            pom_string_time = mojcas;
            return false;
        }
    }

    private void getActuallData() {
        ProgressDialog progressDialog = new ProgressDialog(Dust.this);
        progressDialog.setMessage("Loading data please wait.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float max = 0;
                float min = Float.MAX_VALUE;
                List<String> arrayList = new LinkedList<>();
                List<String> celkovo = new LinkedList<>();
                List<BarEntry> grafik = new LinkedList<>();
                for (DataSnapshot pom : snapshot.getChildren()) {
                    String data = pom.getValue().toString();
                    String[] array = null;
                    array = data.split(",");
                    dust = array[7];
                    id = pom.getKey();
                    status = array[8];
                    date = array[9];
                    time = array[10];
                    String novyCas = time;
                    float pomDust = Float.parseFloat(dust);
                    String cas = "";
                    String minuty = "";
                    String sekundy = "";
                    minuty = time.substring(2,5);
                    sekundy = time.substring(5,8);
                    cas = time.substring(0,2);
                    if((!(sekundy.equals(":00")))){
                        databaseReference.child(id).removeValue();
                    }
                    if(jeDuplikant(novyCas)){
                        databaseReference.child(id).removeValue();
                    }
                    if(date.equals(currentDate)&&pomDust >=0){
                        if(pomDust > max){
                            max = pomDust;
                        }
                        if(pomDust < min){
                            min = pomDust;
                        }
                        textCoMaxMinDust.setText("Max. Value : " + max + " Min. Value : " + min);
                    }
                    celkovo.add(data);
                    currentDate = dateDust.getText().toString();
                    //zmena pridane minuty
                    if(date.equals(currentDate)&&pomDust >=0&& sekundy.equals(":00")){
                        arrayList.add(data);
                    }
                    if(date.equals(currentDate) && sekundy.equals(":00") && minuty.equals(":00")&&pomDust >=0){
                        pomDust = Float.parseFloat(dust);
                        float pom_cas = (float) Float.parseFloat(cas);
                        grafik.add(new BarEntry(pom_cas,pomDust));
                    }
                }
                if(grafik.size() <= 0){
                    barChart.clear();
                    barChart.setNoDataText("No actual data!");
                } else {
                    barChart.clear();
                    BarDataSet barDataSet = new BarDataSet(grafik,"Graf");

                    barDataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            double a = (double) value;
                            return String.format("%.2f",a);
                        }
                    });

                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);
                    barDataSet.setColor(Color.rgb(255,200,0));
                    BarData barData = new BarData(barDataSet);
                    barData.setBarWidth(1.0f);
                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Real data from firebase");
                    barChart.animateY(2000);
                }
                ukoncenie = true;
                // endTime = System.currentTimeMillis();
                if(arrayList.size() <= 0){
                    textCoMaxMinDust.setText("No actual data");
                }
                if(grafik.size() <= 0){
                    barChart.clear();
                    barChart.setNoDataText("No actual data!");
                } else {
                    barChart.clear();
                    BarDataSet barDataSet = new BarDataSet(grafik,"Graf");

                    barDataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            double a = (double) value;
                            return String.format("%.2f",a);
                        }
                    });
                    barChart.clear();
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);
                    barDataSet.setColor(Color.rgb(255,200,0));
                    BarData barData = new BarData(barDataSet);
                    barData.setBarWidth(1.0f);
                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Real data from firebase");
                    barChart.animateY(2000);
                }
                numberOfValue = arrayList.size();
                Thread thread = new Thread();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        System.out.println("Cakame");
                        if(ukoncenie){
                            progressDialog.show();
                            progressDialog.setCancelable(true);
                            progressDialog.dismiss();
                        }
                    }
                }, 2000);
                //thread.start();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void DustDetails(View view) {
        String datum = currentDate;
        String graf = numberOfValue + "";
        Intent intent = new Intent(this,DustDetails.class);
        intent.putExtra("date",datum);
        intent.putExtra("numberof",graf);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
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
