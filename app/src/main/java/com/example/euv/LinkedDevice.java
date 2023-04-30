package com.example.euv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.Set;

public class LinkedDevice extends AppCompatActivity {
    BluetoothAdapter bladapter;

    ListView list;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_device);
        bladapter = BluetoothAdapter.getDefaultAdapter();
        list = findViewById(R.id.list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("EVU");

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
        Set<BluetoothDevice> bt = bladapter.getBondedDevices();
        String[] strings = new String[bt.size()];
        int index = 0;
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                strings[index] = device.getName();
                index++;
            }
    }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,strings);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(LinkedDevice.this, "Linked successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LinkedDevice.this,Dashboard.class));
                Toast.makeText(LinkedDevice.this, "Click on Start Test", Toast.LENGTH_SHORT).show();

            }
        });
}


}