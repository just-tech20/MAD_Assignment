package com.example.writedatatosd;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button write = (Button) findViewById(R.id.savadata);

        write.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                if (Environment.isExternalStorageManager()) {
//                    Toast.makeText(getBaseContext(), "Have all Required Permission", Toast.LENGTH_LONG).show();
                    mainfunction();
                } else {
                    Toast.makeText(getBaseContext(), "Please Allow Required Permission", Toast.LENGTH_LONG).show();
                    int requestCode = 1;
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, requestCode);
//                    ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, requestCode);

//                    This is new Way to get Permission Access
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, requestCode);
                }
            }
        });
    }

    private void mainfunction() {
        EditText e1 = (EditText) findViewById(R.id.writedata);
        String message = e1.getText().toString();
        try {
                    File f=new File("/sdcard/myfile.txt");
//                    For Testing path taken
//            File f = new File("/storage/self/primary/WhatsApp/myfile.txt");
            f.createNewFile();
            FileOutputStream fout = new FileOutputStream(f);
            fout.write(message.getBytes());
            fout.close();
            Toast.makeText(getBaseContext(), "Data Written in SD CARD", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
