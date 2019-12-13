package com.example.bluetoothlibrary.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.bluetoothlibrary.interfacer.DataHandler;
import com.example.bluetoothlibrary.enumer.RxEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothThread extends Thread {
    private final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //蓝牙通信的UUID，必须为这个，如果换成其他的UUID会无法通信
    private BluetoothSocket socket = null;
    private BluetoothDevice device;
    private DataHandler dataHandler;
    ReadThread readThread;
    OutputStream writeStream = null;
    public BluetoothThread(BluetoothDevice device, DataHandler dataHandler) {
        this.device = device;
        this.dataHandler = dataHandler;
    }
    @Override
    public void run() {
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));
            socket.connect();
            dataHandler.isConnected(true);
            readThread = new ReadThread(socket, dataHandler);
            readThread.start();
            dataHandler.sendComm(RxEnum.Searched);
        } catch (Exception e) {
            dataHandler.isConnected(false);
        }
    }
    public boolean send(String command){
        if(socket == null) return false;
        if(!socket.isConnected()){
            try{
                socket.connect();
            }catch(Exception ex){
                dataHandler.isConnected(false);
                return false;
            }
        }
        try {
            writeStream = socket.getOutputStream();
            writeStream.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            dataHandler.isConnected(false);
        }
        return true;
    }
    public void onDestroy(){
        if(readThread!=null)readThread.interrupt();
        if (socket!=null) {
            try {
                socket.close();
                writeStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

