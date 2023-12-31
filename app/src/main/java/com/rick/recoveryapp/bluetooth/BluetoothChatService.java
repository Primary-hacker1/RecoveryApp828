package com.rick.recoveryapp.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.rick.recoveryapp.activity.BluetoothChat;
import com.xuexiang.xutil.tip.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 这个类做了所有设置和管理与其它蓝牙设备连接的工作。
 * 它有一个线程监听传入连接，一个线程与设备进行连接，还有一个线程负责连接后的数据传输。
 */
public class BluetoothChatService {

    public final int MESSAGE_STATE_CHANGE = 1;
    public final int MESSAGE_READ = 2;
    public final int MESSAGE_WRITE = 3;
    public final int MESSAGE_DEVICE_NAME = 4;
    public final int MESSAGE_TOAST = 5;
    // Debugging
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;
    // 创建服务器套接字时SDP记录的名称
    private static final String NAME = "BluetoothChat";
    // 该应用的唯一UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 成员变量
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;//一个线程监听传入连接
    private ConnectThread mConnectThread;//一个线程与设备进行连接
    private ConnectedThread mConnectedThread;//一个线程负责连接后的数据传输
    private int mState;
    // 指示当前连接状态的常量
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // 现在正在侦听传入连接
    public static final int STATE_CONNECTING = 2; // 现在启动传出连接
    public static final int STATE_CONNECTED = 3;  // 现在连接到远程设备

    // 来自BluetoothChatService Handler的关键名
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent请求代码
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //蓝牙接收延迟时间
    private int delay_time;

    public void setDelay_time(int delay_time) {
        this.delay_time = delay_time;
    }

    /**
     * 构造函数。 准备新的BluetoothChat会话。
     *
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothChatService(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        delay_time = 100;
    }

    /**
     * 设置聊天连接的当前状态
     *
     * @param state 定义当前连接状态的整数
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * 返回当前连接状态。
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * 启动聊天服务。 特别地启动AcceptThread以在侦听（服务器）模式下开始会话。
     * 由Activity onResume（）调用
     */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // 取消尝试建立连接的任何线程
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // 取消当前运行连接的任何线程
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 启动线程以监听BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * 启动ConnectThread以启动与远程设备的连接。
     *
     * @param device 要连接的BluetoothDevice
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // 取消尝试建立连接的任何线程
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // 取消当前运行连接的任何线程
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 启动线程以与给定设备连接
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * 启动ConnectedThread以开始管理蓝牙连接
     *
     * @param socket 在其上进行连接的BluetoothSocket
     * @param device 已连接的BluetoothDevice
     */
    @SuppressLint("MissingPermission")
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // 取消完成连接的线程
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // 取消当前运行连接的任何线程
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 取消接受线程，因为我们只想连接到一个设备
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // 启动线程以管理连接并执行传输
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        //将连接的设备的名称发送回UI活动
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
//        ToastUtils.toast("设备 ----  "+  device.getName());
//        bundle.putString(DEVICE_NAME, "matan");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * 以线程锁（不同步）方式写入ConnectedThread
     *
     * @param out 要写入的字节
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        //创建临时对象
        ConnectedThread r;
        // 同步ConnectedThread的副本
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
            Log.d("hexStr2Bytes", "---     连接成功 ---" );
        }
        //执行写入不同步
        r.write(out);
    }

    /**
     * 指示连接尝试失败并通知UI活动.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);

        // 将失败消息发送回活动
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "无法自动连接设备，请手动连接");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 指示连接已丢失并通知UI活动
     */
    private void connectionLost() {
        setState(STATE_LISTEN);

        // 将失败消息发送回活动
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "丢失设备连接");
        Log.d("TOAST", "丢失设备连接");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 此线程在侦听传入连接时运行。 它的行为像一个服务器端。
     * 它运行直到接受连接
     * (或直到取消).
     */
    private class AcceptThread extends Thread {
        // 本地服务器套接字
        private final BluetoothServerSocket mmServerSocket;

        @SuppressLint("MissingPermission")
        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // 创建一个新的侦听服务器套接字
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        /**
         * 2023-8-22 15:32:36
         * 在这里socket.getRemoteDevice()，获得了设备名称。
         * */
        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // 如果我们没有连接，请监听服务器插座
            while (mState != STATE_CONNECTED) {
                try {
                    // 这是一个阻塞调用，只会在成功的连接或异常返回
                    if (mmServerSocket != null) {
                        socket = mmServerSocket.accept();
                    }

                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // 如果连接被接受
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // 状况正常。 启动连接的线程。
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                //未准备就绪或已连接。 终止新套接字。
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                if (mmServerSocket != null) {
                    mmServerSocket.close();
                }

            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * 尝试与设备建立传出连接时，此线程运行。类似于一个客户端
     * 它直通; 连接
     * 成功或失败。
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // 获取与给定的蓝牙设备的连接的BluetoothSocket
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            // 始终取消发现，因为它会减慢连接速度
            mAdapter.cancelDiscovery();
            //连接到BluetoothSocket
//            new Thread(){
//                @Override
//                public void run() {
                    try {
                        // 这是一个阻塞调用，只会在成功的连接或异常返回
                        mmSocket.connect();
                    } catch (IOException e) {
//                        ToastUtils.toast(e.getMessage());
                        connectionFailed();
                        // Close the socket
                        try {
                            mmSocket.close();
                        } catch (IOException e2) {
                            Log.e(TAG, "unable to close() socket during connection failure", e2);
                        }
                        // 启动服务以重新启动侦听模式
                        BluetoothChatService.this.start();
                        return;
                    }
//                }
//            }.start();

            // 重置ConnectThread，因为我们完成了
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // 启动连接的线程
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * 此线程在与远程设备的连接期间运行。
     * 它处理所有传入和传出传输。
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // 获取BluetoothSocket输入和输出流
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            String readMessage;

            while (true) {
                try {
                    int availableBytes = mmInStream.available();

                    if (availableBytes > 0) {
//                        Log.d("setBlood", "------tmpIn         1    ----- " + mmInStream.toString());
                        bytes = mmInStream.read(buffer);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        // readMessage = new String(buffer,0,bytes);
                        readMessage = bytesToHexString(buffer, bytes);
                        data.putString("BTdata", readMessage);
                        msg.what = BluetoothChat.MESSAGE_READ;
//                        Log.d("setBlood", "------handler   readMessage  1----- " + readMessage.toString());
                        msg.setData(data);
                        mHandler.sendMessage(msg);
                    }
//                        bytes = mmInStream.read(buffer);
//
//                    }
//                    while (mmInStream.available() == 0) {
//                    }
//                    }else{
//                        bytes = mmInStream.read(buffer);
//
//                    }
//                    while(mmInStream.available()==0){
//                    }
                    try {
                        Thread.sleep(delay_time);  //当有数据流入时，线程休眠一段时间，默认100ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    bytes = mmInStream.read(buffer);  //从字节流中读取数据填充到字节数组，返回读取数据的长度
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();  //将消息传回主界面
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                    //     java.io.IOException: socket closed
                }
            }
        }

        // 将字节数组转化为16进制字符串，确定长度
        public String bytesToHexString(byte[] bytes, int a) {
            String result = "";
            for (int i = 0; i < a; i++) {
                String hexString = Integer.toHexString(bytes[i] & 0xFF);// 将高24位置0
                if (hexString.length() == 1) {
                    hexString = '0' + hexString;
                }
                result += hexString.toUpperCase() + " ";

            }
            return result;
        }

        /**
         * 写入连接的OutStream。
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                //将发送的消息共享回UI活动
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}