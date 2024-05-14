package com.example.acceleratorpro;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SensorDataLogger implements SensorEventListener {

    private MainActivity sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor gyroscope;

    private File file;
    private FileWriter fileWriter;

    public SensorDataLogger(MainActivity sensorManager) {
        this.sensorManager = sensorManager;
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Check and request permission if necessary
        if (ContextCompat.checkSelfPermission(sensorManager, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(sensorManager,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // Permission already granted, proceed with file creation
            createFile();
        }
    }

    private void createFile() {
        try {
            // Get the app-specific Downloads directory
            File downloadsDir = sensorManager.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

            if (downloadsDir != null) {
                // Create the file
                file = new File(downloadsDir, "sensor_data.csv");
                fileWriter = new FileWriter(file);
                fileWriter.write("timestamp,ax,ay,az,mx,my,mz,gx,gy,gz\n");
                fileWriter.flush();
            } else {
                Log.e("SensorDataLogger", "Failed to get Downloads directory.");
                showToast("Error creating file");
            }
        } catch (IOException e) {
            Log.e("SensorDataLogger", "Error creating file: " + e.getMessage());
            showToast("Error creating file");
        }
    }

    private void showToast(String message) {
        Toast.makeText(sensorManager, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // ... Rest of your code ...
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ... Rest of your code ...
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        try {
            fileWriter.flush();
        } catch (IOException e) {
            Log.e("SensorDataLogger", "Error closing");
        }
    }
}
