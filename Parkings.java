package com.smartpark;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Parkings extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.parking:
                    return true;
                case R.id.home:
                    intent = new Intent(Parkings.this, MainActivity.class);
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

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        // Then you start a new Activity via Intent

        Intent intent = new Intent(getApplicationContext(), ParkSelected.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkings);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.getMenu().findItem(R.id.parking).setChecked(true);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ListView listview = (ListView) findViewById(R.id.parkings_list);
        listview.setOnItemClickListener(this);
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
