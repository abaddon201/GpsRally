package com.glebov.gpsrally;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText planedDistance;
    private EditText planedAvgSpeed;
    private EditText planedTime;

    private ImageView gpsFixedIco;
    private ImageView gpsAvailableIco;
    private TextView gpsSatelites;
    private TextView gpsAccuracity;

    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter dataAdapter;

    private GPSReceiver gpsReceiver;
    private GPSReceiver.GPSUI gpsCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planedDistance = (EditText) findViewById(R.id.planedDistance);
        planedAvgSpeed = (EditText) findViewById(R.id.planedAvgSpeed);
        planedDistance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        planedAvgSpeed.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        gpsFixedIco = (ImageView) findViewById(R.id.gpsFix);
        gpsAvailableIco = (ImageView) findViewById(R.id.gpsAvailable);
        gpsSatelites = (TextView) findViewById(R.id.gpsSatelites);
        gpsAccuracity = (TextView) findViewById(R.id.gpsAccuracity);

        dbHelper = new DatabaseHelper(this);
        dbHelper.open();

        dbHelper.deleteAllRoutes();
        dbHelper.insertSome();

        Cursor cursor = dbHelper.fetchRoute();

        // The desired columns to be bound
        String[] columns = new String[]{
                DatabaseHelper.KEY_NUM,
                DatabaseHelper.KEY_FULL_DISTANCE,
                DatabaseHelper.KEY_DISTANCE,
                DatabaseHelper.KEY_AVGSPEED,
                DatabaseHelper.KEY_TIME
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.row_num,
                R.id.row_full_distance,
                R.id.row_distance,
                R.id.row_speed,
                R.id.row_time
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.route_row,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listView);
        View v = getLayoutInflater().inflate(R.layout.header_row, null);
        //listView.addHeaderView(v);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                Toast.makeText(MainActivity.this, "long tap", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EditRouteActivity.class);
                startActivity(intent);
                return true;
            }
        });
        gpsCallback = new GPSReceiver.GPSUI() {
            @Override
            public void onFixChanged(boolean fixed) {
                if (fixed)
                    gpsFixedIco.setBackgroundColor(Color.GREEN);
                else
                    gpsFixedIco.setBackgroundColor(Color.RED);
            }

            @Override
            public void onDeviceStatusChanged(int status) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        gpsAvailableIco.setBackgroundColor(Color.GREEN);
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        gpsAvailableIco.setBackgroundColor(Color.RED);
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        gpsAvailableIco.setBackgroundColor(Color.YELLOW);
                        break;
                }

            }

            @Override
            public void onSatelitesChanged(int all, int good) {
                String t = String.format("%2d", good) + "/" + String.format("%2d", all);
                gpsSatelites.setText(t);
            }

            @Override
            public void onLocation(Location location) {
                String t = String.format("%.2f", location.getAccuracy());
                gpsAccuracity.setText(t);
            }
        };
        gpsReceiver = new GPSReceiver(this);
        gpsReceiver.start(gpsCallback);
    }
}
