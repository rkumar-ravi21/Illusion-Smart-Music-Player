package com.saxena.kshitij.miband2connect;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class GattClientMainActivity extends AppCompatActivity {

    private ArrayAdapter<?> genericListAdapter;
    private ArrayList<BluetoothDevice> deviceArrayList;
    private ListView deviceListView;
    private BluetoothGattCallback miBandGattCallBack;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService variableService;
    private final Object object = new Object();
    private SharedPreferences sharedPreferences;
    private Map<UUID, String> deviceInfoMap;
    private TextView heartRate;

    private RequestQueue volleyRequestQueue;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private Sensor gravitySensor;
    private Sensor accelerometerSensor;
    private SensorEventListener gyroscopeEventListener;
    private SensorEventListener gravityEventListener;
    private SensorEventListener accelerometerEventListener;

    private TextView gyroscopeX;
    private TextView gyroscopeY;
    private TextView gyroscopeZ;

    private TextView gravityX;
    private TextView gravityY;
    private TextView gravityZ;

    private TextView accelerometerX;
    private TextView accelerometerY;
    private TextView accelerometerZ;

    //Variable to Stop(Unregister Listener)
    int gravityCount = 0;
    int gyroscopeCount = 0;
    int accelerometerCount = 0;

    ArrayList<String> gravityXArray = new ArrayList<String>();
    ArrayList<String> gravityYArray = new ArrayList<String>();
    ArrayList<String> gravityZArray = new ArrayList<String>();
    ArrayList<String> gyroscopeXArray = new ArrayList<String>();
    ArrayList<String> gyroscopeYArray = new ArrayList<String>();
    ArrayList<String> gyroscopeZArray = new ArrayList<String>();
    ArrayList<String> accelerometerXArray = new ArrayList<String>();
    ArrayList<String> accelerometerYArray = new ArrayList<String>();
    ArrayList<String> accelerometerZArray = new ArrayList<String>();

    private int gravityDone, gyroscopeDone, accelerometerDone, sendCalled = 0;

    private int samplingSize = 120; //No. of values of each kind to be send.

    private String baseUrl = "10.42.0.1:5000";
    private String firstSongURLSuffix = "/first_song";
    private String nextSongURLSuffix = "/next_song";
    private String defaultMediaLinkSuffix = "DB/M108.ogg";
    private MediaPlayer mediaPlayer;

    EditText baseURLEditText;


    private void initializeSensorVariables() {
        gravityCount = 0;
        gyroscopeCount = 0;
        accelerometerCount = 0;

        gravityXArray = new ArrayList<String>();
        gravityYArray = new ArrayList<String>();
        gravityZArray = new ArrayList<String>();
        gyroscopeXArray = new ArrayList<String>();
        gyroscopeYArray = new ArrayList<String>();
        gyroscopeZArray = new ArrayList<String>();
        accelerometerXArray = new ArrayList<String>();
        accelerometerYArray = new ArrayList<String>();
        accelerometerZArray = new ArrayList<String>();

        gravityDone = 0;
        gyroscopeDone = 0;
        accelerometerDone = 0;
        sendCalled = 0;
    }

    private void playFile(String mediaLink) {
        if (mediaLink == null) {
            mediaLink = baseUrl+defaultMediaLinkSuffix;
        }
        if(mediaPlayer==null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                showToast("Playing Song");
                mediaPlayer.start();
            }
        });
        try {
            mediaPlayer.setDataSource(mediaLink);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            showToast("Error in Loading Media");
            Log.e("LoadingMediaException", e.getStackTrace().toString());
        }
    }

    //    private String gyroscopeFilename = "gyroscope.csv";
//    private FileOutputStream gyroscopeOutputStream;
//    private int gyroscopeWritingCount = 0;
//
//    private void writeToCSV(String timestamp, String coordinateX, String coordinateY, String coordinateZ) {
//        String value = timestamp + "," + coordinateX + "," + coordinateY + "," + coordinateZ + "\n";
//        try {
//            if (gyroscopeOutputStream == null) {
//                gyroscopeOutputStream = openFileOutput(gyroscopeFilename, Context.MODE_APPEND);
//            }
//            gyroscopeOutputStream.write(value.getBytes());
//            gyroscopeWritingCount++;
//            if (gyroscopeWritingCount > 20) {
//                gyroscopeOutputStream.close();
//                gyroscopeOutputStream = null;
//                gyroscopeWritingCount = 0;
//                System.exit(0);
//            }
//            gyroscopeOutputStream.close();
//            Toast.makeText(this, "Written to file successfully", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("GyroscopeFileWriting", e.getMessage());
//        }
//    }
    protected void sensorsCreate() {
//        gyroscopeX = findViewById(R.id.gyroscope_x);
//        gyroscopeY = findViewById(R.id.gyroscope_y);
//        gyroscopeZ = findViewById(R.id.gyroscope_z);
//
//        gravityX = findViewById(R.id.gravity_x);
//        gravityY = findViewById(R.id.gravity_y);
//        gravityZ = findViewById(R.id.gravity_z);
//
//        accelerometerX = findViewById(R.id.accelerometer_x);
//        accelerometerY = findViewById(R.id.accelerometer_y);
//        accelerometerZ = findViewById(R.id.accelerometer_z);

        initializeSensorVariables();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        gyroscopeEventListener = new SensorEventListener() {
            String tag = "gyroscope";

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (gyroscopeCount >= samplingSize) {

                    gyroscopeDone = 1;
                    sensorManager.unregisterListener(gyroscopeEventListener, gyroscopeSensor);

                    if (accelerometerDone == 1 && gravityDone == 1 && gyroscopeDone == 1 && sendCalled == 0) {
                        sendCalled = 1;
                        sendData();
                    }
                }
                if (gyroscopeX != null) {
                    gyroscopeX.setText(event.values[0] + "");

                }
                if (gyroscopeY != null) {
                    gyroscopeY.setText(event.values[1] + "");

                }
                if (gyroscopeZ != null) {
                    gyroscopeZ.setText(event.values[2] + "");

                }
                gyroscopeXArray.add(String.valueOf(event.values[0]));
                gyroscopeYArray.add(String.valueOf(event.values[1]));
                gyroscopeZArray.add(String.valueOf(event.values[2]));

//                Log.i(tag, event.values[0] + "");
//                Log.i(tag, event.values[1] + "");
//                Log.i(tag, event.values[2] + "");

                gyroscopeCount = gyroscopeCount + 1;

//                writeToCSV(event.timestamp + "", event.values[0] + "", event.values[1] + "", event.values[2] + "");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        gravityEventListener = new SensorEventListener() {

            String tag = "gravity";

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (gravityCount >= samplingSize) {

                    gravityDone = 1;
                    sensorManager.unregisterListener(gravityEventListener, gravitySensor);

                    if (accelerometerDone == 1 && gravityDone == 1 && gyroscopeDone == 1 && sendCalled == 0) {

                        sendCalled = 1;
                        sendData();
                    }
                }

                if (gravityX != null) {
                    gravityX.setText(event.values[0] + "");
                }
                if (gravityY != null) {
                    gravityY.setText(event.values[1] + "");
                }
                if (gravityZ != null) {
                    gravityZ.setText(event.values[2] + "");
                }
                gravityXArray.add(String.valueOf(event.values[0]));
                gravityYArray.add(String.valueOf(event.values[1]));
                gravityZArray.add(String.valueOf(event.values[2]));

//                Log.i(tag, event.values[0] + "");
//                Log.i(tag, event.values[1] + "");
//                Log.i(tag, event.values[2] + "");

                gravityCount = gravityCount + 1;

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        accelerometerEventListener = new SensorEventListener() {

            String tag = "accelerometer";

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (accelerometerCount >= samplingSize) {

                    accelerometerDone = 1;
                    sensorManager.unregisterListener(accelerometerEventListener, accelerometerSensor);

                    if (accelerometerDone == 1 && gravityDone == 1 && gyroscopeDone == 1 && sendCalled == 0) {

                        sendCalled = 1;
                        sendData();

                    }

                }

                if (accelerometerX != null) {
                    accelerometerX.setText(event.values[0] + "");
                }
                if (accelerometerY != null) {
                    accelerometerY.setText(event.values[1] + "");
                }
                if (accelerometerZ != null) {
                    accelerometerZ.setText(event.values[2] + "");
                }
                accelerometerXArray.add(String.valueOf(event.values[0]));
                accelerometerYArray.add(String.valueOf(event.values[1]));
                accelerometerZArray.add(String.valueOf(event.values[2]));

//                Log.i(tag, event.values[0] + "");
//                Log.i(tag, event.values[1] + "");
//                Log.i(tag, event.values[2] + "");

                accelerometerCount = accelerometerCount + 1;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gravityEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void getNextSong() {
        JSONObject obj;
        try {
            obj = new JSONObject();
            obj.put("Please Send", "Next Song");
        } catch (JSONException e) {
            showToast("Please try again later");
            Log.e("JSONException", "In send data \n" + e.getStackTrace().toString());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl+nextSongURLSuffix, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ResponseFromServer", response.toString());
                        showToast(response.toString());
                        try {
                            //Extracting data from response
                            String link = response.getString("path");
                            playFile(link);
                        } catch (JSONException e) {
                            Log.e("ParsingResponseExp", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Try once again, please...");
                        Log.e("Volley Next Song", "Error:"+error.getMessage());
                        error.printStackTrace();
                    }
                }
        );

        volleyRequestQueue.add(request);
        showToast("Requesting Next Song");

    }

    private void sendData() {

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject obj = new JSONObject();
            obj.put("heartRate", heartRate.getText());
            jsonArray.put(obj);
        } catch (JSONException e) {
            Log.e("JSONException", "In send data \n" + e.getStackTrace().toString());
        }
        for (int i = 0; i <= samplingSize - 1; i++) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("gravityX", gravityXArray.get(i));
                obj.put("gravityY", gravityYArray.get(i));
                obj.put("gravityZ", gravityZArray.get(i));
                obj.put("gyroscopeX", gyroscopeXArray.get(i));
                obj.put("gyroscopeY", gyroscopeYArray.get(i));
                obj.put("gyroscopeZ", gyroscopeZArray.get(i));
                obj.put("accelerometerX", accelerometerXArray.get(i));
                obj.put("accelerometerY", accelerometerYArray.get(i));
                obj.put("accelerometerZ", accelerometerZArray.get(i));
            } catch (JSONException e) {
                Log.e("JSONException", "In send data \n" + e.getStackTrace().toString());
            }
            jsonArray.put(obj);
        }

//        Log.i("JSON Array:",jsonArray.toString());

        final JSONObject finalJsonObject = new JSONObject();
        try {
            finalJsonObject.put("data", jsonArray);
            Log.i("JSON Object:", finalJsonObject.toString());

        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        }

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, baseUrl+firstSongURLSuffix, //Change this requestURL to your API
                finalJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ResponseFromServer", response.toString());
                        showToast(response.toString());
                        try {
                            //Extracting data from response
                            String link = response.getString("path");
                            playFile(link);
                        } catch (JSONException e) {
                            Log.e("ParsingResponseExp", "ErrorResponse"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Sending Data Error", "ErrorResponse"+error.getMessage());
                showToast("Try once again, please...");
                error.printStackTrace();
            }
        });

        volleyRequestQueue.add(req);
        showToast("Sending Data & Requesting Song");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceInfoMap = new HashMap<>();
        heartRate = (TextView) findViewById(R.id.heartRate);
        initialiseViewsAndComponents();
        getPermissions();
        enableBTAndDiscover();
        //Sensors Code
        volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        Button firstSong = (Button) findViewById(R.id.firstSong);
        Button nextSong = (Button) findViewById(R.id.nextSong);
        baseURLEditText = (EditText) findViewById(R.id.baseUrlEditText);
        firstSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Please Wait");
                if(baseUrl!=null){
                    baseUrl = baseURLEditText.getText().toString();
                }
                sensorsCreate();
            }
        });
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Fetching Next Song");
                if(baseUrl!=null){
                    baseUrl = baseURLEditText.getText().toString();
                }
                getNextSong();
            }
        });


    }

    @Override
    protected void onDestroy() {
        bluetoothGatt.disconnect();
        super.onDestroy();
    }


    /*------Methods to deal with the received data*/
    private void handleDeviceInfo(BluetoothGattCharacteristic characteristic) {
        String value = characteristic.getStringValue(0);
        // Log.d("TAG", "onCharacteristicRead: " + value + " UUID " + characteristic.getUuid().toString());
        synchronized (object) {
            object.notify();
        }
        deviceInfoMap.put(characteristic.getUuid(), value);
    }

    private void handleGenericAccess(BluetoothGattCharacteristic characteristic) {
        String value = characteristic.getStringValue(0);
        Log.d("TAG", "onCharacteristicRead: " + value + " UUID " + characteristic.getUuid().toString());
        synchronized (object) {
            object.notify();
        }
    }

    private void handleGenericAttribute(BluetoothGattCharacteristic characteristic) {
        String value = characteristic.getStringValue(0);
        Log.d("TAG", "onCharacteristicRead: " + value + " UUID " + characteristic.getUuid().toString());
        synchronized (object) {
            object.notify();
        }
    }

    private void handleAlertNotification(BluetoothGattCharacteristic characteristic) {
        String value = characteristic.getStringValue(0);
        Log.d("TAG", "onCharacteristicRead: " + value + " UUID " + characteristic.getUuid().toString());
        synchronized (object) {
            object.notify();
        }
    }

    private void handleImmediateAlert(BluetoothGattCharacteristic characteristic) {
        String value = characteristic.getStringValue(0);
        Log.d("TAG", "onCharacteristicRead: " + value + " UUID " + characteristic.getUuid().toString());
        synchronized (object) {
            object.notify();
        }
    }

    private void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {

        Log.e("Heart", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0).toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                heartRate.setText(((characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)) / 256) + " bpm");
            }
        });

        // attach a file to FileWriter


    }


    /*------Methods to send requests to the device------*/
    private void authoriseMiBand() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        BluetoothGattService service = bluetoothGatt.getService(UUIDs.CUSTOM_SERVICE_FEE1);

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUIDs.CUSTOM_SERVICE_AUTH_CHARACTERISTIC);
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
            if (descriptor.getUuid().equals(UUIDs.CUSTOM_SERVICE_AUTH_DESCRIPTOR)) {
                Log.d("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + descriptor.getUuid().toString());
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
        }

        characteristic.setValue(new byte[]{0x01, 0x8, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45});
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    private void executeAuthorisationSequence(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value[0] == 0x10 && value[1] == 0x01 && value[2] == 0x01) {
            characteristic.setValue(new byte[]{0x02, 0x8});
            bluetoothGatt.writeCharacteristic(characteristic);
        } else if (value[0] == 0x10 && value[1] == 0x02 && value[2] == 0x01) {
            try {
                byte[] tmpValue = Arrays.copyOfRange(value, 3, 19);
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

                SecretKeySpec key = new SecretKeySpec(new byte[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45}, "AES");

                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] bytes = cipher.doFinal(tmpValue);


                byte[] rq = ArrayUtils.addAll(new byte[]{0x03, 0x8}, bytes);
                characteristic.setValue(rq);
                bluetoothGatt.writeCharacteristic(characteristic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //getting the device details
    private void getDeviceInformation() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        variableService = bluetoothGatt.getService(UUIDs.DEVICE_INFORMATION_SERVICE);

        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGenericAccessInfo() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        variableService = bluetoothGatt.getService(UUIDs.GENERIC_ACCESS_SERVICE);
        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
                deviceInfoMap.put(characteristic.getUuid(), characteristic.getStringValue(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void genericAttribute() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        variableService = bluetoothGatt.getService(UUIDs.GENERIC_ATTRIBUTE_SERVICE);
        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertNotification() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        variableService = bluetoothGatt.getService(UUIDs.ALERT_NOTIFICATION_SERVICE);
        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void immediateAlert() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        variableService = bluetoothGatt.getService(UUIDs.IMMEDIATE_ALERT_SERVICE);
        try {
            for (BluetoothGattCharacteristic characteristic : variableService.getCharacteristics()) {
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                bluetoothGatt.readCharacteristic(characteristic);
                synchronized (object) {
                    object.wait(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHeartRate() {
        if (bluetoothGatt == null) {
            showToast("Unable to connect");
            return;
        }
        variableService = bluetoothGatt.getService(UUIDs.HEART_RATE_SERVICE);
        BluetoothGattCharacteristic heartRateCharacteristic = variableService.getCharacteristic(UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC);
        BluetoothGattDescriptor heartRateDescriptor = heartRateCharacteristic.getDescriptor(UUIDs.HEART_RATE_MEASURMENT_DESCRIPTOR);

        bluetoothGatt.setCharacteristicNotification(heartRateCharacteristic, true);
        heartRateDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(heartRateDescriptor);
    }


    /*--------Connection and other basic methods---------*/
    private void getPermissions() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
                shouldShowRequestPermissionRationale("Please grant this app the following permissions to make it work properly");
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH}, 1);
            }
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_DENIED) {
                shouldShowRequestPermissionRationale("Please grand Location permission for scanning devices nearby");
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        } else {
            Toast.makeText(GattClientMainActivity.this, "This device doesn't support Bluetooth LE", Toast.LENGTH_LONG).show();
        }
    }

    private void enableBTAndDiscover() {
        final BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();

        final ProgressDialog searchProgress = new ProgressDialog(GattClientMainActivity.this);
        searchProgress.setIndeterminate(true);
        searchProgress.setTitle("BlueTooth LE Device");
        searchProgress.setMessage("Searching...");
        searchProgress.setCancelable(false);
        searchProgress.show();

        deviceArrayList = new ArrayList<BluetoothDevice>();
        genericListAdapter = new ArrayAdapter<>(GattClientMainActivity.this, R.layout.simple_list_item_1_modified, deviceArrayList);
        deviceListView.setAdapter(genericListAdapter);


        if (bluetoothAdapter == null) {
            Toast.makeText(GattClientMainActivity.this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);

        }
        final ScanCallback leDeviceScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.d("TAG", "Device found" + " " + result.getDevice().getAddress() + " " + result.getDevice().getName());
                if (!deviceArrayList.contains(result.getDevice())) {
                    deviceArrayList.add(result.getDevice());
                    genericListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };


        bluetoothAdapter.getBluetoothLeScanner().startScan(leDeviceScanCallback);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.getBluetoothLeScanner().stopScan(leDeviceScanCallback);
                searchProgress.dismiss();
            }
        }, 10000);
    }

    private void connectDevice(BluetoothDevice miBand) {

        if (miBand.getBondState() == BluetoothDevice.BOND_NONE) {
            miBand.createBond();
            Log.d("Bond", "Created with Device");
        }

        bluetoothGatt = miBand.connectGatt(getApplicationContext(), true, miBandGattCallBack);
    }

    private void initialiseViewsAndComponents() {
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        Button getBandDetails = (Button) findViewById(R.id.getBandDetails);

        sharedPreferences = getSharedPreferences("MiBandConnectPreferences", Context.MODE_PRIVATE);
        miBandGattCallBack = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch (newState) {
                    case BluetoothGatt.STATE_DISCONNECTED:
                        Log.d("Info", "Device disconnected");

                        break;
                    case BluetoothGatt.STATE_CONNECTED: {
                        Log.d("Info", "Connected with device");
                        Log.d("Info", "Discovering services");
                        gatt.discoverServices();
                    }
                    break;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                if (!sharedPreferences.getBoolean("isAuthenticated", false)) {
                    authoriseMiBand();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAuthenticated", true);
                    editor.apply();
                } else
                    Log.i("Device", "Already authenticated");
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

                switch (characteristic.getService().getUuid().toString()) {
                    case UUIDs.DEVICE_INFORMATION_SERVICE_STRING:
                        handleDeviceInfo(characteristic);
                        break;
                    case UUIDs.GENERIC_ACCESS_SERVICE_STRING:
                        handleGenericAccess(characteristic);
                        break;
                    case UUIDs.GENERIC_ATTRIBUTE_SERVICE_STRING:
                        handleGenericAttribute(characteristic);
                        break;
                    case UUIDs.ALERT_NOTIFICATION_SERVICE_STRING:
                        handleAlertNotification(characteristic);
                        break;
                    case UUIDs.IMMEDIATE_ALERT_SERVICE_STRING:
                        handleImmediateAlert(characteristic);
                        break;
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                switch (characteristic.getUuid().toString()) {
                    case UUIDs.CUSTOM_SERVICE_AUTH_CHARACTERISTIC_STRING:
                        executeAuthorisationSequence(characteristic);
                        break;
                    case UUIDs.HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING:
                        handleHeartRateData(characteristic);
                        break;
                }
            }


            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d("Descriptor", descriptor.getUuid().toString() + " Read");
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.v("Descriptor", descriptor.getUuid().toString() + " Written");
            }
        };

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectDevice((BluetoothDevice) parent.getItemAtPosition(position));
            }
        });

        getBandDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView serialNo, hardwareRev, softwareRev, deviceName;
                Button cancel;

                getDeviceInformation();
                getGenericAccessInfo();
                //temporary calls
                getHeartRate();

                final Dialog deviceInfoDialog = new Dialog(GattClientMainActivity.this);
                deviceInfoDialog.setContentView(R.layout.device_info);
                deviceInfoDialog.setCancelable(false);
                deviceInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        deviceInfoMap.clear();
                    }
                });

                deviceName = (TextView) deviceInfoDialog.findViewById(R.id.deviceName);
                serialNo = (TextView) deviceInfoDialog.findViewById(R.id.serialNumber);
                hardwareRev = (TextView) deviceInfoDialog.findViewById(R.id.hardwareRevision);
                softwareRev = (TextView) deviceInfoDialog.findViewById(R.id.softwareRevision);
                cancel = (Button) deviceInfoDialog.findViewById(R.id.cancel_action);

                deviceName.setText(deviceInfoMap.get(UUIDs.DEVICE_NAME_CHARACTERISTIC));
                serialNo.setText(deviceInfoMap.get(UUIDs.SERIAL_NUMBER));
                hardwareRev.setText(deviceInfoMap.get(UUIDs.HARDWARE_REVISION_STRING));
                softwareRev.setText(deviceInfoMap.get(UUIDs.SOFTWARE_REVISION_STRING));

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deviceInfoDialog.dismiss();
                    }
                });

                deviceInfoDialog.show();
            }
        });
    }
}
