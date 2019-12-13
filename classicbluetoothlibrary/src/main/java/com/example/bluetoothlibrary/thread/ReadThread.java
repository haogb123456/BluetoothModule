package com.example.bluetoothlibrary.thread;

import android.bluetooth.BluetoothSocket;

import com.example.bluetoothlibrary.interfacer.DataHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * create by : hgb
 * time: 2019/12/13 11:40
 * describe
 */
class ReadThread extends Thread {
    private BluetoothSocket socket;
    private DataHandler dataHandler;
    public ReadThread(BluetoothSocket socket, DataHandler dataHandler) {
        this.socket = socket;
        this.dataHandler = dataHandler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        InputStream mmInStream = null;   //建立输入流读取数据
        try {
            mmInStream = socket.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (!interrupted()) {  //无限循环读取数据
            try {
                if ((bytes = mmInStream.read(buffer)) > 0) {
                    byte[] buf_data = new byte[bytes];
                    for (int i = 0; i < bytes; i++) {
                        buf_data[i] = buffer[i];
                    }
                    dataHandler.getData(new String(buf_data));
                }
            } catch (Exception e) {
                e.printStackTrace();
                dataHandler.isConnected(false);
                closeStream(mmInStream);
                break;
            }
        }
        closeStream(mmInStream);
    }

    private void closeStream(InputStream mmInStream) {
        try {
            mmInStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}