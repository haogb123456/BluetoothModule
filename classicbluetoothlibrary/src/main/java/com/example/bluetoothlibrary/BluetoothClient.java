package com.example.bluetoothlibrary;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.example.bluetoothlibrary.enumer.RxEnum;
import com.example.bluetoothlibrary.interfacer.DataHandler;

/**
 * create by : hgb
 * time: 2019/12/13 10:56
 * describe:
 */
public class BluetoothClient {
    private BluetoothAdapter mBluetoothAdapter;
    BlueToothBroadCast blueToothBroadCast;
    private Context context;

    public BluetoothClient(Context context) {
        this.context = context;
        initBluetooth();
    }

    public void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            boolean isEnable = mBluetoothAdapter.enable();
            if (!isEnable) {
                //蓝牙权限被禁止，请在权限管理中打开
            }
        }
        if (blueToothBroadCast!=null) {
            context.unregisterReceiver(blueToothBroadCast);
        }
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        blueToothBroadCast = new BlueToothBroadCast();
        context.registerReceiver(blueToothBroadCast, mFilter);
        startSearch();

    }

    private class BlueToothBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //发现蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println("发现设备，名称为： "+device.getName());
                if (!TextUtils.isEmpty(device.getAddress()) && device.getAddress().startsWith(Key.btMacBegin)) {//搜索到以此地址开头的蓝牙，则连接
                    mBluetoothAdapter.cancelDiscovery();
                    bindServicer(device);
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.cancelDiscovery();
            }else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            }
        }
    }

    TimeThread timeThread;
    BluetoothService.LocalBinder bluetoothServiceBinder;
    ServiceConnection serviceConnection;
    private void bindServicer(final BluetoothDevice device) {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                bluetoothServiceBinder = (BluetoothService.LocalBinder) iBinder;
                bluetoothServiceBinder.setDevice(device, new DataHandler() {
                    @Override
                    public void getData(String data) {
                        System.out.println(data);
                        timeThread.interrupt();
                    }
                    @Override
                    public void isConnected(boolean b) {
                        if (!b) {
                            System.out.println("连接不成功");
                        }
                    }

                    @Override
                    public void sendComm(RxEnum rxEnum) {
                        if (rxEnum == RxEnum.Searched) {
                            timeThread = new TimeThread();
                            timeThread.start();
                        }
                    }
                });
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bluetoothServiceBinder = null;
            }
        };
        context.bindService(new Intent(context, BluetoothService.class), serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private void startSearch() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }
    String com = "#SET{F:4,D:1}*";
    public void setCom(String com) {//设置发送指令
        this.com = com;
    }

    class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (!interrupted()){
                try {
                    bluetoothServiceBinder.sendComm(com);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    public void onDestroy(){
        context.unregisterReceiver(blueToothBroadCast);
        timeThread.interrupt();
        if (serviceConnection!=null)  {
            context.unbindService(serviceConnection);
        }
    }
}
