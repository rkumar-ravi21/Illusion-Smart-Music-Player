package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

button=findViewById(R.id.press);
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.e("hello","hello");

            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            Log.e("11","1");
               startActivity(intent);

           }
       });


    }
}
