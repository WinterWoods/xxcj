package com.sz.baseuiframe;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheung.android.base.baseuiframe.activity.BaseUIActivity;
import com.printer.sdk.PrinterConstants;
import com.sdsmdg.tastytoast.TastyToast;
import com.sz.baseuiframe.Models.PersonModel;
import com.sz.baseuiframe.Models.PrintModel;
import com.sz.baseuiframe.dialog.BluetoothSelectDialog;
import com.sz.baseuiframe.okhttp.callback.Callback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Request;

import com.printer.sdk.PrinterInstance;

public class TestActivity extends BaseUIActivity implements View.OnClickListener {
    private Button button_connect, button_send, button_clean, bt_minus, bt_add;
    private BluetoothSelectDialog bluetoothselectdialog;
    private TextView textView;
    //private EditText et_W,et_H;
    private final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    // 选中发送数据的蓝牙设备，全局变量，否则连接在方法执行完就结束了
    private BluetoothDevice selectDevice;
    // 获取到选中设备的客户端串口，全局变量，否则连接在方法执行完就结束了
    private BluetoothSocket clientSocket;
    // 获取到向设备写的输出流，全局变量，否则连接在方法执行完就结束了
    private OutputStream os;
    String address = "";
    String name = "";
    AcceptThread thread;
    BluetoothConnectReceiver myreceiver;
    int labelCount = 1;//总数
    int labelIndex = 1;//当前页数
    int W = 47;
    int H = 110;

    private TextView textView_info;
    PrintModel.PrintInfo info;
    private ImageView review;
    private ImageView review1;
    private Handler handler = new Handler();
    private Bitmap bmp;
    //private Bitmap bmp1;
    private Button button_send1;
    private boolean isConnect = false;
    //private PrinterInstance mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = (TextView) findViewById(R.id.textView);
//        textView_info=(TextView)findViewById(R.id.textView_info);
        review = (ImageView) findViewById(R.id.review);
        review1 = (ImageView) findViewById(R.id.review1);
//        tv_number=(TextView)findViewById(R.id.tv_number);
        //tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
//        et_W=(EditText)findViewById(R.id.et_W);
//        et_H=(EditText)findViewById(R.id.et_H);
        button_connect = (Button) findViewById(R.id.button_connect);
        button_send = (Button) findViewById(R.id.button_send);
//        button_send1=(Button)findViewById(R.id.button_send1);
        button_clean = (Button) findViewById(R.id.button_clean);
//        bt_minus=(Button)findViewById(R.id.bt_minus);
//        bt_add=(Button)findViewById(R.id.bt_add);
        button_connect.setOnClickListener(this);
        button_send.setOnClickListener(this);
        button_send.setEnabled(false);
//        button_send1.setOnClickListener(this);
//        button_send1.setEnabled(false);
        button_clean.setOnClickListener(this);
        //bt_minus.setOnClickListener(this);
        //bt_add.setOnClickListener(this);
        bluetoothselectdialog = new BluetoothSelectDialog();
        // 实例接收客户端传过来的数据线程
        thread = new AcceptThread();
        myreceiver = new BluetoothConnectReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(myreceiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Android M Permission check
            //android 6.0 以上 需要授权位置服务 否则搜索不到蓝牙2.0
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }

        initBluetooth();
        initData();
    }

    /**
     * 每次进来进行蓝牙自动连接
     */
    private void initBluetooth() {
        address = MyApp.instance.dataKeeper.get("bluetooth_address", "");
        name = MyApp.instance.dataKeeper.get("bluetooth_name", "");
        myreceiver.setButton(button_connect, address, name);
        button_connect.setText("未连接，请选择打印机");
        if (!address.equals("")) {
            button_connect.setText(name + "（" + address + "）未连接");
            //mPrinter = PrinterInstance.getPrinterInstance(address,0,null);
            connBluetooth(thread);
        }
    }

    private void initData() {
        //    得到跳转到该Activity的Intent对象
        Intent intent = getIntent();
        String bh = intent.getStringExtra("BH");
        CommHttp.post("jchongda.php")
                .addParams("YHID", MyApp.instance.userInfo.getYHID())
                .addParams("BH", bh)
                .build().execute(new Callback<PrintModel>() {
            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void onAfter(int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.show(mActivity, "出错了。", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                button_send.setEnabled(false);
            }

            @Override
            public void onResponse(PrintModel response, int id) {

                //textView_info.setText(response.getData().getBH()+"-"+response.getData().getXM()+"-"+response.getData().getZJ());
                info = response.getData();
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.one);

                int fontSize = 16;
                int fontColor = Color.BLACK;
                Bitmap qrBmp = QRCodeUtil.creatBarcode(mActivity, info.getBH(), 650, 90, false);
                //写入条码
                bmp = ImageUtil.drawBmpToBitmap(mActivity, bmp, qrBmp, 150, 88);

                //写入条码
                bmp = ImageUtil.drawBmpToBitmap(mActivity, bmp, qrBmp, 150, 650);
                //写入编号
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getBH(), fontSize - 4, fontColor, 120, 73);
                //写入姓名
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXM(), fontSize, fontColor, 50, 100);
                //写入性别
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXB(), fontSize, fontColor, 215, 100);

                //写入证件号码
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getZJ(), fontSize, fontColor, 50, 140);

                //写入编号
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getBH(), fontSize - 4, fontColor, 120, 265);
                //写入姓名
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXM(), fontSize, fontColor, 50, 285);
                //写入性别
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXB(), fontSize, fontColor, 215, 285);
                //出生日期
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getCSRQ(), fontSize, fontColor, 50, 325);

                //写入民族
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getMZ(), fontSize, fontColor, 215, 325);

                //写入证件号码
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getZJ(), fontSize, fontColor, 50, 365);

                if (info.getHJD() != null && info.getHJD().length() > 14) {
                    //写入户籍地址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getHJD().substring(0, 14), fontSize, fontColor, 50, 395);
                    //写入户籍地址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getHJD().substring(14), fontSize, fontColor, 50, 410);
                } else {
                    //写入户籍地址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getHJD().substring(9), fontSize, fontColor, 50, 405);
                }

                if (info.getXZZ() != null && info.getXZZ().length() > 14) {
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXZZ().substring(0, 14), fontSize, fontColor, 50, 430);
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXZZ().substring(14), fontSize, fontColor, 50, 445);
                } else {
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getXZZ().substring(9), fontSize, fontColor, 50, 440);
                }

                //

                //写入单位
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getDWMC(), fontSize, fontColor, 50, 475);

                //写入人员
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getYHXM(), fontSize, fontColor, 50, 515);

                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getTJSJ(), fontSize - 4, fontColor, 215, 515);

                if (info.getBZ() == null) {
                    info.setBZ("");
                }
                //info.setBZ("");
                if (info.getBZ() != null && info.getBZ().length() > 14) {
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getBZ().substring(0, 14), fontSize, fontColor, 50, 545);
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getBZ().substring(14), fontSize, fontColor, 50, 560);
                } else {
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getBZ(), fontSize, fontColor, 50, 550);
                }
                bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getDH(), fontSize-4, fontColor, 15, 610);
                //info.setDD("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试");
                if (info.getDD() != null && info.getDD().length() > 22) {
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getDD().substring(0, 22), fontSize-4, fontColor, 15, 625);
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getDD().substring(22), fontSize-4, fontColor, 15, 640);
                } else {
                    //写入家庭住址
                    bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.getDD(), fontSize-4, fontColor, 15, 630);
                }
                //写入备注
//            bmp = ImageUtil.drawTextToLeftTop(mActivity, bmp, info.bz(), fontSize, fontColor, 65, 670);


//            bmp1=BitmapFactory.decodeResource(getResources(), R.drawable.two);
//            //写入条码
//            bmp1 = ImageUtil.drawBmpToBitmap(mActivity, bmp1, qrBmp, 30, 260);
//            //写入编号
//            bmp1 = ImageUtil.drawTextToLeftTop(mActivity, bmp1, info.getBH(), fontSize, fontColor, 151, 40);
//
//            //写入姓名
//            bmp1 = ImageUtil.drawTextToLeftTop(mActivity, bmp1, info.getXM(), fontSize, fontColor, 65, 175);
//            //写入性别
//            bmp1 = ImageUtil.drawTextToLeftTop(mActivity, bmp1, info.getXB(), fontSize, fontColor, 220, 175);
//
//            //写入证件号码
//            bmp1 = ImageUtil.drawTextToLeftTop(mActivity, bmp1, info.getZJ(), fontSize-4, fontColor, 65, 250);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        review.setImageBitmap(bmp);
                        button_send.setEnabled(true);
                        //review1.setImageBitmap(bmp1);
                        //button_send1.setEnabled(true);
                    }
                });
            }
        });
    }

    private void connBluetooth(final Thread th) {
        Thread thread = new Thread(new Runnable() {


            @Override
            public void run() {
                //BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                //PrinterInstance.getPrinterInstance(bluetoothDevice, null, 0, null);
                //PrinterInstance.mPrinter.openConnection();
                selectDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                if (clientSocket == null && selectDevice != null) {
                    // 获取到客户端接口
                    try {
                        clientSocket = selectDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
                        // 向服务端发送连接
                        clientSocket.connect();
                        (TestActivity.this).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button_connect.setText(name + "（" + address + "）连接成功");
                                isConnect = true;
                                //button_connect.setText(textView.getText().toString() + "\n" + "发送");
                                //tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
                            }
                        });
                        th.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        (TestActivity.this).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button_connect.setText(name + "（" + address + "）连接失败");
                                //button_connect.setText(textView.getText().toString() + "\n" + "发送");
                                //tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
                            }
                        });
                        clientSocket = null;
                    }
                }
            }
        });
        thread.start();
    }

    public void print(Bitmap bmp) {

//        if(mPrinter.openConnection())
//        {
//            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
//            mPrinter.printImage(bmp, PrinterConstants.PAlign.CENTER, 0, false);
//            return;
//        }
//        else
//        {
//            ToastUtils.show(mActivity, "设备未连接成功,。", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
//        }
        //mPrinter.printText("打印单色位图演示：");


        if (selectDevice == null && address != "") {
            //通过地址获取到该设备
            selectDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        }

        try {
            // 判断客户端接口是否为空
            if (clientSocket == null && selectDevice != null) {
                // 获取到客户端接口
                clientSocket = selectDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
                // 向服务端发送连接
                clientSocket.connect();
            }

            if (clientSocket != null && !clientSocket.isConnected()) {
                clientSocket.connect();
            }

            if (os == null && clientSocket != null) {
                // 获取到输出流，向外写数据
                os = clientSocket.getOutputStream();

            }

            if (!thread.isAlive()) {
                // 线程开始
                thread.start();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ArrayList<byte[]> rowDataList = new ArrayList<byte[]>();
            //Bitmap bmp=review.getDrawingCache();

            List<byte[]> D1 = bitmaptobyte(bmp, W, H);
            int length = D1.size();
            int spaceStart = 0;
            int sIndex = 0;
            boolean isSpace = true;
            int spaceCount = 0;
            int spaceEnd = 0;
            int nL, nH;
            int sL, sH;
            Bitmap bitmap203 = Bitmap.createBitmap(W * 8, H * 8, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap203);
            canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), new Rect(0, 0, W * 8, H * 8), new Paint());
            bmp = bitmap203;
            int fw = bmp.getWidth() / 8;
            int bh = bmp.getHeight();

            for (int i = 0; i < length; i++) {
                byte[] rowData = D1.get(i);
                if (rowData.length == 2 && rowData[0] == 0x15) {
                    if (isSpace) {
                        ++spaceStart;
                        sIndex = i;
                    } else {
                        ++spaceCount;
                    }
                } else {
                    while (spaceCount > 0) {
                        baos.write(new byte[]{0x15, 0x01});
                        rowDataList.add(new byte[]{0x15, 0x01});
                        --spaceCount;
                    }
                    isSpace = false;
                    baos.write(rowData);
                    rowDataList.add(rowData);
                }
            }

            for (int i = length - 1; i > sIndex; i--) {
                byte[] rowData = D1.get(i);
                if (rowData.length == 2 && rowData[0] == 0x15) {
                    spaceEnd++;
                } else {
                    break;
                }
            }

            int usePrint = length - spaceStart - spaceEnd;
            nL = usePrint % 256; // 有效打印区域
            nH = usePrint / 256;
            sL = spaceStart % 256; // 头部空白区域
            sH = spaceStart / 256;

            // 计算baos的总字节数
            int D11 = 0, D2 = 0, D3 = 0, D4 = 0;
            byte[] data = baos.toByteArray();
            int dataCount = data.length;
            D11 = dataCount & 0xff;
            D2 = (dataCount >> 8) & 0xff;
            D3 = (dataCount >> 16) & 0xff;
            D4 = (dataCount >> 24) & 0xff;

            for (; labelIndex <= labelCount; labelIndex++) {
                if (os != null) {
                    os.write(new byte[]{0x17, (byte) nL, (byte) nH, (byte) sL, (byte) sH,
                            (byte) fw, (byte) 0, (byte) labelCount, (byte) labelIndex, (byte) D11,
                            (byte) D2, (byte) D3, (byte) D4});

                    ArrayList<byte[]> sendData = new ArrayList<byte[]>();
                    int packSize = 512;
                    int send = 0;
                    byte[] cmd = new byte[0];
                    for (byte[] d : rowDataList) {
                        if (send + d.length > packSize) {
                            sendData.add(cmd);
                            send = 0;
                            cmd = new byte[0];
                        }
                        send += d.length;
                        byte[] byte_3 = new byte[cmd.length + d.length];
                        System.arraycopy(cmd, 0, byte_3, 0, cmd.length);
                        System.arraycopy(d, 0, byte_3, cmd.length, d.length);
                        cmd = byte_3;
                    }
                    if (cmd.length > 0) {
                        sendData.add(cmd);
                    }

                    for (byte[] d : sendData) {
                        os.write(d);
                    }

                    //发送结尾0x0C
                    os.write(new byte[]{0x0C});

                    (TestActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(textView.getText().toString() + "\n" + "发送");
                            //tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
                        }
                    });
                }
                Thread.sleep(2000);
            }

            labelCount = 1;
            labelIndex = 1;
            (TestActivity.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
                }
            });

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            (TestActivity.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(textView.getText().toString() + "\n" + "发送失败");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_connect://连接蓝牙
                bluetoothselectdialog.Show(this);
                LinearLayout ll = bluetoothselectdialog.getly();
                ListView listView = (ListView) ll.findViewById(R.id.list_item);
                setClick(listView, thread);
                break;
            case R.id.button_send://打印图片
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //原始打印发送
//                        if(et_W.getText().length()>0){
//                            W=Integer.valueOf(et_W.getText().toString());
//                        }
//                        if(et_H.getText().length()>0){
//                            H=Integer.valueOf(et_H.getText().toString());
//                        }
                        if (isConnect)
                            print(bmp);
                        else
                            ToastUtils.show(mActivity, "未连接打印机，请连接。", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        //print(review1.getDrawingCache());
                    }
                }).start();

                break;
//            case R.id.button_send1:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //原始打印发送
////                        if(et_W.getText().length()>0){
////                            W=Integer.valueOf(et_W.getText().toString());
////                        }
////                        if(et_H.getText().length()>0){
////                            H=Integer.valueOf(et_H.getText().toString());
////                        }
//                            //print(bmp1);
//                            //print(review1.getDrawingCache());
//                        }
//                    }).start();
//
//                    break;
            case R.id.button_clean:
                textView.setText("");
                //et_W.setText(null);
                //et_H.setText(null);
                labelCount = 1;
                labelIndex = 1;
                break;
//            case R.id.bt_minus:
//                if(labelCount>1){
//                    labelCount--;
//                }
//                tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
//                break;
//            case R.id.bt_add:
//                labelCount++;
//                tv_number.setText(String.valueOf(labelIndex)+"/"+String.valueOf(labelCount));
//                break;
        }
    }

    public void setClick(ListView listView, final Thread th) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                // 获取
                address = map.get("address");
                String name = map.get("title");

                MyApp.instance.dataKeeper.put("bluetooth_address", address);
                MyApp.instance.dataKeeper.put("bluetooth_name", name);
                bluetoothselectdialog.getbluetooth().cancelDiscovery();
                // 关闭窗口
                bluetoothselectdialog.Hide();
                connBluetooth(th);

            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_test;
    }

    // 服务端接收信息线程
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;// 服务端接口
        //private BluetoothSocket socket;// 获取到客户端的接口
        private InputStream is;// 获取到输入流
        private OutputStream os;// 获取到输出流

        public AcceptThread() {
        }

        public void run() {
            try {
                if (clientSocket != null) {
                    // 获取到输入流
                    is = clientSocket.getInputStream();
                    // 获取到输出流
                    os = clientSocket.getOutputStream();
                }
                // 无线循环来接收数据
                while (true) {
                    if (is != null) {
                        int datasize = is.available();
                        if (datasize > 0) {
                            byte[] buffer = new byte[is.available()];
                            is.read(buffer);
                            String data = "";
                            for (int i = 0; i < buffer.length; i++) {
                                data = data + String.valueOf(buffer[i]) + "   ";
                            }
                            final String finalData = data;
                            (TestActivity.this).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(textView.getText().toString() + "\n" + "接收数据" + finalData);
                                }
                            });
                        }

/*                        byte[] bytes = new byte[1024];// 缓冲数据流
                        int count=is.read(bytes);
                        if(count!=-1){
                            byte[] contents = new byte[count];
                            for (int i = 0; i < contents.length; i++) {
                                contents[i] = bytes[i];
                            }
                            String data = "";
                            for (int i = 0; i < contents.length; i++) {
                                data = data + String.valueOf(contents[i]) + "   ";
                            }
                            final String finalData = data;
                            (MainActivity.this).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(textView.getText().toString() + "\n" + "接收数据" + finalData);
                                }
                            });
                        }

                        while (true) {
                            int i=-1;
                            if ((i = is.read()) != -1) {
                                final int finalI = i;
                                (MainActivity.this).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(textView.getText().toString() + "\n" + "接收数据" + String.valueOf(finalI));
                                    }
                                });
                            }
                        }*/
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }

    private static long msgtime = 0;
    //T20 打印错误数据处理
    public Handler t20Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] buffer = bundle.getByteArray("result");
            if (buffer[0] == 1 || msgtime == 0) {
                long a = System.currentTimeMillis() - msgtime;
                if (msgtime != 0 && a > 10000) {
                    msgtime = 0;
                    Toast.makeText(TestActivity.this, String.valueOf(a), Toast.LENGTH_SHORT).show();
                } else {
                    Message time = new Message();
                    Bundle data = new Bundle();
                    data.putByteArray("result", new byte[]{1});
                    time.setData(data);
                    Toast.makeText(TestActivity.this, String.valueOf(a) + "send", Toast.LENGTH_SHORT).show();
                    t20Handler.sendMessageDelayed(time, 10000);
                }
            }
            if (buffer.length >= 4) {
                msgtime = System.currentTimeMillis();
                String data = "";
                for (int i = 0; i < buffer.length; i++) {
                    data = data + "    " + String.valueOf(buffer[i]);
                }
                Toast.makeText(TestActivity.this, String.valueOf(msgtime) + "data:" + data, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (clientSocket != null && clientSocket.isConnected()) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clientSocket = null;
        unregisterReceiver(myreceiver);
        super.onDestroy();
    }

    private List<byte[]> bitmaptobyte(Bitmap bmp, int W, int H) {
        Bitmap bitmap203 = Bitmap.createBitmap(W * 8, H * 8, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap203);
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), new Rect(0, 0, W * 8, H * 8), new Paint());
        bmp = bitmap203;
        int Width = bmp.getWidth();
        int Height = bmp.getHeight();
        int useRowWith = (Width >= 48 * 8) ? 48 * 8 : Width;//有效宽度（不超出最大宽度）

        List<byte[]> D1 = new ArrayList<byte[]>();

        byte rowData[] = new byte[useRowWith / 8];
        for (int i = 0; i < Height; i++) {
            pixelToByteArray(useRowWith, bmp, i, rowData);

            // =========================================判断是否全空行=================================
            int rowOffset = 0;
            int tail = rowData.length - 1;
            for (; tail >= 0; tail--) {
                if (rowData[tail] != 0)
                    break;
            }
            if (tail < 0) {
                D1.add(new byte[]{0x15, 0x01});// 空行指令
            } else {
                int index = 0; // 16 n n个数据 [0,tail] tail+1 数据
                byte[] result = new byte[1 + 1 + rowOffset + tail + 1]; // 长度: [16] +
                // [n] + [n个数据]
                result[index++] = 0x16;
                result[index++] = (byte) (rowOffset + tail + 1);
                for (int j = 0; j < rowOffset; j++) {
                    result[index++] = 0x00; // 无效指令
                }
                for (int j = 0; j <= tail; j++) {
                    result[index++] = rowData[j];
                }
                D1.add(result);
            }
        }
        return D1;
    }


    private static void pixelToByteArray(int useRowWith, Bitmap bitmap,
                                         int row, byte[] rowData) {
        // 从图片矩阵转换为byte数据
        int gray, sum = 0, index = 0;
        for (int w = 0; w < useRowWith; w++) {
            int pixels = bitmap.getPixel(w, row);
            gray = toGray(Color.red(pixels), Color.green(pixels),
                    Color.blue(pixels));
            if (gray <= 172) {
                sum |= 1 << (7 - w % 8);
            }
            if ((w + 1) % 8 == 0) {
                rowData[index++] = (byte) sum;
                sum = 0;
            }
        }
    }

    public static int toGray(int r, int g, int b) {
        int sum = r * 19661 + g * 38666 + b * 7209;
        return sum >> 16 & 0xFF;
    }
}
