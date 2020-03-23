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
    Map<String, String> gravityX_params = new HashMap<String, String>();
    private TextView gravityY;
    private TextView gravityZ;

    private TextView accelerometerX;
    private TextView accelerometerY;
    private TextView accelerometerZ;

    //Variable to Stop(Unregister Listener)
    int gravity_Count = 0;
    int gyroscope_Count = 0;
    int acc_Count = 0;

    ArrayList<String> gravityXArray = new ArrayList<String>();
    ArrayList<String> gravityYArray = new ArrayList<String>();
    ArrayList<String> gravityZArray = new ArrayList<String>();
    ArrayList<String> gyroXArray = new ArrayList<String>();
    ArrayList<String> gyroYArray = new ArrayList<String>();
    ArrayList<String> gyroZArray = new ArrayList<String>();
    ArrayList<String> accXArray = new ArrayList<String>();
    ArrayList<String> accYArray = new ArrayList<String>();
    ArrayList<String> accZArray = new ArrayList<String>();

    int GravityDone, GryoDone, AccDone, sendCalled = 0;

    int Sampling_size = 20; //No. of values of each kind to be send.


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

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);


        gyroscopeEventListener = new SensorEventListener() {

            String tag = "gyroscope";

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (gyroscope_Count == Sampling_size) {

                    GryoDone = 1;
                    sensorManager.unregisterListener(gyroscopeEventListener, gyroscopeSensor);
                    SensorClosed();

                    if (AccDone == 1 && GravityDone == 1 && GryoDone == 1 && sendCalled == 0) {

                        sendCalled = 1;
                        sendData();

                    }

                }


                if (gyroscopeX != null) {
                    gyroscopeX.setText(event.values[0] + "");
                    gyroXArray.add(String.valueOf(event.values[0]));

                }
                if (gyroscopeY != null) {
                    gyroscopeY.setText(event.values[1] + "");
                    gyroYArray.add(String.valueOf(event.values[1]));

                }
                if (gyroscopeZ != null) {
                    gyroscopeZ.setText(event.values[2] + "");
                    gyroZArray.add(String.valueOf(event.values[2]));

                }

                Log.i(tag, event.values[0] + "");
                Log.i(tag, event.values[1] + "");
                Log.i(tag, event.values[2] + "");

                gyroscope_Count = gyroscope_Count + 1;
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

                if (gravity_Count == Sampling_size) {

                    GravityDone = 1;
                    sensorManager.unregisterListener(gravityEventListener, gravitySensor);
                    SensorClosed();

                    if (AccDone == 1 && GravityDone == 1 && GryoDone == 1 && sendCalled == 0) {

                        sendCalled = 1;
                        sendData();
                    }


                }

                if (gravityX != null) {
                    gravityX.setText(event.values[0] + "");
                    gravityXArray.add(String.valueOf(event.values[0]));
                    //gravityX_params.put("gravityX"+gravity_Count,String.valueOf(event.values[0]));

                }
                if (gravityY != null) {
                    gravityY.setText(event.values[1] + "");
                    gravityYArray.add(String.valueOf(event.values[1]));

                }
                if (gravityZ != null) {
                    gravityZ.setText(event.values[2] + "");
                    gravityZArray.add(String.valueOf(event.values[2]));

                }

                gravity_Count = gravity_Count + 1;

                Log.i(tag, event.values[0] + "");
                Log.i(tag, event.values[1] + "");
                Log.i(tag, event.values[2] + "");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        accelerometerEventListener = new SensorEventListener() {

            String tag = "accelerometer";

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (acc_Count == Sampling_size) {

                    AccDone = 1;
                    sensorManager.unregisterListener(accelerometerEventListener, accelerometerSensor);
                    SensorClosed();

                    if (AccDone == 1 && GravityDone == 1 && GryoDone == 1 && sendCalled == 0) {

                        sendCalled = 1;
                        sendData();

                    }

                }

                if (accelerometerX != null) {
                    accelerometerX.setText(event.values[0] + "");
                    accXArray.add(String.valueOf(event.values[0]));
                }
                if (accelerometerY != null) {
                    accelerometerY.setText(event.values[1] + "");
                    accYArray.add(String.valueOf(event.values[1]));

                }
                if (accelerometerZ != null) {
                    accelerometerZ.setText(event.values[2] + "");
                    accZArray.add(String.valueOf(event.values[2]));

                }

                Log.i(tag, event.values[0] + "");
                Log.i(tag, event.values[1] + "");
                Log.i(tag, event.values[2] + "");

                acc_Count = acc_Count + 1;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, 2 * 1000 * 1000);
        sensorManager.registerListener(gravityEventListener, gravitySensor, 2 * 1000 * 1000);
        sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, 2 * 1000 * 1000);

    }

    private void sendData() {


        JSONArray JsonArray = new JSONArray();
        for (int x = 0; x <= Sampling_size - 1; x++) {
            JSONObject obj = new JSONObject();
            try {

                obj.put("GravityX", gravityXArray.get(x));
                obj.put("GravityY", gravityYArray.get(x));
                obj.put("GravityZ", gravityZArray.get(x));
                obj.put("GyroX", gyroXArray.get(x));
                obj.put("GyroY", gyroYArray.get(x));
                obj.put("GyroZ", gyroZArray.get(x));
                obj.put("accX", accXArray.get(x));
                obj.put("accY", accYArray.get(x));
                obj.put("accZ", accZArray.get(x));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonArray.put(obj);
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        final JSONObject finalJsonObject = new JSONObject();
        try {
            finalJsonObject.put("data", JsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                "https://webhook.site/87bb1bfb-4af8-459e-a526-c819e41d7d66", //Change this url to your API
                finalJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


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
                try {
                    //SOME ACTION IF THE RESPONSE STATUS CODE IS NOT 4xx or 5xx
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Try once again, please...", Toast.LENGTH_LONG).show();
                }
            }

        });


        requestQueue.add(req);
        Toast.makeText(this, "data send!", Toast.LENGTH_SHORT).show();


    }


    public void SensorClosed() {
        Toast.makeText(this, "Sensor closed!", Toast.LENGTH_SHORT).show();

    }
}