package com.example.secondar

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.secondar.gestures.CustomGestureDetector
import com.example.secondar.gestures.CustomOnGestureListener
import com.example.secondar.gestures.IGesture
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Trackable
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.lang.Long.parseLong
import java.lang.Long.valueOf

import java.util.ArrayList
import java.util.LinkedHashMap

class MainActivity : AppCompatActivity(), IFurniture, IGesture {

    private lateinit var arFragment: ArFragment
    private val anchorsMap = LinkedHashMap<Long, AnchorNode?>()
    private var anchorNode: AnchorNode? = null
    private lateinit var btnRemove: Button

    private val pointer = PointerDrawable()
    private var isTracking: Boolean = false
    private var isHitting: Boolean = false
    private var nodeIndexToDelete: Long = 0
    private var nodeToDelete: Node? = null

    private lateinit var mGestureDetector: CustomGestureDetector
    private lateinit var mViewTouchListener: Node.OnTouchListener
    private lateinit var gestureListener: CustomOnGestureListener

    private lateinit var viewModel: ArViewModel

    private val ASSET_3D = "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment

        gestureListener = CustomOnGestureListener(this)
        mGestureDetector = CustomGestureDetector(this, gestureListener)
        mGestureDetector.setOnDoubleTapListener(gestureListener)

        mViewTouchListener = Node.OnTouchListener { hitTestResult, event ->
            if (mGestureDetector.onTouchEvent(event)) {
                nodeToDelete = hitTestResult.node

                nodeToDelete?.let { node ->
                    node.name.let { nodeIndexToDelete = parseLong(node.name)  }
                }

                return@OnTouchListener true
            }
            false
        }

        arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            arFragment.onUpdate(frameTime)
            onUpdate()
        }

        btnRemove = findViewById(R.id.remove)
        initiateRecyclerView()

        btnRemove.setOnClickListener { view -> removeAnchorNode() }

        viewModel = ViewModelProviders.of(this, viewModelFactory {
            ArViewModel(anchorNode, anchorsMap, arFragment.transformationSystem, mViewTouchListener, gestureListener)
        }).get(ArViewModel::class.java)

        // Observer for the Game finished event
        viewModel.eventGameFinish.observe(this, Observer<Boolean> { hasFinished ->
            System.out.println("_xyz hasFinished = $hasFinished")
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

    override fun onClickType(modelName: String) {
        addObject(modelName)
    }

    private fun addObject(modelName: String) {
        val frame = arFragment.arSceneView.arFrame
        val pt = screenCenter
        val hits: List<HitResult>
        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    loadModel(hit.createAnchor(), modelName)
                    break
                }
            }
        }
    }

    private fun loadModel(anchor: Anchor, modelName: String) {
        /* When you build a Renderable, Sceneform loads model and related resources
         * in the background while returning a CompletableFuture.
         * Call thenAccept(), handle(), or check isDone() before calling get().
         */
        if (modelName.startsWith("https")) {
            ModelRenderable
                    .builder()
                    .setSource(
                            this,
                            RenderableSource
                                    .builder()
                                    .setSource(this, Uri.parse(modelName), RenderableSource.SourceType.GLTF2)
                                    .setScale(0.75f)  // Scale the original model to 50%.
                                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                    .build()
                    )
                    .setRegistryId(modelName)
                    .build()
                    .thenAccept { modelRenderable -> addAnchorToScene(anchor, modelRenderable) }
                    .exceptionally { throwable ->
                        val toast = Toast.makeText(this, "Unable to load renderable $ASSET_3D", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        null
                    }
        } else {
            ModelRenderable.builder()
                    .setSource(this, Uri.parse(modelName))
                    .build()
                    .thenAccept { modelRenderable -> addAnchorToScene(anchor, modelRenderable) }
        }
    }

    private fun addAnchorToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val key = System.currentTimeMillis()

        anchorNode = AnchorNode(anchor)
        anchorsMap[key] = anchorNode

        val node = TransformableNode(arFragment.transformationSystem)
        node.setParent(anchorNode)
        node.name = "" + key
        node.renderable = modelRenderable
        node.select()
        node.setOnTouchListener(mViewTouchListener)

        gestureListener.nodeList[key] = gestureListener.nodeList.size
        arFragment.arSceneView.scene.addChild(anchorNode)
    }

    private fun removeAnchorNode() {
        nodeIndexToDelete = 0L
        val iter = anchorsMap.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next()
            var node: Node? = entry.value!!.children[0]
            if (node != null) {
                arFragment.arSceneView.scene.removeChild(node)
                entry.value?.anchor!!.detach()
                entry.value?.setParent(null)
                iter.remove()
                node = null
            }
            gestureListener.nodeList.remove(nodeIndexToDelete)
            nodeIndexToDelete++
        }
        nodeIndexToDelete = -1L
    }

    override fun onLongPressItem() {
        viewModel.onGameFinishComplete()
        nodeToDelete?.let { childNode ->
            arFragment.arSceneView.scene.removeChild(childNode)
            anchorsMap[valueOf(childNode.name)]?.let { anchorNode ->
                anchorNode.removeChild(childNode)
                anchorNode.anchor?.detach()
                anchorNode.setParent(null)
            }
            anchorsMap.remove(valueOf(childNode.name))
            nodeToDelete = null
            gestureListener.nodeList.remove(nodeIndexToDelete)
            nodeIndexToDelete = -1L
        }
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