package com.example.bluetoothlibrary.interfacer;

import com.example.bluetoothlibrary.enumer.RxEnum;

public interface DataHandler {
    void getData(String data);
    void isConnected(boolean b);
    void sendComm(RxEnum rxEnum);
}
