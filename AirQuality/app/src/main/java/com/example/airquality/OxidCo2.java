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
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class OxidCo2 extends AppCompatActivity {

    private FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private static String co2;
    private static String time;
    private static String date;
    private BarChart barChart;
    private static TextView date2;
    private static TextView textViewCo2MaxMin;
    // private static Calendar calendar;
    private static int day;
    private static int month;
    private static int year;
    private static String id;
    private static String currentDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static Button buttonChangeDate;
    private static int numberOfValue = 0;
    private boolean ukoncenie = false;
    private static String pom_string_time = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxid_co2);
        barChart = findViewById(R.id.barChartCo2);
        buttonChangeDate = findViewById(R.id.buttonChangeDateCo2);
        textViewCo2MaxMin = findViewById(R.id.textCo2MaxMin);
        date2 = findViewById(R.id.date2);
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
        databaseReference = firebaseData.getReference();
        date2.setText(currentDate);
        buttonChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(OxidCo2.this,
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
                                date2.setText("");
                                date2.setText(year + "-" + pom + "-" + pom_day);
                                MainActivity.setOff();
                                getActuallData();
                            }
                        }, year, month, day);
                datePickerDialog.show();

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
        ProgressDialog progressDialog = new ProgressDialog(OxidCo2.this);
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
                    id = pom.getKey();
                    co2 = array[2];
                    date = array[9];
                    time = array[10];
                    String cas = "";
                    String minuty = "";
                    String sekundy = "";
                    String novyCas = time;
                    minuty = time.substring(2,5);
                    sekundy = time.substring(5,8);
                    cas = time.substring(0,2);
                    float pomCo2 = Float.parseFloat(co2);
                    if((!(sekundy.equals(":00")))){
                        databaseReference.child(id).removeValue();
                    }
                    if(jeDuplikant(novyCas)){
                        databaseReference.child(id).removeValue();
                    }
                    if(date.equals(currentDate)){
                        if(pomCo2 > max){
                            max = pomCo2;
                        }
                        if(pomCo2 < min){
                            min = pomCo2;
                        }
                        textViewCo2MaxMin.setText("Max. Value : " + max + " Min. Value : " + min);
                    }
                    celkovo.add(data);
                    currentDate = date2.getText().toString();
                    if(date.equals(currentDate)&&sekundy.equals(":00")){
                        arrayList.add(data);
                    }
                    if(date.equals(currentDate)  && sekundy.equals(":00") && minuty.equals(":00")){
                        pomCo2 = Float.parseFloat(co2);
                        float pom_cas = (float) Float.parseFloat(cas);
                        grafik.add(new BarEntry(pom_cas,pomCo2));
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
                //numberOfValue = arrayList.size();
                ukoncenie = true;
                // endTime = System.currentTimeMillis();
                if(arrayList.size() <= 0){
                    textViewCo2MaxMin.setText("No actual data");
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

    public void OxidCo2Details(View view) {
        String datum = currentDate;
        String graf = numberOfValue + "";
        Intent intent = new Intent(OxidCo2.this,OxidCo2Details.class);
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