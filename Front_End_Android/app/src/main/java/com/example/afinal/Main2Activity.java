package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    private RequestQueue requestQueue;

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

    private int samplingSize = 60; //No. of values of each kind to be send.

    private String gyroscopeFilename = "gyroscope.csv";
    private FileOutputStream gyroscopeOutputStream;
    private int gyroscopeWritingCount = 0;

    private void writeToCSV(String timestamp, String coordinateX, String coordinateY, String coordinateZ) {
        String value = timestamp + "," + coordinateX + "," + coordinateY + "," + coordinateZ + "\n";
        try {
            if (gyroscopeOutputStream == null) {
                gyroscopeOutputStream = openFileOutput(gyroscopeFilename, Context.MODE_APPEND);
            }
            gyroscopeOutputStream.write(value.getBytes());
            gyroscopeWritingCount++;
            if (gyroscopeWritingCount > 20) {
                gyroscopeOutputStream.close();
                gyroscopeOutputStream = null;
                gyroscopeWritingCount = 0;
                System.exit(0);
            }
            gyroscopeOutputStream.close();
            Toast.makeText(this, "Written to file successfully", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GyroscopeFileWriting", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        gyroscopeX = findViewById(R.id.gyroscope_x);
        gyroscopeY = findViewById(R.id.gyroscope_y);
        gyroscopeZ = findViewById(R.id.gyroscope_z);

        gravityX = findViewById(R.id.gravity_x);
        gravityY = findViewById(R.id.gravity_y);
        gravityZ = findViewById(R.id.gravity_z);

        accelerometerX = findViewById(R.id.accelerometer_x);
        accelerometerY = findViewById(R.id.accelerometer_y);
        accelerometerZ = findViewById(R.id.accelerometer_z);

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
                    gyroscopeXArray.add(String.valueOf(event.values[0]));

                }
                if (gyroscopeY != null) {
                    gyroscopeY.setText(event.values[1] + "");
                    gyroscopeYArray.add(String.valueOf(event.values[1]));

                }
                if (gyroscopeZ != null) {
                    gyroscopeZ.setText(event.values[2] + "");
                    gyroscopeZArray.add(String.valueOf(event.values[2]));

                }

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
                    gravityXArray.add(String.valueOf(event.values[0]));
                }
                if (gravityY != null) {
                    gravityY.setText(event.values[1] + "");
                    gravityYArray.add(String.valueOf(event.values[1]));
                }
                if (gravityZ != null) {
                    gravityZ.setText(event.values[2] + "");
                    gravityZArray.add(String.valueOf(event.values[2]));
                }

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
                    accelerometerXArray.add(String.valueOf(event.values[0]));
                }
                if (accelerometerY != null) {
                    accelerometerY.setText(event.values[1] + "");
                    accelerometerYArray.add(String.valueOf(event.values[1]));
                }
                if (accelerometerZ != null) {
                    accelerometerZ.setText(event.values[2] + "");
                    accelerometerZArray.add(String.valueOf(event.values[2]));
                }

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

    private void sendData() {

        JSONArray jsonArray = new JSONArray();
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
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }
//        Log.i("JSON Array:",jsonArray.toString());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        final JSONObject finalJsonObject = new JSONObject();
        try {
            finalJsonObject.put("data", jsonArray);
            Log.i("JSON Object:", finalJsonObject.toString());

        } catch (JSONException e) {
            Log.e("JSONException", e.toString());
        }

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,"http://192.168.43.16:5000/first_song", //Change this url to your API
//                "https://webhook.site/87bb1bfb-4af8-459e-a526-c819e41d7d66", //Change this url to your API
                finalJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showToast(response.toString());
                       /* try {
                            JSONObject jObj = response;

                            //Extracting data from response
                            String uid = jObj.getString("_id");
                            String name = jObj.getString("fullName");
                            String email = jObj.getString("email");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Sending Data Error", error.toString());
                showToast("Try once again, please...");
            }
        });

        requestQueue.add(req);
        showToast("Sending Message");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}