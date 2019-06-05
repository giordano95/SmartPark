package com.smartpark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Locale;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;
import static java.lang.StrictMath.multiplyExact;

public class ReservedHome extends AppCompatActivity {
    private boolean reservation;
    private String PREF = "loc";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.parking:
                    return true;
                case R.id.home:
                    intent = new Intent(getApplicationContext(), ReservedHome.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.money:
                    intent = new Intent(getApplicationContext(), bonus.class);
                    startActivity(intent);
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.reserved_home);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().findItem(R.id.home).setChecked(true);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        TextView title = findViewById(R.id.title);
        TextView floor_field = findViewById(R.id.floor);
        final TextView slot_field = findViewById(R.id.slot);
        TextView floor_tit = findViewById(R.id.floor_tit);
        final TextView slot_tit = findViewById(R.id.slot_tit);
        final TextView pay_home = findViewById(R.id.pay_home);
        TextView pay_up = findViewById(R.id.pay_update);
        ImageView screen = findViewById(R.id.ic_loc);
        TextView pay_hometitle = findViewById(R.id.pay_hometitle);
        TextView pay_cash = findViewById(R.id.pay_cash);
        final TextView pay_start = findViewById(R.id.pay_time);

        SharedPreferences sp = getSharedPreferences(PREF,MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALIAN);
        Date startTime = Calendar.getInstance().getTime();
        int startHr = 0;
        int startMin = 0;
        try{
            startTime = sdf.parse(sp.getString("startTime", ""));
            cal.setTime(startTime);
            startHr = cal.get(Calendar.HOUR_OF_DAY);
            startMin = cal.get(Calendar.MINUTE);
            String shr = Integer.toString(startHr);
            String sm = Integer.toString(startMin);
            if(startHr<10){ shr = "0"+startHr;}
            if(startMin<10){ sm = "0"+startMin;}
            pay_home.setText(shr+":"+sm);
        }catch (Exception e){
            Log.i("TIME", "Unable to retrieve starting time");
        };
        Date now = Calendar.getInstance().getTime();
        int[] payTime = datesDiffer(startTime, now);

        String hours = Integer.toString(payTime[0]);
        String minutes = Integer.toString(payTime[1]);

        if(payTime[0] < 10 && payTime[0] >= 0){
            hours = "0"+payTime[0];
        }

        if(payTime[1] < 10 && payTime[1] >= 0){
            minutes = "0"+payTime[1];
        }

        title.setText(sp.getString("park", ""));
        pay_hometitle.setText("Payment details");
        floor_tit.setText("Slot");
        slot_tit.setText("Plate");
        floor_field.setText(sp.getString("slt", "").toUpperCase());
        slot_field.setText(sp.getString("plateNo", "").toUpperCase());
        pay_start.setText(hours + ":" + minutes +" left");
        double sum = howMuch(payTime);
        pay_cash.setText("€ " + sum);
        pay_up.setText("+€0.50/hr");

        title.setTextColor(getResources().getColor(R.color.font));
        floor_tit.setTextColor(getResources().getColor(R.color.font));
        slot_tit.setTextColor(getResources().getColor(R.color.font));
        floor_field.setTextColor(getResources().getColor(R.color.font));
        slot_field.setTextColor(getResources().getColor(R.color.font));
        pay_home.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        pay_cash.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        pay_hometitle.setTextColor(getResources().getColor(R.color.fontDark));
        pay_up.setTextColor(getResources().getColor(R.color.fontDark));
        pay_start.setTextColor(getResources().getColor(R.color.fontDark));

        final String parkingName = sp.getString("park", "");
        final String slot = sp.getString("slt", "");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("reservations");
        final DatabaseReference ref2 = database.getReference(parkingName);

        ref.child(parkingName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot park: dataSnapshot.getChildren()) {
                    if(park.getKey().equals(slot)){
                        String dateString = "";
                        dateString = park.child("start").getValue().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date convertedDate = new Date();
                        Date now = Calendar.getInstance().getTime();
                        try {
                            convertedDate = dateFormat.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(now.after(convertedDate)){
                            //reservation = false;
                            //SharedPreferences sp = getSharedPreferences("loc",MODE_PRIVATE);
                            //SharedPreferences.Editor ed = sp.edit();
                            //ed.putBoolean("reserv", reservation);
                            //ed.commit();
                            //ref.child(parkingName).child(slot).removeValue();
                            //ref2.child(slot).setValue(true);

                            int[] elaps = datesDiffer(now, convertedDate);

                            String hrs = Integer.toString(elaps[0]);
                            String minute = Integer.toString(elaps[1]);

                            if(elaps[0] < 10){
                                hrs = "0"+hrs;
                            }
                            if(elaps[1] < 10){
                                minute = "0"+minute;
                            }
                            pay_home.setText(hrs+":"+minute);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(convertedDate);
                            String start_hours = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
                            String start_min = Integer.toString(cal.get(Calendar.MINUTE));
                            if(cal.get(Calendar.HOUR_OF_DAY) < 10){
                                start_hours = "0"+start_hours;
                            }
                            if(cal.get(Calendar.MINUTE)<10){
                                start_min = "0"+start_min;
                            }
                            pay_start.setText("Started at: " + start_hours + ":" + start_min);

                            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        final Button leave = findViewById(R.id.leave_home);
        final Button pay = findViewById(R.id.pay_button);

        if(sp.getBoolean("leave", true)){
            leave.setEnabled(false);
            leave.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
        }

        leave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reservation = true;
                SharedPreferences sp = getSharedPreferences(PREF,MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("leave", true);
                ed.commit();

                String park = sp.getString("park", "");
                String slot = sp.getString("slt", "");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference(park);
                ref.child(slot).setValue(true);
                ref = database.getReference("reservations");
                ref.child(park).child(slot).removeValue();
                ref = database.getReference("away");
                ref.child(park).child(slot).child("plate").setValue(slot_field.getText());
                ref.child(park).child(slot).child("going").setValue(true);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date now = Calendar.getInstance().getTime();
                String converted = "";
                try {
                    converted = dateFormat.format(now);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ref.child(park).child(slot).child("start").setValue(converted);
                leave.setVisibility(View.GONE);
                pay.setVisibility(View.VISIBLE);
            }
        });
        leave.setText("Are  you leaving?");

        pay.setText("Pay");
        pay.setTextColor(getResources().getColor(R.color.font));
        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reservation = false;
                SharedPreferences sp = getSharedPreferences(PREF,MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("reserv", reservation);
                ed.putBoolean("leave", false);
                ed.commit();

                final String park = sp.getString("park", "");
                final String slot = sp.getString("slt", "");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference away = database.getReference("away");

                away.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(park).child(slot).exists()){
                            String s = dataSnapshot.child(park).child(slot).child("start").getValue().toString();
                            Date now = Calendar.getInstance().getTime();
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm:ss");
                            Date convertedDate = new Date();
                            try {
                                convertedDate = dateFormat.parse(s);
                            } catch (ParseException e) { e.printStackTrace(); }

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(convertedDate);
                            cal.add(Calendar.MINUTE, 1);

                            if(now.before(cal.getTime())){
                                SharedPreferences sp = getSharedPreferences(PREF,MODE_PRIVATE);
                                SharedPreferences.Editor ed = sp.edit();
                                int bon = sp.getInt("bonus", 0);
                                ed.putInt("bonus", bon + 100);
                                ed.commit();
                                Toast.makeText(getApplicationContext(), "Added 100 bonus points!", Toast.LENGTH_LONG).show();
                            }

                            away.child(park).child(slot).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public int[] datesDiffer(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        int secondsInMilli = 1000;
        int minutesInMilli = secondsInMilli * 60;
        int hoursInMilli = minutesInMilli * 60;
        int daysInMilli = hoursInMilli * 24;

        int elapsedDays = (int) different / daysInMilli;
        different = different % daysInMilli;

        int elapsedHours = (int) different / hoursInMilli;
        different = different % hoursInMilli;

        int elapsedMinutes = (int) different / minutesInMilli;

        int[] timeElaps = new int[2];

        timeElaps[0] = elapsedHours;
        timeElaps[1] = elapsedMinutes;

        if(timeElaps[0] < 0 || timeElaps[1] < 0){
            timeElaps[0] = abs(timeElaps[0]);
            timeElaps[1] = abs(timeElaps[1]);
        }

        return timeElaps;
    }

    private double howMuch(int[] payTime){
        int hours = payTime[0];
        int minutes = payTime[1];

        double sum = hours * 0.50;
        if(minutes != 0){
            sum += 0.50;
        }

        return sum;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }
}
