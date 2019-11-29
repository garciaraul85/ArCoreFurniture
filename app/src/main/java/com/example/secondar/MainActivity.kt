package com.example.secondar

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secondar.feature.takepictures.TakePicturesViewModel
import com.example.secondar.gestures.CustomGestureDetector
import com.example.secondar.gestures.CustomOnGestureListener
import com.example.secondar.gestures.IGesture
import com.example.secondar.models.Product
import com.example.secondar.repository.Common
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuObject
import com.yalantis.contextmenu.lib.MenuParams
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity(), IFurniture, IGesture {
    private lateinit var arFragment: ArFragment
    private lateinit var btnRemove: Button
    private lateinit var btnTakePicture: FloatingActionButton

    private val pointer = PointerDrawable()
    private var isTracking: Boolean = false
    private var isHitting: Boolean = false

    private lateinit var mGestureDetector: CustomGestureDetector
    private lateinit var gestureListener: CustomOnGestureListener
    private lateinit var arViewModel: ArViewModel
    private lateinit var takePicturesViewModel: TakePicturesViewModel
    private lateinit var scene: Scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        initMenuFragment()

        viewModelAndArCoreSetup()
        arCorListenersSetup()
        btnSetup()
        modelsAnchorsAndNodesObserver()
        takePicturesObserver()
    }

    private fun viewModelAndArCoreSetup() {
        arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
        scene = arFragment.arSceneView.scene

        arViewModel = ViewModelProviders.of(this, viewModelFactory {
            ArViewModel(arFragment.transformationSystem, scene)
        }).get(ArViewModel::class.java)

        takePicturesViewModel = ViewModelProviders.of(this, viewModelFactory {
            TakePicturesViewModel(application)
        }).get(TakePicturesViewModel::class.java)
    }

    private fun arCorListenersSetup() {
        gestureListener = CustomOnGestureListener(this)
        mGestureDetector = CustomGestureDetector(this, gestureListener)
        mGestureDetector.setOnDoubleTapListener(gestureListener)

        arViewModel.listenForModelUserInteraction(mGestureDetector)

        scene.addOnUpdateListener { frameTime ->
            arFragment.onUpdate(frameTime)
            onUpdate()
        }
    }

    private fun btnSetup() {
        btnRemove = findViewById(R.id.remove)
        btnTakePicture = findViewById(R.id.takePicture)
        initiateRecyclerView()

        btnRemove.setOnClickListener { view -> arViewModel.removeAllModels() }
        btnTakePicture.setOnClickListener { view -> takePicture() }
    }

    private fun takePicturesObserver() {
        takePicturesViewModel.startActivityLiveData.observe(this, Observer<Intent> { intent ->
            startActivity(intent)
        })
    }

    private fun modelsAnchorsAndNodesObserver() {
        arViewModel.setNodeNameLiveData.observe(this, Observer<Long> { key ->
            gestureListener.nodeList[key] = gestureListener.nodeList.size
        })
        arViewModel.anchorNodeIntoSceneLiveData.observe(this, Observer<AnchorNode> { anchorNode ->
            scene.addChild(anchorNode)
        })
        arViewModel.removeNodeNameLiveData.observe(this, Observer<Long> { nodeIndexToDelete ->
            gestureListener.nodeList.remove(nodeIndexToDelete)
        })
        arViewModel.loadModelLiveData.observe(this, Observer<Product> { product ->
            System.out.println("_xyz loadModelLiveData.observe")
            if (product.modelsName.startsWith("https")) {
                ModelRenderable
                        .builder()
                        .setSource(
                                this,
                                RenderableSource
                                        .builder()
                                        .setSource(this, Uri.parse(product.modelsName), RenderableSource.SourceType.GLTF2)
                                        .setScale(0.75f)  // Scale the original model to 50%.
                                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                        .build()
                        )
                        .setRegistryId(product.modelsName)
                        .build()
                        .thenAccept { modelRenderable ->
                            arViewModel.addAnchorToScene(product.anchor, modelRenderable)
                        }
                        .exceptionally { throwable ->
                            null
                        }
            } else {
                ModelRenderable.builder()
                        .setSource(this, Uri.parse(product.modelsName))
                        .setRegistryId(product.modelsName)
                        .build()
                        .thenAccept { modelRenderable -> arViewModel.addAnchorToScene(product.anchor, modelRenderable) }
                        .exceptionally { throwable ->
                            null
                        }
            }
        })
    }

    private fun onUpdate() {
        val trackingChanged = updateTracking()
        val contentView = findViewById<View>(android.R.id.content)
        if (trackingChanged) {
            if (isTracking) {
                contentView.overlay.add(pointer)
            } else {
                contentView.overlay.remove(pointer)
            }
            contentView.invalidate()
        }

        if (isTracking) {
            val hitTestChanged = updateHitTest()
            if (hitTestChanged) {
                pointer.isEnabled = isHitting
                contentView.invalidate()
            }
        }
    }

    private fun updateTracking(): Boolean {
        val frame = arFragment.arSceneView.arFrame
        val wasTracking = isTracking
        isTracking = frame?.camera?.trackingState == TrackingState.TRACKING
        return isTracking != wasTracking
    }

    private fun updateHitTest(): Boolean {
        val frame = arFragment.arSceneView.arFrame
        val pt = screenCenter
        val hits: List<HitResult>
        val wasHitting = isHitting
        isHitting = false
        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    isHitting = true
                    break
                }
            }
        }
        return wasHitting != isHitting
    }

    private fun initiateRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = layoutManager
        val adapter = RecyclerViewAdapter(Common.getProductsList(), this)
        recyclerView.adapter = adapter
    }

    fun takePicture() {
        val snackbar = Snackbar.make(findViewById(android.R.id.content),
                "Photo saved", Snackbar.LENGTH_LONG)
        takePicturesViewModel.takePhoto(arFragment.arSceneView, snackbar, getPackageName())
    }

    override fun onModelItemClick(modelName: String) {
        val frame = arFragment.arSceneView.arFrame
        frame?.let { arViewModel.addObject(modelName, it, screenCenter) }
    }

    override fun onLongPressItem() {
        arViewModel.removeModel()
    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
            }

    private val screenCenter: android.graphics.Point
        get() {
            val vw = findViewById<View>(android.R.id.content)
            return android.graphics.Point(vw.width / 2, vw.height / 2)
        }
}