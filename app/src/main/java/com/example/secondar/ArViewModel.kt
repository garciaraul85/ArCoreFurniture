package com.example.secondar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.secondar.gestures.CustomOnGestureListener
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import java.util.LinkedHashMap

class ArViewModel(var anchorNode: AnchorNode?, val anchorsMap: LinkedHashMap<Long, AnchorNode?>,
                  val transformationSystem: TransformationSystem, val mViewTouchListener: Node.OnTouchListener,
                  var gestureListener: CustomOnGestureListener): ViewModel() {

    private val anchorNodeMutableLiveData = MutableLiveData<AnchorNode>()
    val anchorNodeLiveData: LiveData<AnchorNode>
        get() = anchorNodeMutableLiveData

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private fun addAnchorToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val key = System.currentTimeMillis()

        var anchorNode = AnchorNode(anchor)
        anchorsMap[key] = anchorNode

        val node = TransformableNode(transformationSystem)
        node.setParent(anchorNode)
        node.name = "" + key
        node.renderable = modelRenderable
        node.select()
        node.setOnTouchListener(mViewTouchListener)

        gestureListener.nodeList[key] = gestureListener.nodeList.size
        anchorNodeMutableLiveData.value = anchorNode
    }

    fun onGameFinish() {
        _eventGameFinish.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

}