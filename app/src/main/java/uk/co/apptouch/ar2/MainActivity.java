package uk.co.apptouch.ar2;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    //BELOW ADDED FOR THIS AR CORE TEST
    private ArFragment fragment;
    private Uri selectedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BELOW ADDED FOR THIS AR CORE TEST
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        InitializedGallery();

        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if(plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING){
                        return;
                    }
                    Anchor anchor = hitResult.createAnchor();
                    placeObject(fragment, anchor, selectedObject);

                }
        );
    }

    private void InitializedGallery(){

        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView chair = new ImageView(this);
        chair.setImageResource(R.drawable.chair_thumb);
        chair.setContentDescription("chair");
        chair.setOnClickListener(view -> {selectedObject = Uri.parse("chair.sfb");});
        gallery.addView(chair);

        ImageView lamp = new ImageView(this);
        lamp.setImageResource(R.drawable.lamp_thumb);
        lamp.setContentDescription("lamp");
        lamp.setOnClickListener(view -> {selectedObject = Uri.parse("Lamp.sfb");});
        gallery.addView(lamp);
    }

    private void placeObject(ArFragment fragment, Anchor anchor, Uri model){
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));
    }


    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable){

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }
}
