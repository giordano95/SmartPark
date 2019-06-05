package com.smartpark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class bonus extends AppCompatActivity {
    private String PREF = "loc";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.parking:
                    Intent intent = new Intent(getApplicationContext(), Parkings.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.home:
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);
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
        setContentView(R.layout.activity_bonus);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().findItem(R.id.home).setChecked(true);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SharedPreferences sp = getSharedPreferences(PREF,MODE_PRIVATE);
        int bon = sp.getInt("bonus", 0);

        TextView bonus = findViewById(R.id.bonus);
        TextView det = findViewById(R.id.bonus_det);
        bonus.setText("Bonus points\n\n" + bon);
        det.setText("Each 1000 points you can ask for 10% discount in your favourite shop within the mall");
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }
}
