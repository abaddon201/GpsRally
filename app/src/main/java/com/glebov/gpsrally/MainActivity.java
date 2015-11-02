package com.glebov.gpsrally;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText planedDistance;
    EditText planedAvgSpeed;
    EditText planedTime;

    DatabaseHelper dbHelper;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planedDistance = (EditText) findViewById(R.id.planedDistance);
        planedAvgSpeed = (EditText) findViewById(R.id.planedAvgSpeed);
        planedDistance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        planedAvgSpeed.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

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
        listView.addHeaderView(v);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "long tap",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
