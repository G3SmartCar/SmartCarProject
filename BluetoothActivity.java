package com.example.thomasemilsson.smartcarapplication;

/**
 *
 * @author Thomas Emilsson
 * 13/04/2016
 *
 * Version 0.0.0.1
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.UserHandle;
import android.view.SearchEvent;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends Activity {


    ListView deviceList;
    ListView wifiList; // nothing for now

    boolean display = true;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        // initialize variable views
        deviceList = (ListView)findViewById(R.id.deviceList);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        // Check for existing bluetooth connection
        if (myBluetooth == null){
            // Display error message
            message("Bluetooth Device is not Available");

            finish();
        } else if (!myBluetooth.isEnabled()){
            Intent turnBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(turnBluetoothOn, 1);
        }

        if (display) {
            showDeviceList();
        }
    }

    private void showDeviceList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0){

            // Search through paired device and get name/address & Display Them
            for (BluetoothDevice bluetooth : pairedDevices){
                list.add(bluetooth.getName() + "\n" + bluetooth.getAddress());
            }
        } else {
            message("No Paired Bluetooth Devices Found");
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);

        display = false;
    }

    // On Click listener after selecting paired device
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            // Get Device Information
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an Intent to change activities
            Intent intent = new Intent(BluetoothActivity.this, Joystick.class);

            // Change Activity
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void message(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
