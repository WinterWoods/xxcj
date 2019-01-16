package com.sz.baseuiframe.dialog;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.sz.baseuiframe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluetoothSelectDialog {

    static Dialog _builder = null;
    private BroadcastReceiver mReceiver;
    private LinearLayout ll;
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothSelectDialog(){
    }


    public void Show(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetoothselect, null);
        ll = (LinearLayout) view.findViewById(R.id.bluetoothselectcontent);
        // 加载蓝牙设备列表
        if (!loadbluetoothlist(ll,context))
            return;

        _builder = new Dialog(context, R.style.dialog);
        _builder.setContentView(view);
        _builder.show();
        _builder.setCancelable(true);
        _builder.setCanceledOnTouchOutside(true);
    }

    public static void Hide() {
        if (_builder != null) {
            _builder.hide();
        }
    }

    // 加载蓝牙设备
    boolean loadbluetoothlist(LinearLayout v, final Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 获得本机蓝牙适配器对象引用
        JSONArray json = null;
        try {
            json = ListBondedDevices(bluetoothAdapter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        List<Map<String, Object>> mData = null;

        try {
            mData = getData(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Hide();
            Toast.makeText(context, "加载蓝牙设备失败！", Toast.LENGTH_SHORT).show();
            return false;
        }

        SimpleAdapter adapter = new SimpleAdapter(context, mData, R.layout.bluetootchlist, new String[] { "title", "address",
                "img" }, new int[] { R.id.bluetootchlist_devicename, R.id.bluetoothlist_address, R.id.bluetootchlist_icon });

        ListView listView = new ListView(context);
        listView.setId(R.id.list_item);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);// 不显示分割线
        loadData(context,bluetoothAdapter,mData,adapter);
        v.removeAllViews();
        v.addView(listView);

        return true;
    }

    // 获取已匹配的蓝牙设备
    static JSONArray ListBondedDevices(BluetoothAdapter bluetoothAdapter) throws JSONException {
        JSONArray deviceList = new JSONArray();
        Set<BluetoothDevice> bondedDevices = null;
        bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            JSONObject json = new JSONObject();
            if(device.getName()==null||device.getName().equals("")){continue;}
            json.put("name", device.getName());
            json.put("address", device.getAddress());
            json.put("id", device.getAddress());
            if (device.getBluetoothClass() != null) {
                json.put("class", device.getBluetoothClass().getDeviceClass());
            }
            if(!(device.getName().toUpperCase().startsWith("T10")||
                    device.getName().toUpperCase().startsWith("D20")||
                    device.getName().toUpperCase().startsWith(""))) continue;
            deviceList.put(json);
        }
        return deviceList;
    }

    static List<Map<String, Object>> getData(JSONArray deviceList) throws JSONException {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = null;

        int len = deviceList.length();
        for (int i = 0; i < len; i++) {
            JSONObject o = deviceList.getJSONObject(i);
            map = new HashMap<String, Object>();
            map.put("title", o.getString("name"));
            map.put("address", o.getString("address"));
            map.put("img", R.drawable.print2);
            list.add(map);
        }

        return list;
    }

    protected void loadData(Context context, BluetoothAdapter bluetoothAdapter, final List<Map<String, Object>> mData, final SimpleAdapter adapter) {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //得到intent里面的信息
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) return;
                    String deviceName = device.getName();
                    if (deviceName == null) return;
                    //排除机型
                    if (!(device.getName().toUpperCase().startsWith("T10")||
                            device.getName().toUpperCase().startsWith("D20")||
                            device.getName().toUpperCase().startsWith(""))) return;
                    if(mData!=null){
                        for (int i=0;i<mData.size();i++){
                            if(device.getName().equals(mData.get(i).get("title"))){
                                return;
                            }
                        }
                    }
                    Map<String, Object> map  = new HashMap<String, Object>();
                    map.put("title", device.getName());
                    map.put("address", device.getAddress());
                    map.put("img", R.drawable.print2);
                    mData.add(map);
                    adapter.notifyDataSetInvalidated();
                }
            }
        };

        //创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND,查找蓝牙
        IntentFilter intentFileter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //注册广播接收器
        context.registerReceiver(mReceiver, intentFileter);
        this.bluetoothAdapter.cancelDiscovery();
        this.bluetoothAdapter.startDiscovery();
    }

    public LinearLayout getly(){
        return ll;
    }

    public BluetoothAdapter getbluetooth(){
        return bluetoothAdapter;
    }

}
