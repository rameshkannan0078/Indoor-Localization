
package com.example.acceleratorpro;

        import android.database.Cursor;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;

        import androidx.appcompat.app.AppCompatActivity;
        import com.google.ar.core.Anchor;
        import com.google.ar.core.Pose;
        import com.google.ar.sceneform.AnchorNode;
        import com.google.ar.sceneform.SceneView;
        import com.google.ar.sceneform.math.Vector3;
        import com.google.ar.sceneform.rendering.Color;
        import com.google.ar.sceneform.rendering.MaterialFactory;
        import com.google.ar.sceneform.rendering.ModelRenderable;
        import com.google.ar.sceneform.rendering.ShapeFactory;
        import com.google.ar.sceneform.ux.ArFragment;
        import com.google.ar.sceneform.ux.TransformableNode;

public class ShowArrowDestination extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable arrowRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showarrow_destination);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        // Set the click listener for the show arrows button
        findViewById(R.id.showArrowsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the arrows from the database
                TriangleDatabaseHelper dbHelper = new TriangleDatabaseHelper(ShowArrowDestination.this);
                Cursor cursor = dbHelper.getAllAnchorsAndTriangles();

                // Clean up previous anchors
                arFragment.getArSceneView().getScene();

                // Create anchors at the retrieved positions and add nodes to them
                if (cursor != null && cursor.moveToFirst()) {
                    int xIndex = cursor.getColumnIndex(TriangleDatabaseHelper.COLUMN_X);
                    int yIndex = cursor.getColumnIndex(TriangleDatabaseHelper.COLUMN_Y);
                    int zIndex = cursor.getColumnIndex(TriangleDatabaseHelper.COLUMN_Z);
                    int anchorIdIndex = cursor.getColumnIndex(TriangleDatabaseHelper.ANCHOR_ID);
                    int trianglePointsIndex = cursor.getColumnIndex(TriangleDatabaseHelper.TRIANGLE_POINTS);

                    LayoutInflater inflater = LayoutInflater.from(ShowArrowDestination.this);
                    SceneView arContainer = arFragment.getArSceneView().getScene().getView();

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
        });
    }

    private void createArrowRenderable() {
        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(material -> {
                    arrowRenderable = ShapeFactory.makeCylinder(0.02f, 0.1f, new Vector3(0f, 0.05f, 0f), material);
                });
    }
}
