package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/12/16.
 * This class connects to to RaspberryPi, Arduino through bluetooth or wifi. 
 *
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// Nav Drawer imports, might be duplicates. Will tidy up here on Sunday

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.transition.Slide;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ListView;
import android.widget.AdapterView;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.Set;

/**
 * This is the main page of the application. The user is shown two possible
 * types of connections: Bluetooth or Wifi. To app shows a list of bluetooth devices
 * the user is paired to and a box for entering an IP to connect to. After pressing
 * either the connect button or a paired bluetooth device, the phone connects to the
 * appropriately and the next page is opened where the user can move around the car
 * using a joystick or the tilt of his/her phone.
 */
 
public class ConnectionActivity extends Activity
                    // implements NavigationView.OnNavigationIntemSelectedListener
{


    ListView deviceList;
    TextView textEnterIP;
    Button connectButton;
    
    EditText enterIPAddress;

    boolean display = true;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    public static String EXTRA_ADDRESS = "device_address";
    
    private String IPAddress;
    
    // Used to allow only real IPs
    private final Pattern IPRegEx = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    
    /**
     * Navigation Drawer is created in onCreate...
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        
    
       // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       // drawer.setDrawerListener(toggle);
       // toggle.syncState();
        
       // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
       // navigationView.setNavigationItemSelectedListener(this);


        // initialize variable views
        deviceList = (ListView) findViewById(R.id.deviceList);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        textEnterIP = (TextView) findViewById(R.id.textEnterIP);
        connectButton = (Button) findViewById(R.id.buttonConnect);


        // Default IP Hostname
        textEnterIP.setText("172.20.10.6");
        
        // textEnterIP.setText("192.168.43.220");
        // textEnterIP.setText("192.168.43.140");
        
        //enterIPAddress = (EditText) findViewById(R.id.EditText);
        //String IP = enterIPAddress.getText().toString();
        setIPAddress(IP);

        // Check for existing bluetooth connection
        if (myBluetooth == null) {
            // Display error message
            message("Bluetooth Device is not Available");
            finish();
        } else if (!myBluetooth.isEnabled()) {
            Intent turnBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(turnBluetoothOn, 1);
        }

        if (display) {
            showDeviceList();
        }


        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Set Singleton for wifiStatus to true
                Properties.getInstance().wifiStatus = true;

                // Get the text from the textField and set the IP Singleton to that
                IP.getInstance().activeIP = textEnterIP.getText().toString();

             //   Intent intent = new Intent(ConnectionActivity.this, BasicActivity.class);
                Intent intent = new Intent(ConnectionActivity.this, ConnectionActivity.class);

                intent.putExtra(EXTRA_ADDRESS, "==");
                // Change Activity
                //intent.putExtra(EXTRA_ADDRESS, address);

                startActivity(intent);
            }
        });
    }
    
    
    
    /**
     * Method that goes through all paried Devices,
     * and puts them in ArrayAdapter to be displayed
     */
    
    private void showDeviceList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {

            // Search through paired device and get name/address & Display Them
            for (BluetoothDevice bluetooth : pairedDevices) {
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

            Properties.getInstance().wifiStatus = false;
            // Get Device Information
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an Intent to change activities
           // Intent intent = new Intent(ConnectionActivity.this, BasicActivity.class);
            Intent intent = new Intent(ConnectionActivity.this, ControlActivity.class);

            // Change Activity putExtra() is necessary
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection, menu);
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

    // Error Message on Phone Method
    // Simply enter the desired string to show up on the screen
    public void message(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
            }
        }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


    int id = item.getItemId();

    if(id == R.id.nav_first_layout){
        Intent appIntent = new Intent(ConnectionActivity.this, AboutUsActivity.class);
        startActivity(intent);
            }

    else if (id == R.id.nav_second_layout){
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.arduino.cc/");
        startActivity(webIntent);
    }


    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.END);
    return true;
}

 /**
  * A method to set the IPAddress, and to check for the right format with RegExp.
  * If format in not right user is prompted to enter a valid IP.
  *
  * @param IPAddress entered by the user
  */

    public void setIPAddress(String IPAddress){

        if (!IPRegEx.matcher(IPAddress).matches()){

    //put this text in a textbox in the app
    System.out.println("Not a valid IP Address, please try again");
    enterIPAddress.setText("Please enter a valid IP address", TextView.BufferType.EDITABLE);
    enterIPAddress.selectAll();
    }

    else {
        this.IPAddress = IPAddress;

        }
    }

}


