package fi.lauriari.ar_project.fragments

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import fi.lauriari.ar_project.R

// AR fragment to display purchased items
class CollectedItemARFragment : Fragment() {

    private val arguments by navArgs<CollectedItemARFragmentArgs>()
    private lateinit var arFragment: ArFragment
    private var modelRenderable: ModelRenderable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get the 3d model's url through the navigation argument
        val objectUrl = arguments.objectUrl
        val view = inflater.inflate(R.layout.fragment_collected_item_ar, container, false)
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        val addBtn = view.findViewById<Button>(R.id.add_btn)

        addBtn.setOnClickListener { add3dObject(rootView, addBtn) }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        arFragment = childFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        ModelRenderable.builder()
            .setSource(activity, Uri.parse(objectUrl))
            .setIsFilamentGltf(true).setIsFilamentGltf(true).setAsyncLoadEnabled(true)
            .build()
            .thenAccept { modelRenderable = it }
            .exceptionally { null }
        return view
    }

    private fun getScreenCenter(rootView: View): Point =
        Point(rootView.width / 2, rootView.height / 2)

    private fun add3dObject(rootView: View, button: Button) {
        val frame = arFragment.arSceneView.arFrame
        if (frame != null && modelRenderable != null) {
            val point = getScreenCenter(rootView)
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)
                    val mNode = TransformableNode(arFragment.transformationSystem)
                    mNode.setParent(anchorNode)
                    //mNode.setRenderable(modelRenderable).animate(true).start()
                    mNode.renderable = modelRenderable
                    mNode.select()
                    mNode.setOnTapListener { _, _ ->
                        button.visibility = View.INVISIBLE
                        // make an object animate if it has an animation
                        mNode.setRenderable(modelRenderable).animate(true).start()
                    }
                    break
                }
            }
        }
    }
}
