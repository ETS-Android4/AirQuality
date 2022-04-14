package com.example.airquality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NextData extends AppCompatActivity {

    private FirebaseDatabase firebaseData = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private static TextView textViewlpg;
    private static TextView textViewpropan;
    private static TextView textViewdust;
    private static String lpg;
    private static String propan;
    private static String dust;
    private static String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_data);
        databaseReference = firebaseData.getReference();
        textViewlpg = (TextView) findViewById(R.id.textviewlpg);
        textViewpropan = (TextView) findViewById(R.id.textviewpropan);
        textViewdust = (TextView) findViewById(R.id.textviewdust);
        getActuallData();
    }

    private void getActuallData() {
        databaseReference.child("Data").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                System.out.println(value);
                String[] array = null;
                array = value.split(",");

                lpg = array[3];
                propan = array[4];
                dust = array[7];

                time = array[10];
                String cas = "";
                String minuty = "";
                String sekundy = "";
                minuty = time.substring(2, 5);
                sekundy = time.substring(5, 8);
                System.out.println(sekundy + " sekundy ineeeee");

                double pom_lpg = Double.parseDouble(lpg);
                double pom_propan = Double.parseDouble(propan);
                double pom_dust = Double.parseDouble(dust);

                textViewlpg.setText(pom_lpg + " ppm");
              //  textViewcopropan.setText(pom_propan + " ppm");
                textViewdust.setText(pom_dust + " u/m3");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("problem");
            }
        });
    }

    public void nextData2(View view) {
        Intent nextData = new Intent(NextData.this,MainActivity.class);
        startActivity(nextData);

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