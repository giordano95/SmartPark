package com.smartpark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ParkSelected extends AppCompatActivity implements View.OnClickListener{
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private Map<String, Boolean> parking = new TreeMap<>();
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitem);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        ref = database.getReference("parking"+(position+1));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    String key = s.getKey();
                    Boolean value = s.getValue(Boolean.class);
                    parking.put(key, value);
                }
                drawParking(parking);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERR", "Failed to read value.", error.toException());
            }
        });
    }

    private void drawParking(Map<String, Boolean> parking){
        int numSlots = parking.keySet().size();
        int div = numSlots/2;
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        int i = 0;
        int j = 0;

        for (Map.Entry<String,Boolean> k : parking.entrySet()) {
            if (i >= numSlots){
                i = 0;
            }
            if(j >= numSlots){
                j = 0;
            }

            final Button tv = new Button(this);
            tv.setOnClickListener(this);

            final String slot = k.getKey();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference("reservations");
            //final DatabaseReference ref2 = database.getReference("parking"+(position+1));
            ref.child("parking"+(position+1)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot park: dataSnapshot.getChildren()) {
                        if(park.getKey().equals(slot)){
                            String dateString = "";
                            dateString = park.child("start").getValue().toString();
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm:ss");
                            Date convertedDate = new Date();
                            Date now = Calendar.getInstance().getTime();
                            try {
                                convertedDate = dateFormat.parse(dateString);
                            } catch (ParseException e) { e.printStackTrace(); }

                            if(now.after(convertedDate)){
                                Boolean reservation = false;
                                SharedPreferences sp = getSharedPreferences("loc",MODE_PRIVATE);
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putBoolean("reserv", reservation);
                                ed.commit();
                                ref.child("parking"+(position+1)).child(slot).removeValue();
                                //ref2.child(slot).setValue(true);
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.wait);
            params.bottomMargin = 10;

            if(i<=div) {
                params.leftMargin = 200;
                params.topMargin = i*150 + 30;
            }else{
                params.leftMargin = 600;
                params.topMargin = j*150 + 30;
                j++;
            }
            tv.setId(i);
            tv.setText(k.getKey());
            tv.setWidth(180);
            tv.setHeight(50);

            if(k.getValue()) {
                tv.setBackgroundColor(getResources().getColor(R.color.free));
                tv.setTextColor(getResources().getColor(R.color.font));
            }else{
                tv.setEnabled(false);
                tv.setTextColor(getResources().getColor(R.color.fontUltraDark));;
                tv.setBackgroundColor(getResources().getColor(R.color.occupied));
            }

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("parking"+(position+1)).hasChild(slot)){
                        tv.setBackgroundColor(getResources().getColor(R.color.reserved));
                        tv.setEnabled(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

            final DatabaseReference ref2 = database.getReference("away");
            ref2.child("parking"+(position+1)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot park: dataSnapshot.getChildren()) {
                        if(park.getKey().equals(slot)){
                            Date now = Calendar.getInstance().getTime();
                            String dateString = park.child("start").getValue().toString();
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm:ss");
                            Date convertedDate = new Date();
                            try {
                                convertedDate = dateFormat.parse(dateString);
                            } catch (ParseException e) { e.printStackTrace(); }

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(convertedDate);
                            cal.add(Calendar.MINUTE, 1);

                            if(now.before(cal.getTime())){
                                tv.setBackgroundColor(getResources().getColor(R.color.going));
                                tv.setEnabled(false);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

            tv.setPadding(16, 8, 8, 8);
            tv.setLayoutParams(params);
            rl.addView(tv);
            i++;
        }
        TextView wait = findViewById(R.id.wait);
        wait.setText("Total slots: "+numSlots);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        id++;
        String str = Integer.toString(id);
        if(id < 10){
            str = "0" + str;
        }
        Intent intent = new Intent(getApplicationContext(), Reservation.class);
        intent.putExtra("id", str);
        intent.putExtra("parking", position);
        startActivity(intent);
    }
}
