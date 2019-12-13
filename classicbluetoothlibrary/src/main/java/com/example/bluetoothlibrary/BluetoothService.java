package com.example.bluetoothlibrary;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.bluetoothlibrary.interfacer.DataHandler;
import com.example.bluetoothlibrary.thread.BluetoothThread;

public class BluetoothService extends Service {
    BluetoothThread bluetoothThread;
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public class LocalBinder extends Binder {
        public BluetoothService getService(){
            return BluetoothService.this;
        }
        public void setDevice(BluetoothDevice device, DataHandler dataHandler){
            bluetoothThread = new BluetoothThread(device,dataHandler);
            bluetoothThread.start();
        }
        public void sendComm(String com){
            bluetoothThread.send(com);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothThread.onDestroy();
    }
}
