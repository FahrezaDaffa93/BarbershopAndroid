package com.project.barbershop.registrasiSatu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.barbershop.R;
import com.project.barbershop.registrasiSatu.activity.registrasi;

public class MainActivitySatu extends AppCompatActivity {
@Override
    protected  void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    final Handler handler = new Handler();

    handler.postDelayed(new Runnable() {
        @Override
        public void run(){
            startActivity(new Intent(getApplicationContext(), registrasi.class));
            finish();
            Toast.makeText(getApplicationContext(), "Halo", Toast.LENGTH_SHORT).show();

        }
    }, 5001);

    }
}