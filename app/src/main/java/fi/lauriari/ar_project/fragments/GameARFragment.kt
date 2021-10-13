package fi.lauriari.ar_project.fragments


import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import fi.lauriari.ar_project.*
import fi.lauriari.ar_project.entities.Inventory
import fi.lauriari.ar_project.datamodels.ImageQuestion
import fi.lauriari.ar_project.datamodels.ImageSelectionQuestion
import fi.lauriari.ar_project.entities.DailyQuest
import fi.lauriari.ar_project.entities.MapDetails
import fi.lauriari.ar_project.entities.MapLatLng
import fi.lauriari.ar_project.repositories.TriviaRepository
import fi.lauriari.ar_project.viewmodels.InventoryViewModel
import fi.lauriari.ar_project.viewmodels.MapDetailsViewModel
import fi.lauriari.ar_project.viewmodels.TriviaApiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.net.URL


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
    private var imageSelectionQuestionList: MutableList<ImageSelectionQuestion>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_a_r, container, false)
        latestMapDetails = mMapDetailsViewModel.getLatestMapDetails()
        selectedMapLatLngPoint = mMapDetailsViewModel.getMapLatPointLngById(args.id)
        inventory = mInventoryViewModel.getInventoryNormal()

        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        val repository = TriviaRepository()
        val viewModelFactory = TriviaViewModelFactory(repository)
        mTriviaApiViewModel =
            ViewModelProvider(this, viewModelFactory).get(TriviaApiViewModel::class.java)
        lifecycleScope.launch(context = Dispatchers.IO) {
            // TODO: Check that these exist on click listeners else return ! ! !
            quizQuestion = mTriviaApiViewModel.getQuiz()
            imageQuestionsResponse = mTriviaApiViewModel.getImageQuestions()
            imageSelectionQuestionList = mTriviaApiViewModel.getImageSelectionQuestions()
        }

        val playButton = view.findViewById<Button>(R.id.play_btn)
        val resetFab = view.findViewById<FloatingActionButton>(R.id.reset_fab)
        val gameTypeTv = view.findViewById<TextView>(R.id.gametype_tv)
        resetFab.visibility = View.GONE
        resetFab.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Clear")
            builder.setMessage("Do you really want to clear the scene?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { _, _ ->
                removeChildNodes()
                Toast.makeText(
                    requireContext(),
                    "Cleared all Augmented Reality items",
                    Toast.LENGTH_LONG
                ).show()
                playButton.visibility = View.VISIBLE
                resetFab.visibility = View.GONE
            }
            builder.setNegativeButton("Cancel") { _, _ ->
            }.create()
            builder.show()
        }


        when (args.gameType) {
            "normalQuiz" -> {
                ViewRenderable.builder()
                    .setView(requireContext(), R.layout.quiz_question_layout)
                    .build()
                    .thenAccept { quizQuestionRenderable = it }
                gameTypeTv.text = getString(R.string.normal_quiz_description)
                playButton.text = getString(R.string.start_quiz)
                playButton.setOnClickListener {
                    quizQuestionRenderable ?: return@setOnClickListener
                    quizQuestion?.body() ?: return@setOnClickListener
                    normalQuizTask()
                    playButton.visibility = View.GONE
                    resetFab.visibility = View.VISIBLE
                }
            }
            "imageQuiz" -> {
                ViewRenderable.builder()
                    .setView(requireContext(), R.layout.image_question_layout)
                    .build()
                    .thenAccept { imageQuestionRenderable = it }
                gameTypeTv.text = getString(R.string.image_quiz_description)
                playButton.text = getString(R.string.start_image_quiz)
                playButton.setOnClickListener {
                    imageQuestionRenderable ?: return@setOnClickListener
                    imageQuizTask()
                    playButton.visibility = View.GONE
                    resetFab.visibility = View.VISIBLE
                }
            }
            "sphereTask" -> {
                playButton.text = getString(R.string.start_sphere_task)
                gameTypeTv.text = getString(R.string.sphere_task_description)
                playButton.setOnClickListener {
                    sphereTask()
                    playButton.visibility = View.GONE
                    resetFab.visibility = View.VISIBLE
                }
            }
            "multipleImageQuiz" -> {
                createMultipleImageQuestionRenderables()
                gameTypeTv.text = getString(R.string.multiple_image_quiz_description)
                playButton.text = getString(R.string.start_find_correct_image_task)
                playButton.setOnClickListener {
                    imageSelectionQuizTvRenderable ?: return@setOnClickListener
                    imageRenderable ?: return@setOnClickListener
                    imageRenderable2 ?: return@setOnClickListener
                    imageRenderable3 ?: return@setOnClickListener
                    multipleImageQuizTask()
                    playButton.visibility = View.GONE
                    resetFab.visibility = View.VISIBLE
                }
            }
        }

        return view
    }

    /**
     * Removes the generated nodes from the scene
     */
    private fun removeChildNodes() {
        val children: List<Node> = ArrayList(arFrag.arSceneView.scene.children)
        for (node in children) {
            if (node is AnchorNode) {
                if (node.anchor != null) {
                    node.anchor!!.detach()
                }
            }
            if (node !is Camera) {
                node.setParent(null)
            }
        }
    }

    /**
     * Generates a normal quiz
     */
    private fun normalQuizTask() {
        val node: TransformableNode =
            createLocationAnchorForViewRenderable(quizQuestionRenderable!!)
                ?: return

        val questionTv =
            quizQuestionRenderable!!.view.findViewById<TextView>(R.id.quiz_tv)
        val button1 =
            quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer1_btn)
        val button2 =
            quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer2_btn)
        val button3 =
            quizQuestionRenderable!!.view.findViewById<Button>(R.id.answer3_btn)

        val correctAnswer = quizQuestion?.body()!!.results[0].correct_answer
        val answers = mutableListOf(
            quizQuestion?.body()!!.results[0].incorrect_answers[0],
            quizQuestion?.body()!!.results[0].incorrect_answers[1],
            quizQuestion?.body()!!.results[0].correct_answer
        )
        answers.shuffle()

        val decodedQuestion =
            decodeHtmlString(quizQuestion?.body()!!.results[0].question)
        val decodedAnswers0 = decodeHtmlString(answers[0])
        val decodedAnswers1 = decodeHtmlString(answers[1])
        val decodedAnswers2 = decodeHtmlString(answers[2])

        questionTv.text = decodedQuestion
        button1.text = decodedAnswers0
        button2.text = decodedAnswers1
        button3.text = decodedAnswers2

        button1.setOnClickListener {
            if (button1.text == correctAnswer) {
                handleCorrectAnswer(node)
            } else {
                handleWrongAnswer()
            }
        }

        button2.setOnClickListener {
            if (button2.text == correctAnswer) {
                handleCorrectAnswer(node)
            } else {
                handleWrongAnswer()
            }
        }

        button3.setOnClickListener {
            if (button3.text == correctAnswer) {
                handleCorrectAnswer(node)
            } else {
                handleWrongAnswer()
            }
        }

    }

    /**
     * Generates an image quiz
     */
    private fun imageQuizTask() {
        val node: TransformableNode =
            createLocationAnchorForViewRenderable(imageQuestionRenderable!!)
                ?: return
        Log.d("imagequetions", imageQuestionsResponse.toString())
        imageQuestionsResponse?.shuffle()
        val chosenQuestion = imageQuestionsResponse?.take(1)?.get(0)

        val questionTv =
            imageQuestionRenderable!!.view.findViewById<TextView>(R.id.question_tv)
        val flagIv =
            imageQuestionRenderable!!.view.findViewById<ImageView>(R.id.flag_iv)
        val button1 =
            imageQuestionRenderable!!.view.findViewById<Button>(R.id.answer1_btn)
        val button2 =
            imageQuestionRenderable!!.view.findViewById<Button>(R.id.answer2_btn)
        val button3 =
            imageQuestionRenderable!!.view.findViewById<Button>(R.id.answer3_btn)

        val answers = mutableListOf(
            chosenQuestion!!.answer1,
            chosenQuestion.answer2,
            chosenQuestion.answer3
        )

        // Randomize the answer locations
        answers.shuffle()

        val image = getWebImage(chosenQuestion.imageSource)

        questionTv.text = chosenQuestion.question
        flagIv.setImageBitmap(image)
        button1.text = answers[0]
        button2.text = answers[1]
        button3.text = answers[2]

        button1.setOnClickListener {
            if (button1.text == chosenQuestion.correctAnswer) {
                handleCorrectAnswer(node)
            } else {
                handleWrongAnswer()
            }
        }
        button2.setOnClickListener {
            if (button2.text == chosenQuestion.correctAnswer) {
                handleCorrectAnswer(node)
            } else {
                handleWrongAnswer()
            }
        }
        button3.setOnClickListener {
            if (button3.text == chosenQuestion.correctAnswer) {
                handleCorrectAnswer(node)
            } else {
                handleWrongAnswer()
            }
        }
    }

    /**
     * Generates a spehere task
     */
    private fun sphereTask() {
        createSpehere()
        sphereRenderable ?: return
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
                    ?: return

            node.setOnTapListener { _, _ ->
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
                    handleCorrectAnswer(node)
                }
            }
        }
    }

    /**
     * Generates a task with multiple images
     */
    private fun multipleImageQuizTask() {
        imageSelectionQuizTvRenderable ?: return
        imageRenderable ?: return
        imageRenderable2 ?: return
        imageRenderable3 ?: return
        imageSelectionQuestionList?.shuffle()
        val chosen = imageSelectionQuestionList?.take(1)
        val chosenQuestion = chosen!![0]
        Log.d("chosenquestion", chosenQuestion.question)
        val imageUrls = mutableListOf(
            chosenQuestion.image1,
            chosenQuestion.image2,
            chosenQuestion.image3
        )


        createLocationAnchorForViewRenderable(imageSelectionQuizTvRenderable!!)


        imageSelectionQuizTvRenderable!!.view.findViewById<TextView>(R.id.image_selection_question_tv).text =
            chosenQuestion.question

        val imageRenderablesList =
            listOf(
                imageRenderable!!,
                imageRenderable2!!,
                imageRenderable3!!
            )
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
                    ?: return

            node.name = imageUrls[i]

            node.setOnTapListener { _, _ ->
                if (node.name == chosenQuestion.correctAnswer) {
                    handleCorrectAnswer(node)
                } else {
                    handleWrongAnswer()
                }

            }

            val randomedImage: Bitmap = getWebImage(imageUrls[i])
            viewRenderable.view.findViewById<ImageView>(R.id.image)
                .setImageBitmap(randomedImage)
        }
    }

    /**
     * After clicking the correct button, inform user of the correct answer and update DB
     * Modifies this LatLng value isComplete value to be true and adds correct reward(s) to Inventory
     */
    private fun handleCorrectAnswer(questionNode: TransformableNode) {
        Toast.makeText(
            requireContext(),
            "Correct answer! Collect your reward!",
            Toast.LENGTH_SHORT
        )
            .show()
        questionNode.setParent(null)
        questionNode.renderable = null

        lifecycleScope.launch {
            var diamondInsertable = 0
            val random = (0..3).random()
            if (random == 1) {
                diamondInsertable = 1
            }

            var imageresource = 123
            val updateDb = async {
                val updatedMapLatLng = MapLatLng(
                    selectedMapLatLngPoint!!.id,
                    selectedMapLatLngPoint!!.mapDetailsId,
                    selectedMapLatLngPoint!!.lat,
                    selectedMapLatLngPoint!!.lng,
                    selectedMapLatLngPoint!!.address,
                    selectedMapLatLngPoint!!.reward,
                    selectedMapLatLngPoint!!.gameType,
                    false
                )
                mMapDetailsViewModel.updateMapLatLng(updatedMapLatLng)


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
            }

            updateDb.await()

            lifecycleScope.launch {
                val dailyQuest =
                    mMapDetailsViewModel.getDailyQuestsByMapDetailsId(latestMapDetails!!.id)
                val updatedLatestMapDetails = mMapDetailsViewModel.getLatestMapDetails()
                val updatedInventory = mInventoryViewModel.getInventoryNormal()
                Log.d("daily", dailyQuest.toString())
                if (updatedLatestMapDetails.collectedEmeralds >= dailyQuest[0].requiredEmeralds &&
                    updatedLatestMapDetails.collectedRubies >= dailyQuest[0].requiredRubies &&
                    updatedLatestMapDetails.collectedSapphires >= dailyQuest[0].requiredSapphires &&
                    updatedLatestMapDetails.collectedTopazes >= dailyQuest[0].requiredTopazes &&
                    !dailyQuest[0].isCompleted
                ) {
                    Log.d("daily", "UPDATING!!")
                    mMapDetailsViewModel.updateDailyQuest(
                        DailyQuest(
                            dailyQuest[0].id,
                            dailyQuest[0].mapDetailsId,
                            dailyQuest[0].requiredEmeralds,
                            dailyQuest[0].requiredRubies,
                            dailyQuest[0].requiredSapphires,
                            dailyQuest[0].requiredTopazes,
                            dailyQuest[0].requiredSteps,
                            dailyQuest[0].description,
                            dailyQuest[0].rewardString,
                            dailyQuest[0].rewardAmount,
                            true,
                        )
                    )
                    mInventoryViewModel.updateInventory(
                        Inventory(
                            updatedInventory.id,
                            updatedInventory.emeralds,
                            updatedInventory.rubies,
                            updatedInventory.sapphires,
                            updatedInventory.topazes,
                            updatedInventory.diamonds + dailyQuest[0].rewardAmount
                        )
                    )
                }
            }

            val gem = selectedMapLatLngPoint!!.reward
            val message =
                if (gem != "Emerald") "You were rewarded a" else "You were rewarded an"
            val diamondMessage = if (diamondInsertable != 0) "You also got a diamond!" else ""

            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.task_completed_dialog)
            dialog.findViewById<ImageView>(R.id.task_complete_iv).setImageResource(imageresource)
            dialog.findViewById<TextView>(R.id.task_complete_description_tv).text =
                getString(R.string.task_completed_description, message, gem, diamondMessage)
            dialog.findViewById<Button>(R.id.task_complete_btn).setOnClickListener {
                activity?.let { it1 ->
                    Navigation.findNavController(it1, R.id.nav_host_fragment).popBackStack()
                }
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.show()

        }
    }

    /**
     * Display toast for wrong answer
     */
    private fun handleWrongAnswer() {
        Toast.makeText(requireContext(), "Wrong answer!", Toast.LENGTH_SHORT).show()
        // TODO: Add some further functionality for wrong answer, exit without reward or something else?
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

    /**
     * Crates ViewRenderables required for multiple image task
     */
    private fun createMultipleImageQuestionRenderables() {
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
    }

    /**
     * Creates a sphere
     */
    private fun createSpehere() {
        MaterialFactory.makeOpaqueWithColor(requireContext(), Color(255f, 0f, 0f))
            .thenAccept { material: Material? ->
                sphereRenderable =
                    ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), material)
            }
    }

    /**
     * Decode HTML values from string
     */
    private fun decodeHtmlString(string: String): String {
        return Html
            .fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
            .toString()
    }

}