package com.example.acceleratorpro;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.FirebaseApp;
import com.opencsv.CSVWriter;

import java.util.List;

import androidx.appcompat.widget.Toolbar;



public class MainActivity extends FragmentActivity {

    private ArFragment arFragment;
    private Button addButton;
    private ModelRenderable arrowRenderable;
    private Button bugdroidButton;
    private ModelRenderable bugdroidRenderable;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor gyroscope;
    private SensorDataLogger sensorDataLogger;



    private CSVWriter csvWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("AcceleratorPro");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));



        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArrow();
            }
        });

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(material -> {
                    arrowRenderable = ShapeFactory.makeCylinder(0.02f, 0.1f, new Vector3(0f, 0.05f, 0f), material);
                });

        bugdroidButton = findViewById(R.id.bugdroidButton);
        bugdroidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTriangle();
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the SensorDataLogger
        sensorDataLogger.stop();
    }


    private void addTriangle() {
        Session session = arFragment.getArSceneView().getSession();
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            List<HitResult> hitResults = frame.hitTest(arFragment.getArSceneView().getWidth() / 2f, arFragment.getArSceneView().getHeight() / 2f);
            if (!hitResults.isEmpty()) {
                HitResult hitResult = hitResults.get(0);
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.BLUE))
                        .thenAccept(material -> {
                            Node node1 = new Node();
                            node1.setParent(anchorNode);
                            node1.setRenderable(ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.05f), new Vector3(0f, 0.25f, -0.25f), material));

                            Node node2 = new Node();
                            node2.setParent(anchorNode);
                            node2.setRenderable(ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.05f), new Vector3(-0.25f, 0.25f, 0f), material));

                            Node node3 = new Node();
                            node3.setParent(anchorNode);
                            node3.setRenderable(ShapeFactory.makeCube(new Vector3(0.5f, 0.5f, 0.05f), new Vector3(0.25f, 0.25f, 0f), material));
                        });



            }
        }


    }




    private void addArrow() {
        Session session = arFragment.getArSceneView().getSession();
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            List<HitResult> hitResults = frame.hitTest(arFragment.getArSceneView().getWidth() / 2f, arFragment.getArSceneView().getHeight() / 2f);
            if (!hitResults.isEmpty()) {
                HitResult hitResult = hitResults.get(0);
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                Pose pose = anchor.getPose();
                Vector3 direction = new Vector3(pose.tx(), pose.ty(), pose.tz());
                direction = direction.normalized();
                Quaternion rotation = Quaternion.lookRotation(direction, Vector3.up());

                AnchorNode arrowNode = new AnchorNode(anchor);
                arrowNode.setRenderable(arrowRenderable);
                arrowNode.setLocalRotation(rotation);
                arrowNode.setParent(anchorNode);

                TriangleDatabaseHelper dbHelper = new TriangleDatabaseHelper(this);
                float[] hitPoint = hitResult.getHitPose().getTranslation();


                float[] trianglePoints = {hitPoint[0], hitPoint[1], hitPoint[2], 0, 0, 0, 0, 0, 0}; // placeholder values for now
                dbHelper.insertTriangle(hitPoint, Vector3.zero(), Vector3.zero(), anchor, trianglePoints);


                sensorDataLogger = new SensorDataLogger(this);


                // Get a reference to the SensorManager
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                sensorManager.registerListener(sensorDataLogger, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(sensorDataLogger, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(sensorDataLogger, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);



            }
        }
    }

    public void onShowArrowsButtonClick(View view) {
// Retrieve the arrows from the database
        TriangleDatabaseHelper dbHelper = new TriangleDatabaseHelper(this);
        Cursor cursor = dbHelper.getAllAnchorsAndTriangles();
// Clean up previous anchors
// Create anchors at the retrieved positions and add nodes to them
        if (cursor != null && cursor.moveToFirst()) {
            int xIndex = cursor.getColumnIndex(TriangleDatabaseHelper.COLUMN_X);
            int yIndex = cursor.getColumnIndex(TriangleDatabaseHelper.COLUMN_Y);
            int zIndex = cursor.getColumnIndex(TriangleDatabaseHelper.COLUMN_Z);
            int anchorIdIndex = cursor.getColumnIndex(TriangleDatabaseHelper.ANCHOR_ID);
            int trianglePointsIndex = cursor.getColumnIndex(TriangleDatabaseHelper.TRIANGLE_POINTS);

            do {
                float x = cursor.getFloat(xIndex);
                float y = cursor.getFloat(yIndex);
                float z = cursor.getFloat(zIndex);
                Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(new Pose(new float[]{x, y, z}, new float[]{0, 0, 0, 1}));
                AnchorNode anchorNode = new AnchorNode(anchor);
                arFragment.getArSceneView().getScene().addChild(anchorNode);
                if (arrowRenderable == null) {
                    createArrowRenderable();
                }
                TransformableNode arrowNode = new TransformableNode(arFragment.getTransformationSystem());
                arrowNode.setParent(anchorNode);
                arrowNode.setRenderable(arrowRenderable);
                arrowNode.select();

                // Retrieve anchor ID and triangle points from the cursor
                int anchorId = cursor.getInt(anchorIdIndex);
                String trianglePoints = cursor.getString(trianglePointsIndex);

                // Use anchor ID and triangle points to create triangles or perform other operations
                // ...
            } while (cursor.moveToNext());

        } else {
// No arrows found
        }
// Clean up resources
        cursor.close();
        dbHelper.close();
    }


    public Sensor getDefaultSensor(int type) {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(type);
    }

    public void registerListener(SensorEventListener listener, Sensor sensor, int sensorDelay) {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(listener, sensor, sensorDelay);
    }

    private void createArrowRenderable() {
        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(material -> {
                    arrowRenderable = ShapeFactory.makeCylinder(0.02f, 0.1f, new Vector3(0f, 0.05f, 0f), material);
                });
    }





    public void unregisterListener(SensorEventListener listener) {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(listener);
    }


}

