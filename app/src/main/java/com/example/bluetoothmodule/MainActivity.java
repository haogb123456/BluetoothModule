package com.example.bluetoothmodule;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.bluetoothlibrary.BluetoothClient;

public class MainActivity extends AppCompatActivity {
    BluetoothClient bluetoothClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothClient = new BluetoothClient(this);
    }

    @Override
    protected void onDestroy() {
        bluetoothClient.onDestroy();
        super.onDestroy();
    }
}
