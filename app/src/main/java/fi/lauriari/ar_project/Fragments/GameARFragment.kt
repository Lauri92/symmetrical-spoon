package fi.lauriari.ar_project.Fragments


import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import fi.lauriari.ar_project.*
import fi.lauriari.ar_project.Entities.Inventory
import fi.lauriari.ar_project.repositories.TriviaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.net.URL

val SERVER_IMG_BASE_URL = URL("https://users.metropolia.fi/~lauriari/AR_project/images/")

class GameARFragment : Fragment() {

    private lateinit var arFrag: ArFragment
    private var quizQuestionRenderable: ViewRenderable? = null
    private var imageSelectionQuizTvRenderable: ViewRenderable? = null
    private var imageRenderable: ViewRenderable? = null
    private var imageRenderable2: ViewRenderable? = null
    private var imageRenderable3: ViewRenderable? = null
    private var imageQuestionRenderable: ViewRenderable? = null
    private var sphereRenderable: ModelRenderable? = null
    private val args by navArgs<GameARFragmentArgs>()
    private val mMapDetailsViewModel: MapDetailsViewModel by viewModels()
    private val mInventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var mTriviaApiViewModel: TriviaApiViewModel
    private var latestMapDetails: MapDetails? = null
    private var selectedMapLatLngPoint: MapLatLng? = null
    private var inventory: Inventory? = null
    private var quizQuestion: Response<QuizQuestion>? = null
    private var imageQuestionsResponse: MutableList<ImageQuestion>? = null
    private var imageQuestionList: MutableList<ImageQuestion>? = null
    private var imageSelectionQuestionList: MutableList<ImageSelectionQuestion>? = null

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
        inventory = mInventoryViewModel.getInventoryNormal()

        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        buildRenderables()

        val repository = TriviaRepository()
        val viewModelFactory = TriviaViewModelFactory(repository)
        mTriviaApiViewModel =
            ViewModelProvider(this, viewModelFactory).get(TriviaApiViewModel::class.java)
        lifecycleScope.launch(context = Dispatchers.IO) {
            // TODO: Check that these exist on click listeners else return ! ! !
            quizQuestion = mTriviaApiViewModel.getQuiz()
            imageQuestionsResponse = mTriviaApiViewModel.getImageQuestions()
            imageSelectionQuestionList = mTriviaApiViewModel.getImageSelectionQuestions()
            //imageQuestionList = (imageQuestionsResponse!!.body() as MutableList<ImageQuestion>?)!!
            Log.d("image", imageSelectionQuestionList!![0].correctAnswer)
        }

        view.findViewById<Button>(R.id.add_flag_question_btn).setOnClickListener {
            imageQuestionRenderable ?: return@setOnClickListener

            val node: TransformableNode =
                createLocationAnchorForViewRenderable(imageQuestionRenderable!!)
                    ?: return@setOnClickListener

            // Randomize the questions
            imageQuestionsResponse?.shuffle()
            val chosenQuestion = imageQuestionsResponse?.take(1)

            val questionTv = imageQuestionRenderable!!.view.findViewById<TextView>(R.id.question_tv)
            val flagIv = imageQuestionRenderable!!.view.findViewById<ImageView>(R.id.flag_iv)
            val button1 = imageQuestionRenderable!!.view.findViewById<Button>(R.id.answer1_btn)
            val button2 = imageQuestionRenderable!!.view.findViewById<Button>(R.id.answer2_btn)
            val button3 = imageQuestionRenderable!!.view.findViewById<Button>(R.id.answer3_btn)

            val answers = mutableListOf(
                chosenQuestion!![0].answer1,
                chosenQuestion[0].answer2,
                chosenQuestion[0].answer3
            )

            // Randomize the answer locations
            answers.shuffle()

            // Create a drawable from an inputstream opened from an asset to display on screen
            //val stream: InputStream = activity?.assets!!.open(chosenQuestion[0].flagSource)
            //val drawable = Drawable.createFromStream(stream, null)
            //flagIv.setImageDrawable(drawable)

            val image = getWebImage(chosenQuestion[0].imageSource)

            questionTv.text = chosenQuestion[0].question
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


            val questionTv = quizQuestionRenderable!!.view.findViewById<TextView>(R.id.quiz_tv)
            val button1 = quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer1_btn)
            val button2 = quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer2_btn)
            val button3 = quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer3_btn)

            val correctAnswer = quizQuestion?.body()!!.results[0].correct_answer
            val answers = mutableListOf(
                quizQuestion?.body()!!.results[0].incorrect_answers[0],
                quizQuestion?.body()!!.results[0].incorrect_answers[1],
                quizQuestion?.body()!!.results[0].correct_answer
            )
            answers.shuffle()


            val decodedQuestion: String = Html
                .fromHtml(quizQuestion?.body()!!.results[0].question, Html.FROM_HTML_MODE_COMPACT)
                .toString()
            val decodedAnswers0: String = Html
                .fromHtml(answers[0], Html.FROM_HTML_MODE_COMPACT)
                .toString()
            val decodedAnswers1: String = Html
                .fromHtml(answers[1], Html.FROM_HTML_MODE_COMPACT)
                .toString()
            val decodedAnswers2: String = Html
                .fromHtml(answers[2], Html.FROM_HTML_MODE_COMPACT)
                .toString()

            questionTv.text = decodedQuestion//quizQuestion.body()!!.results[0].question
            button1.text = decodedAnswers0
            button2.text = decodedAnswers1
            button3.text = decodedAnswers2

            button1.setOnClickListener {
                if (button1.text == correctAnswer) {
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
                if (button2.text == correctAnswer) {
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
                if (button3.text == correctAnswer) {
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
            //imageSelectionQuestionList ?: return@setOnClickListener

            imageSelectionQuestionList?.shuffle()
            val chosen = imageSelectionQuestionList?.take(1)
            val chosenQuestion = chosen!![0]
            Log.d("chosenquestion", chosenQuestion.question)
            val imageUrls = mutableListOf(
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

                node.name = imageUrls[i]

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


                val randomedImage: Bitmap = getWebImage(imageUrls[i])
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

        var diamondInsertable = 0
        val random = (0..3).random()
        if (random == 1) {
            diamondInsertable = 1
        }


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

        var imageresource: Int = 123
        when (selectedMapLatLngPoint!!.reward) {
            "Emerald" -> {
                imageresource = R.drawable.emerald
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds + 1,
                        latestMapDetails!!.collectedRubies,
                        latestMapDetails!!.collectedSapphires,
                        latestMapDetails!!.collectedTopazes,
                        latestMapDetails!!.collectedDiamonds + diamondInsertable
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds + 1,
                        inventory!!.rubies,
                        inventory!!.sapphires,
                        inventory!!.topazes,
                        inventory!!.diamonds + diamondInsertable
                    )
                )
            }
            "Ruby" -> {
                imageresource = R.drawable.ruby
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds,
                        latestMapDetails!!.collectedRubies + 1,
                        latestMapDetails!!.collectedSapphires,
                        latestMapDetails!!.collectedTopazes,
                        latestMapDetails!!.collectedDiamonds + diamondInsertable
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds,
                        inventory!!.rubies + 1,
                        inventory!!.sapphires,
                        inventory!!.topazes,
                        inventory!!.diamonds + diamondInsertable
                    )
                )
            }
            "Sapphire" -> {
                imageresource = R.drawable.sapphire
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds,
                        latestMapDetails!!.collectedRubies,
                        latestMapDetails!!.collectedSapphires + 1,
                        latestMapDetails!!.collectedTopazes,
                        latestMapDetails!!.collectedDiamonds + diamondInsertable
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds,
                        inventory!!.rubies,
                        inventory!!.sapphires + 1,
                        inventory!!.topazes,
                        inventory!!.diamonds + diamondInsertable
                    )
                )
            }
            "Topaz" -> {
                imageresource = R.drawable.topaz
                mMapDetailsViewModel.updateMapDetails(
                    MapDetails(
                        latestMapDetails!!.id,
                        latestMapDetails!!.time,
                        latestMapDetails!!.collectedEmeralds,
                        latestMapDetails!!.collectedRubies,
                        latestMapDetails!!.collectedSapphires,
                        latestMapDetails!!.collectedTopazes + 1,
                        latestMapDetails!!.collectedDiamonds + diamondInsertable
                    )
                )
                mInventoryViewModel.updateInventory(
                    Inventory(
                        inventory!!.id,
                        inventory!!.emeralds,
                        inventory!!.rubies,
                        inventory!!.sapphires,
                        inventory!!.topazes + 1,
                        inventory!!.diamonds + diamondInsertable
                    )
                )
            }
        }

        val gem = selectedMapLatLngPoint!!.reward
        val message =
            if (gem != "Emerald") "You were rewarded a" else "You were rewarded an"
        val diamondMessage = if (diamondInsertable != 0) "You also got a diamond!" else ""

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("OK") { _, _ ->
            findNavController().navigate(R.id.action_gameARFragment_to_gameMapFragment)
        }
        builder.setTitle("Task completed!")
        builder.setMessage("$message $gem!\n$diamondMessage")
        builder.setIcon(imageresource)
        builder.setCancelable(false)
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
            .thenAccept { imageQuestionRenderable = it }

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
        if (randomDirection == 2 || randomDirection == 3) {
            // Rotate viewrenderable so that text is facing towards the user
            node.localRotation = Quaternion(0f, 0.7399401f, 0f, 0.67267275f)
        }

        return node
    }

    /**
     * Get Image from website
     */
    private fun getWebImage(Url: String): Bitmap {

        fun getImg(imgUrl: URL): Bitmap {
            val inputStream = imgUrl.openStream()
            return BitmapFactory.decodeStream(inputStream)
        }


        var img: Bitmap
        runBlocking(Dispatchers.IO) {
            img = getImg(URL(Url))
        }
        return img
    }

}