package com.example.secondar;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondar.gestures.CustomGestureDetector;
import com.example.secondar.gestures.CustomOnGestureListener;
import com.example.secondar.gestures.IGesture;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements IFurniture, IGesture {

    private ArFragment arFragment;
    private ArrayList<Integer> imagesPath = new ArrayList<Integer>();
    private ArrayList<String> namesPath = new ArrayList<>();
    private ArrayList<String> modelNames = new ArrayList<>();
    private LinkedHashMap<Long, AnchorNode> anchorsList = new LinkedHashMap<>();
    private AnchorNode anchorNode;
    private Button btnRemove;

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;
    private Long nodeIndexToDelete;
    private Node nodeToDelete;

    private CustomGestureDetector mGestureDetector;
    private Node.OnTouchListener mViewTouchListener;
    private CustomOnGestureListener gestureListener;

    private String ASSET_3D = "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);

        gestureListener = new CustomOnGestureListener(this);
        mGestureDetector = new CustomGestureDetector(this, gestureListener);
        mGestureDetector.setOnDoubleTapListener(gestureListener);

        mViewTouchListener = (v, event) -> {
            if (mGestureDetector.onTouchEvent(event)) {
                nodeToDelete = v.getNode();
                nodeIndexToDelete = Long.parseLong(v.getNode().getName());
                System.out.println("_xyz node to delete: " + v.getNode().getName());
                return true;
            }
            return false;
        };

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            arFragment.onUpdate(frameTime);
            onUpdate();
        });

        btnRemove = findViewById(R.id.remove);
        getImages();

        btnRemove.setOnClickListener(view -> removeAnchorNode(anchorNode));
    }

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if ((trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose()))) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private void getImages() {
        imagesPath.add(R.drawable.table);
        imagesPath.add(R.drawable.bookshelf);
        imagesPath.add(R.drawable.lamp);
        imagesPath.add(R.drawable.odltv);
        imagesPath.add(R.drawable.clothdryer);
        imagesPath.add(R.drawable.chair);

        namesPath.add("Table");
        namesPath.add("BookShelf");
        namesPath.add("Lamp");
        namesPath.add("Old Tv");
        namesPath.add("Cloth Dryer");
        namesPath.add("Chair");

        modelNames.add("table.sfb");
        modelNames.add("model.sfb");
        modelNames.add("lamp.sfb");
        modelNames.add("tv.sfb");
        modelNames.add("cloth.sfb");
        modelNames.add(ASSET_3D);

        initiateRecyclerview();
    }

    private void initiateRecyclerview() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(namesPath, imagesPath, modelNames, this);
        recyclerView.setAdapter(adapter);
    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        Long key = System.currentTimeMillis();

        anchorNode = new AnchorNode(anchor);
        anchorsList.put(key, anchorNode);

        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setName("" + key);
        node.setRenderable(modelRenderable);
        node.select();
        node.setOnTouchListener(mViewTouchListener);

        gestureListener.nodeList.put(key, gestureListener.nodeList.size());
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    public void removeAnchorNode(AnchorNode nodeToremove) {
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            nodeToremove.getAnchor().detach();
            nodeToremove.setParent(null);
            nodeToremove = null;
        }
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    @Override
    public void onClickType(int position) {
        addObject(modelNames.get(position));
    }

    private void addObject(String model) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    loadModel(hit.createAnchor(), model);
                    break;
                }
            }
        }
    }

    private void loadModel(Anchor anchor, String model) {
        /* When you build a Renderable, Sceneform loads model and related resources
         * in the background while returning a CompletableFuture.
         * Call thenAccept(), handle(), or check isDone() before calling get().
         */
        if (model.startsWith("https")) {
            ModelRenderable
                    .builder()
                    .setSource(
                    this,
                            RenderableSource
                                    .builder()
                                    .setSource(this, Uri.parse(model), RenderableSource.SourceType.GLTF2)
                                    .setScale(0.75f)  // Scale the original model to 50%.
                                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                    .build()
                    )
                    .setRegistryId(model)
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                    .exceptionally(
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(this, "Unable to load renderable " +
                                                ASSET_3D, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            }
                    );
        } else {
            ModelRenderable.builder()
                    .setSource(this, Uri.parse(model))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable));
        }
    }

    @Override
    public void onLongPressItem() {
        if (nodeToDelete != null) {
            System.out.println("_xyz Anchor = " + anchorNode.getName());
            System.out.println("_xyz nodeToDelete = " + nodeToDelete.getName());
            System.out.println("_xyz Long press : Delete = " + nodeIndexToDelete + ", " + gestureListener.nodeList);
            arFragment.getArSceneView().getScene().removeChild(nodeToDelete);
            anchorsList.get(Long.parseLong(nodeToDelete.getName())).removeChild(nodeToDelete);
            nodeToDelete = null;
            gestureListener.nodeList.remove(nodeIndexToDelete);
            nodeIndexToDelete = -1L;
            System.out.println("_xyz Long press : Pending Nodes to Delete = " + nodeIndexToDelete + ", " + gestureListener.nodeList);
        }
    }
}