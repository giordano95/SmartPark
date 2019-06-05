package com.smartpark;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Reservation extends AppCompatActivity {
    private boolean reservation;
    private String PREF = "loc";
    private int hour_sel;
    private int min_sel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.parking:
                    intent = new Intent(Reservation.this, Parkings.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.home:
                    intent = new Intent(Reservation.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.money:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));;


        final Context contextForDialog = this;

        Intent intent = getIntent();
        int parking = intent.getIntExtra("parking", 0); //from 0 to N
        String id = intent.getStringExtra("id"); //from 0 to N
        TextView title = findViewById(R.id.title);
        final EditText park = findViewById(R.id.park);
        final EditText slot = findViewById(R.id.slot);
        final EditText plate = findViewById(R.id.plate);
        final Button reserve = findViewById(R.id.reserve);
        reserve.setText("Reserve!");
        reserve.setTextColor(getResources().getColor(R.color.font));
        title.setText("Reserve your slot!");
        title.setTextColor(getResources().getColor(R.color.font));
        park.setText("parking" + (parking+1));
        slot.setText("slot"+id);
        plate.setHintTextColor(getResources().getColor(R.color.fontUltraDark));
        plate.setTextColor(getResources().getColor(R.color.font));
        plate.setHint("Insert your plate number");
        park.setTextColor(getResources().getColor(R.color.font));
        slot.setTextColor(getResources().getColor(R.color.font));
        park.setEnabled(false);
        slot.setEnabled(false);

        final EditText picker = findViewById(R.id.picker);
        picker.setTextColor(getResources().getColor(R.color.font));
        picker.setHint("Insert your desired arrival time");
        picker.setHintTextColor(getResources().getColor(R.color.fontUltraDark));
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timepick = new TimePickerDialog(contextForDialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hour;
                        String min;
                        hour_sel = hourOfDay;
                        min_sel = minute;
                        if(hourOfDay<10){
                            hour = "0"+hourOfDay;
                        }else{ hour = ""+hourOfDay; }
                        if(minute<10){
                            min = "0"+minute;
                        }else{ min = ""+minute; }
                        picker.setText(hour + ":" + min);
                    }
                }, 0, 0, true);
                timepick.show();
            }
        });

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidPlate(plate.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Not valid plate", Toast.LENGTH_LONG).show();
                }else if(!isValidTime(picker.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Reservation is limited to 1 hour before", Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences sp = getSharedPreferences(PREF,MODE_PRIVATE);
                    reservation = true;
                    SharedPreferences.Editor ed = sp.edit();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALIAN);
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, hour_sel);
                    calendar.set(Calendar.MINUTE, min_sel);
                    calendar.set(Calendar.SECOND, 0);

                    ed.putString("startTime", sdf.format(calendar.getTime()));
                    ed.putString("park", park.getText().toString());
                    ed.putString("slt", slot.getText().toString());
                    ed.putString("plateNo", plate.getText().toString());
                    ed.putBoolean("reserv", reservation);
                    ed.commit();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("reservations");
                    ref.child(park.getText().toString()).child(slot.getText().toString())
                            .child("plate").setValue(plate.getText().toString());
                    ref.child(park.getText().toString()).child(slot.getText().toString())
                            .child("start").setValue(sdf.format(calendar.getTime()));
                    //ref = database.getReference(park.getText().toString());
                    //ref.child(slot.getText().toString()).setValue(false);

                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);
                }
            }
        });
    }

    public Boolean isValidTime(String time){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALIAN);
        Date startTime = Calendar.getInstance().getTime();
        int startHr = 0;
        int startMin = 0;
        try {
            cal.setTime(startTime);
            startHr = cal.get(Calendar.HOUR_OF_DAY);
            startMin = cal.get(Calendar.MINUTE);
        }catch (Exception e){}

        if(hour_sel > startHr + 1 || startHr > hour_sel){
            return false;
        }
        if(startHr == hour_sel && startMin >= min_sel){
            return false;
        }
        if(hour_sel == startHr + 1){
            if(startMin <= min_sel){
                return false;
            }
        }

        return true;
    }

    private boolean isValidPlate(String target) {
        return Pattern.compile("[A-Za-z][A-Za-z][0-9][0-9][0-9][A-Za-z][A-Za-z]").matcher(target).matches();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }
}
