package com.example.euv;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class Dashboard extends AppCompatActivity {
    Toolbar toolbar;
    UUID uuid;
    FirebaseAuth auth;
    FloatingActionButton fab;
    BluetoothAdapter bluetoothAdapter;
    int requestcode = 1;
    BluetoothSocket bluetoothSocket;
    Intent enablebluetoothintent;
    BluetoothDevice device;
    DatabaseReference databaseReference;
    BroadcastReceiver broadcastReceiver;
    TextView Voltage, Current, SOC, power,clock;
    Button starttest, stoptest, linkdevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        auth = FirebaseAuth.getInstance();
        fab = findViewById(R.id.logout);
        starttest = findViewById(R.id.starttest);
        stoptest = findViewById(R.id.stoptest);
        clock=findViewById(R.id.clock);
        linkdevice = findViewById(R.id.lidevice);
         Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                clock.setText("Time: " + currentTime);
                handler.postDelayed(this, 1000);
            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enablebluetoothintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        TextView v = findViewById(R.id.volt);
        TextView a = findViewById(R.id.amp);
        TextView ah = findViewById(R.id.pow);
        TextView soc = findViewById(R.id.soc);
        Voltage = findViewById(R.id.voltage);
        Current = findViewById(R.id.current);
        power = findViewById(R.id.capacity);
        SOC = findViewById(R.id.staofchrg);
        databaseReference = FirebaseDatabase.getInstance().getReference("Battery_id");
        setSupportActionBar(toolbar);
        toolbar.setTitle("EV_U");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Dashboard.this, LoginSignup.class));
                finish();
            }
        });
        Animation alpha = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        v.startAnimation(alpha);
        a.startAnimation(alpha);
        ah.startAnimation(alpha);
        soc.startAnimation(alpha);
        databaseReference.child("response").push().setValue("Action");
        bluetoothOnMethod();
        linkdevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, LinkedDevice.class));
            }
        });




        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();

        String[] strings = new String[bt.size()];
        int index = 0;
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                starttest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            SOC.setText("SOC(%):error");
                            //fetchData(device,bluetoothAdapter,SOC);
                        }
                        catch (Exception e){
                            Toast.makeText(Dashboard.this, "Device Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                strings[index] = device.getName() + device.getUuids().toString() + device.getAddress();

                databaseReference.child("DeviceInfo").push().setValue(strings[index]);
                index++;
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestcode) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth enabled failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(broadcastReceiver);


    }

    public void bluetoothOnMethod() {

        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(enablebluetoothintent, requestcode);
        }
    }

    public void fetchData(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, TextView textView) {


        try {

             device = bluetoothAdapter.getRemoteDevice(device.getAddress());

            if (ActivityCompat.checkSelfPermission(Dashboard.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            ParcelUuid parcelUuid = device.getUuids()[0];
            UUID uuid = parcelUuid.getUuid();
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();


            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("GET BATTERY LEVEL".getBytes());


            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int numBytes = inputStream.read(buffer);


            String response = new String(buffer, 0, numBytes);
            int batteryLevel = Integer.parseInt(response);

            textView.setText(batteryLevel + "%");


            socket.close();


        }
        catch(Exception e){
          e.printStackTrace();
        }
    }

}
