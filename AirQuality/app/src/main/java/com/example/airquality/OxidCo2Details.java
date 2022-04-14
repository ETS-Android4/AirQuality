package com.example.airquality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class OxidCo2Details extends AppCompatActivity {

    private FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    List<String> arrayList = null;
    List<String>  arrayListpom = null;
    ArrayList<String> skuska;
    private ListView lv;
    private String co2;
    private String time;
    private String date;
    private String status;
    private int akcia = 0;//1 sort by time //2 sort by value of co
    private boolean ukoncenie = false;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxid_co2_details);
        databaseReference = firebaseData.getReference();
        getActuallData();
    }

    private void getActuallData() {
        List<String>  arrayList = new ArrayList<>();
        ProgressDialog progressDialog = new ProgressDialog(OxidCo2Details.this);
        progressDialog.setMessage("Loading data please wait.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data");
        lv = (ListView) findViewById(R.id.listCO2);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent intent = getIntent();
                int counter = 1;
                String datum = intent.getStringExtra("date");
                String numberof = intent.getStringExtra("numberof");
                for (DataSnapshot pom : snapshot.getChildren()) {
                    String data = pom.getValue().toString();
                    String[] array = null;
                    array = data.split(",");
                    co2 = array[2];
                    date = array[9];
                    time = array[10];
                    float pomCo = Float.parseFloat(co2);
                    String cas = "";
                    String minuty = "";
                    String sekundy = "";
                    minuty = time.substring(2, 5);
                    sekundy = time.substring(5, 8);
                    cas = time.substring(0, 2);
                    if (date.equals(datum)&& sekundy.equals(":00")) {
                        String upraveneData = counter + "/" + numberof + " - " + time + " - value of CO2 ppm : " + " " + co2;
                        arrayList.add(upraveneData);
                        counter++;
                    }
                    if (akcia == 0) {
                       // LinkedList pomLinkedList = new LinkedList(arrayList);

                    }
                    if (akcia == 1) {
                        Collections.sort(arrayList);
                        Collections.sort(arrayList, new Comparator<String>() {
                            public int compare(String s1, String s2) {
                                double d1 = Double.valueOf(s1.substring(s1.lastIndexOf(' ') + 1));
                                double d2 = Double.valueOf(s2.substring(s2.lastIndexOf(' ') + 1));
                                return Double.compare(d1, d2);
                            }
                        });
                    }
                    if (akcia == 2) {
                        // Collections.sort(arrayList, Collections.reverseOrder());
                        Collections.sort(arrayList, new Comparator<String>() {
                            public int compare(String s1, String s2) {
                                double d1 = Double.valueOf(s1.substring(s1.lastIndexOf(' ') + 1));
                                double d2 = Double.valueOf(s2.substring(s2.lastIndexOf(' ') + 1));
                                return Double.compare(d2, d1);
                            }
                        });
                    }
                    //
                }
                ukoncenie = true;
                if (arrayList.size() > 0) {
                    lv.setAdapter(arrayAdapter);
                } else {
                    arrayList.add("No data!");
                    lv.setAdapter(arrayAdapter);
                }
                if (ukoncenie) {
                    progressDialog.show();
                    progressDialog.setCancelable(true);
                    progressDialog.dismiss();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        // System.out.println(arrayList.size() + "dlzka pola este raz");
    }



    public void sortCo2(View view) {
        final String[] fonts = {
                "None", "Lowest Value", "Highest Value"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(OxidCo2Details.this);
        builder.setTitle("Select a sort type");
        builder.setItems(fonts, new DialogInterface.OnClickListener() {@
                Override
        public void onClick(DialogInterface dialog, int which) {
            if ("None".equals(fonts[which])) {
                //recreate();
                akcia = 0;
                getActuallData();
            } else if ("Lowest Value".equals(fonts[which])) {
                akcia = 1;
                getActuallData();
            } else if ("Highest Value".equals(fonts[which])) {
                akcia = 2;
                getActuallData();
            }
        }
        });
        builder.show();
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