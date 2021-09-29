package fi.lauriari.ar_project.Fragments


import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import fi.lauriari.ar_project.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

val SERVER_IMG_BASE_URL = URL("https://users.metropolia.fi/~lauriari/AR_project/")

class GameARFragment : Fragment() {

    private lateinit var arFrag: ArFragment
    private var quizQuestionRenderable: ViewRenderable? = null
    private var imageSelectionQuizTvRenderable: ViewRenderable? = null
    private var imageRenderable: ViewRenderable? = null
    private var imageRenderable2: ViewRenderable? = null
    private var imageRenderable3: ViewRenderable? = null
    private var flagQuestionRenderable: ViewRenderable? = null
    private var sphereRenderable: ModelRenderable? = null
    private val args by navArgs<GameARFragmentArgs>()
    private val mMapDetailsViewModel: MapDetailsViewModel by viewModels()
    private val mInventoryViewModel: InventoryViewModel by viewModels()
    private var latestMapDetails: MapDetails? = null
    private var selectedMapLatLngPoint: MapLatLng? = null
    private var inventory: Inventory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_a_r, container, false)
        Log.d("argstest", "Id of this latlng instance: ${args.id}")
        latestMapDetails = mMapDetailsViewModel.getLatestMapDetails()
        Log.d("argstest", latestMapDetails.toString())
        selectedMapLatLngPoint = mMapDetailsViewModel.getMapLatPointLngById(args.id)
        Log.d("argstest", selectedMapLatLngPoint.toString())
        inventory = mInventoryViewModel.getInventory()

        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        buildRenderables()

        //TODO: Obtain the lists of questions from elsewhere, change the structure to be something more meaningful
        val questions = mutableListOf<QuizQuestion>(
            QuizQuestion(
                "How many legs does a spider have?",
                "5",
                "8",
                "10", "8"
            ),
            QuizQuestion(
                "What is the color of an emerald?",
                "Red",
                "Blue",
                "Green", "Green"
            ),
            QuizQuestion(
                "What is the name of the fairy in Peter Pan?",
                "Tinkerbell",
                "Harry Potter",
                "Cinderella", "Tinkerbell"
            ), QuizQuestion(
                "If you freeze water, what do you get?",
                "Steam",
                "Coca-Cola",
                "Ice", "Ice"
            )
        )

        val flagQuestions =
            mutableListOf<FlagQuestion>(
                FlagQuestion(
                    "american-flag.png",
                    "USA",
                    "Canada",
                    "Sweden",
                    "USA"
                ),
                FlagQuestion(
                    "japan-flag.png",
                    "Japan",
                    "South-Korea",
                    "United Kingdom",
                    "Japan"
                ),
                FlagQuestion(
                    "finland-flag.png",
                    "Sweden",
                    "Norway",
                    "Finland",
                    "Finland"
                ),
            )
        val imageSelectionQuestions = mutableListOf<ImageSelectionQuestion>(
            ImageSelectionQuestion(
                "Find the flag of Finland",
                "japan-flag.png",
                "american-flag.png",
                "finland-flag.png",
                "finland-flag.png"
            ),
            ImageSelectionQuestion(
                "Find the flag of USA",
                "japan-flag.png",
                "american-flag.png",
                "finland-flag.png",
                "american-flag.png"
            )
        )

        view.findViewById<Button>(R.id.add_flag_question_btn).setOnClickListener {
            flagQuestionRenderable ?: return@setOnClickListener

            val node: TransformableNode =
                createLocationAnchorForViewRenderable(flagQuestionRenderable!!)
                    ?: return@setOnClickListener

            // Randomize the questions
            flagQuestions.shuffle()
            val chosenQuestion = flagQuestions.take(1)

            val flagIv = flagQuestionRenderable!!.view.findViewById<ImageView>(R.id.flag_iv)
            val button1 = flagQuestionRenderable!!.view.findViewById<Button>(R.id.answer1_btn)
            val button2 = flagQuestionRenderable!!.view.findViewById<Button>(R.id.answer2_btn)
            val button3 = flagQuestionRenderable!!.view.findViewById<Button>(R.id.answer3_btn)

            val answers = mutableListOf(
                chosenQuestion[0].answer1,
                chosenQuestion[0].answer2,
                chosenQuestion[0].answer3
            )

            // Randomize the answer locations
            answers.shuffle()

            // Create a drawable from an inputstream opened from an asset to display on screen
            //val stream: InputStream = activity?.assets!!.open(chosenQuestion[0].flagSource)
            //val drawable = Drawable.createFromStream(stream, null)
            //flagIv.setImageDrawable(drawable)

            val image = getWebImage(chosenQuestion[0].flagSource)

            flagIv.setImageBitmap(image)
            button1.text = answers[0]
            button2.text = answers[1]
            button3.text = answers[2]

            button1.setOnClickListener {
                if (button1.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wrong answer! No reward for you!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            button2.setOnClickListener {
                if (button2.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wrong answer! No reward for you!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            button3.setOnClickListener {
                if (button3.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wrong answer! No reward for you!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        view.findViewById<Button>(R.id.add_quiz_question_btn).setOnClickListener {
            quizQuestionRenderable ?: return@setOnClickListener

            val node: TransformableNode =
                createLocationAnchorForViewRenderable(quizQuestionRenderable!!)
                    ?: return@setOnClickListener

            // Randomize the questions and take the first one to be asked from the user
            questions.shuffle()
            val chosenQuestion = questions.take(1)

            val questionTv = quizQuestionRenderable!!.view.findViewById<TextView>(R.id.quiz_tv)
            val button1 = quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer1_btn)
            val button2 = quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer2_btn)
            val button3 = quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer3_btn)

            val answers = mutableListOf(
                chosenQuestion[0].answer1,
                chosenQuestion[0].answer2,
                chosenQuestion[0].answer3
            )

            // Randomize the answer locations
            answers.shuffle()

            questionTv.text = chosenQuestion[0].question
            button1.text = answers[0]
            button2.text = answers[1]
            button3.text = answers[2]

            button1.setOnClickListener {
                if (button1.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wrong answer! No reward for you!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            button2.setOnClickListener {
                if (button2.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wrong answer! No reward for you!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            button3.setOnClickListener {
                if (button3.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Wrong answer! No reward for you!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }

        view.findViewById<Button>(R.id.add_sphere_btn).setOnClickListener {
            sphereRenderable ?: return@setOnClickListener

            var collectedSpheres = 0

            // Generate 3 spheres around the user
            val list = mutableListOf(0, 1, 2, 3, 4)
            for (i in 1..3) {
                val randomDirection = list.random()
                list.remove(randomDirection)
                val randomDepth = (2..5).random().toFloat()
                Log.d("random", randomDepth.toString())
                val node =
                    createRandomLocationAnchorForModelRenderable(
                        sphereRenderable!!,
                        randomDirection,
                        randomDepth
                    )
                        ?: return@setOnClickListener
                node.setOnTapListener { hitTestResult, motionEvent ->
                    node.setParent(null)
                    node.renderable = null
                    collectedSpheres++
                    Toast.makeText(
                        requireContext(),
                        "Spheres collected $collectedSpheres/3",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    if (collectedSpheres == 3) {
                        Toast.makeText(
                            requireContext(),
                            "3 COLLECTED!!!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        displayAnswerResult(node)
                    }
                }
            }

        }

        view.findViewById<Button>(R.id.add_image_btn).setOnClickListener {
            imageSelectionQuizTvRenderable ?: return@setOnClickListener
            imageRenderable ?: return@setOnClickListener
            imageRenderable2 ?: return@setOnClickListener
            imageRenderable3 ?: return@setOnClickListener

            imageSelectionQuestions.shuffle()
            val chosen = imageSelectionQuestions.take(1)
            val chosenQuestion = chosen[0]
            Log.d("chosenquestion", chosenQuestion.question)
            val imageUrlEndigs = mutableListOf<String>(
                chosenQuestion.image1,
                chosenQuestion.image2,
                chosenQuestion.image3
            )

            val tvNode: TransformableNode =
                createLocationAnchorForViewRenderable(imageSelectionQuizTvRenderable!!)
                    ?: return@setOnClickListener

            imageSelectionQuizTvRenderable!!.view.findViewById<TextView>(R.id.image_selection_question_tv).text =
                chosenQuestion.question

            val imageRenderablesList =
                listOf<ViewRenderable>(imageRenderable!!, imageRenderable2!!, imageRenderable3!!)
            val list = mutableListOf(0, 1, 2, 3, 4, 5)
            imageRenderablesList.forEachIndexed { i, viewRenderable ->
                val randomDirection = list.random()
                list.remove(randomDirection)
                val randomDepth = (2..5).random().toFloat()
                val node: TransformableNode =
                    createRandomLocationAnchorForViewRenderable(
                        viewRenderable,
                        randomDirection,
                        randomDepth
                    )
                        ?: return@setOnClickListener

                node.name = imageUrlEndigs[i]

                node.setOnTapListener { hitTestResult, motionEvent ->
                    Log.d(
                        "chosenquestion",
                        "Tapped node: ${node.name}"
                    )
                    if (node.name == chosenQuestion.correctAnswer) {
                        displayAnswerResult(node)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "You pressed wrong item!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }


                val randomedImage: Bitmap = getWebImage(imageUrlEndigs[i])
                viewRenderable.view.findViewById<ImageView>(R.id.image)
                    .setImageBitmap(randomedImage)
            }
        }

        return view
    }

    /**
     * After clicking the button, display whether the user answered correct or wrong
     */
    private fun displayAnswerResult(questionNode: TransformableNode) {
        Toast.makeText(requireContext(), "Correct answer! Collect your reward!", Toast.LENGTH_SHORT)
            .show()
        questionNode.setParent(null)
        questionNode.renderable = null

        val updatedMapLatLng = MapLatLng(
            selectedMapLatLngPoint!!.id,
            selectedMapLatLngPoint!!.mapDetailsId,
            selectedMapLatLngPoint!!.lat,
            selectedMapLatLngPoint!!.lng,
            selectedMapLatLngPoint!!.address,
            selectedMapLatLngPoint!!.reward,
            false
        )
        mMapDetailsViewModel.updateMapLatLng(updatedMapLatLng)

        when (selectedMapLatLngPoint!!.reward) {
            "Emerald" -> {
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds + 1,
                        latestMapDetails!!.collectedRubies,
                        latestMapDetails!!.collectedSapphires,
                        latestMapDetails!!.collectedTopazes,
                        latestMapDetails!!.collectedDiamonds
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds + 1,
                        inventory!!.rubies,
                        inventory!!.sapphires,
                        inventory!!.topazes,
                        inventory!!.diamonds
                    )
                )
            }
            "Ruby" -> {
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds,
                        latestMapDetails!!.collectedRubies + 1,
                        latestMapDetails!!.collectedSapphires,
                        latestMapDetails!!.collectedTopazes,
                        latestMapDetails!!.collectedDiamonds
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds,
                        inventory!!.rubies + 1,
                        inventory!!.sapphires,
                        inventory!!.topazes,
                        inventory!!.diamonds
                    )
                )
            }
            "Sapphire" -> {
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds,
                        latestMapDetails!!.collectedRubies,
                        latestMapDetails!!.collectedSapphires + 1,
                        latestMapDetails!!.collectedTopazes,
                        latestMapDetails!!.collectedDiamonds
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds,
                        inventory!!.rubies,
                        inventory!!.sapphires + 1,
                        inventory!!.topazes,
                        inventory!!.diamonds
                    )
                )
            }
            "Topaz" -> {
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds,
                        latestMapDetails!!.collectedRubies,
                        latestMapDetails!!.collectedSapphires,
                        latestMapDetails!!.collectedTopazes + 1,
                        latestMapDetails!!.collectedDiamonds
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds,
                        inventory!!.rubies,
                        inventory!!.sapphires,
                        inventory!!.topazes + 1,
                        inventory!!.diamonds
                    )
                )
            }
        }

        val gem = selectedMapLatLngPoint!!.reward
        val message =
            if (gem != "Emerald") "You were rewarded a" else "You were rewarded an"

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("OK") { _, _ ->
            findNavController().navigate(R.id.action_gameARFragment_to_gameMapFragment)
        }
        builder.setTitle("Task completed")
        builder.setMessage("$message $gem!")
        builder.create().show()


        /*
        diamondRenderable?.let { createLocationAnchorForViewRenderable(it) }
            ?.setOnTapListener { hitTestResult, motionEvent ->
                hitTestResult.node?.setParent(null)
                hitTestResult.node?.renderable = null
            }
         */
    }

    /**
     * Build the ViewRenderables needed for the fragment
     */
    private fun buildRenderables() {
        ViewRenderable.builder()
            .setView(requireContext(), R.layout.flag_question_layout)
            .build()
            .thenAccept { flagQuestionRenderable = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.quiz_question_layout)
            .build()
            .thenAccept { quizQuestionRenderable = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.game_ar_image_tv_layout)
            .build()
            .thenAccept { imageSelectionQuizTvRenderable = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.game_ar_image_layout)
            .build()
            .thenAccept { imageRenderable = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.game_ar_image_layout2)
            .build()
            .thenAccept { imageRenderable2 = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.game_ar_image_layout3)
            .build()
            .thenAccept { imageRenderable3 = it }

        MaterialFactory.makeOpaqueWithColor(requireContext(), Color(255f, 0f, 0f))
            .thenAccept { material: Material? ->
                sphereRenderable =
                    ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), material)
            }
    }

    /**
     * Create a random location for an anchor, this is where the selected item (question or reward) will be placed
     */
    private fun createLocationAnchorForViewRenderable(viewRenderable: ViewRenderable): TransformableNode? {
        // Find a position in front of the user.
        val cameraPos: Vector3 = arFrag.arSceneView.scene.camera.worldPosition
        val cameraForward: Vector3 = arFrag.arSceneView.scene.camera.forward
        val position = Vector3.add(cameraPos, cameraForward.scaled(3f))

        // Create an ARCore Anchor at the position.
        val pose = Pose.makeTranslation(position.x, position.y, position.z)

        // Check if there is an active tracking session active
        val frame = arFrag.arSceneView.session?.update()
        if (frame?.camera?.trackingState.toString() == "PAUSED") {
            return null
        }

        val anchor: Anchor = arFrag.arSceneView.session!!.createAnchor(pose)


        // Create the Sceneform AnchorNode
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFrag.arSceneView.scene)

        // Create the node relative to the AnchorNode
        val node = TransformableNode(arFrag.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = viewRenderable

        return node
    }

    /**
     * Creates an anchor at a random location for ModelRenderable
     */
    private fun createRandomLocationAnchorForModelRenderable(
        modelRenderable: ModelRenderable,
        randomDirection: Int,
        randomDepth: Float
    ): TransformableNode? {
        // Random the spehere location

        var cameraDirection: Vector3 = arFrag.arSceneView.scene.camera.forward

        when (randomDirection) {
            0 -> cameraDirection = arFrag.arSceneView.scene.camera.down
            1 -> cameraDirection = arFrag.arSceneView.scene.camera.forward
            2 -> cameraDirection = arFrag.arSceneView.scene.camera.back
            3 -> cameraDirection = arFrag.arSceneView.scene.camera.left
            4 -> cameraDirection = arFrag.arSceneView.scene.camera.right
            5 -> cameraDirection = arFrag.arSceneView.scene.camera.up
        }

        val cameraPos: Vector3 = arFrag.arSceneView.scene.camera.worldPosition
        //val cameraForward: Vector3 = arFrag.arSceneView.scene.camera.forward
        val position = Vector3.add(cameraPos, cameraDirection.scaled(randomDepth))

        // Create an ARCore Anchor at the position.
        val pose = Pose.makeTranslation(position.x, position.y, position.z)

        // Check if there is an active tracking session active
        val frame = arFrag.arSceneView.session?.update()
        if (frame?.camera?.trackingState.toString() == "PAUSED") {
            return null
        }

        val anchor: Anchor = arFrag.arSceneView.session!!.createAnchor(pose)


        // Create the Sceneform AnchorNode
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFrag.arSceneView.scene)

        // Create the node relative to the AnchorNode
        val node = TransformableNode(arFrag.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = modelRenderable

        return node
    }

    /**
     * Creates an anchor at a random location for ViewRenderable
     */
    private fun createRandomLocationAnchorForViewRenderable(
        viewRenderable: ViewRenderable,
        randomDirection: Int,
        randomDepth: Float
    ): TransformableNode? {

        // Random the image location
        var cameraDirection: Vector3 = arFrag.arSceneView.scene.camera.forward

        when (randomDirection) {
            0 -> cameraDirection = arFrag.arSceneView.scene.camera.down
            1 -> cameraDirection = arFrag.arSceneView.scene.camera.back
            2 -> cameraDirection = arFrag.arSceneView.scene.camera.left
            3 -> cameraDirection = arFrag.arSceneView.scene.camera.right
            4 -> cameraDirection = arFrag.arSceneView.scene.camera.up
        }

        val cameraPos: Vector3 = arFrag.arSceneView.scene.camera.worldPosition
        //val cameraForward: Vector3 = arFrag.arSceneView.scene.camera.forward
        val position = Vector3.add(cameraPos, cameraDirection.scaled(randomDepth))

        // Create an ARCore Anchor at the position.
        val pose = Pose.makeTranslation(position.x, position.y, position.z)

        // Check if there is an active tracking session active
        val frame = arFrag.arSceneView.session?.update()
        if (frame?.camera?.trackingState.toString() == "PAUSED") {
            return null
        }

        val anchor: Anchor = arFrag.arSceneView.session!!.createAnchor(pose)


        // Create the Sceneform AnchorNode
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFrag.arSceneView.scene)

        // Create the node relative to the AnchorNode
        val node = TransformableNode(arFrag.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = viewRenderable

        return node
    }

    /**
     * Get Image from website
     */
    private fun getWebImage(urlEndPart: String): Bitmap {

        fun getImg(imgUrl: URL): Bitmap {
            val inputStream = imgUrl.openStream()
            return BitmapFactory.decodeStream(inputStream)
        }


        val serverImagePath = "$SERVER_IMG_BASE_URL${urlEndPart}"
        var img: Bitmap
        runBlocking(Dispatchers.IO) {
            img = getImg(URL(serverImagePath))
        }
        return img
    }

}