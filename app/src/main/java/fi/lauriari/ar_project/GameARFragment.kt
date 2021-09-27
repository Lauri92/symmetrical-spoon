package fi.lauriari.ar_project


import android.graphics.drawable.Drawable
import android.net.Uri
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
import androidx.navigation.fragment.navArgs
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.InputStream


class GameARFragment : Fragment() {

    private lateinit var arFrag: ArFragment
    private var quizQuestionRenderable: ViewRenderable? = null
    private var diamondRenderable: ViewRenderable? = null
    private var flagQuestionRenderable: ViewRenderable? = null
    private val args by navArgs<GameARFragmentArgs>()
    private val mGameMapViewModel: GameMapViewModel by viewModels()
    private var selectedMapLatLngPoint: MapLatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_a_r, container, false)
        Log.d("argstest","Id of this latlng instance: ${args.id}")
        val latestMapDetails = mGameMapViewModel.getLatestMapDetails()
        Log.d("argstest", latestMapDetails.toString())
        selectedMapLatLngPoint = mGameMapViewModel.getMapLatPointLngById(args.id)
        Log.d("argstest", selectedMapLatLngPoint.toString())

        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        buildViewRenderables()

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
            )

        view.findViewById<Button>(R.id.add_flag_question_btn).setOnClickListener {
            flagQuestionRenderable ?: return@setOnClickListener

            val node : TransformableNode = createLocationAnchor(flagQuestionRenderable!!) ?: return@setOnClickListener

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
            val stream : InputStream = activity?.assets!!.open(chosenQuestion[0].flagSource)
            val drawable = Drawable.createFromStream(stream, null)


            flagIv.setImageDrawable(drawable)
            button1.text = answers[0]
            button2.text = answers[1]
            button3.text = answers[2]

            button1.setOnClickListener {
                if (button1.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(requireContext(), "Wrong answer! No reward for you!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            button2.setOnClickListener {
                if (button2.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(requireContext(), "Wrong answer! No reward for you!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            button3.setOnClickListener {
                if (button3.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(requireContext(), "Wrong answer! No reward for you!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        view.findViewById<Button>(R.id.add_quiz_question_btn).setOnClickListener {
            quizQuestionRenderable ?: return@setOnClickListener

            val node : TransformableNode = createLocationAnchor(quizQuestionRenderable!!) ?: return@setOnClickListener

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
                    Toast.makeText(requireContext(), "Wrong answer! No reward for you!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            button2.setOnClickListener {
                if (button2.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(requireContext(), "Wrong answer! No reward for you!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            button3.setOnClickListener {
                if (button3.text == chosenQuestion[0].correctAnswer) {
                    displayAnswerResult(node)
                } else {
                    Toast.makeText(requireContext(), "Wrong answer! No reward for you!", Toast.LENGTH_SHORT)
                        .show()
                }
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
        diamondRenderable?.let { createLocationAnchor(it) }?.setOnTapListener { hitTestResult, motionEvent ->
            hitTestResult.node?.setParent(null)
            hitTestResult.node?.renderable = null
        }
    }

    /**
     * Build the ViewRenderables needed for the fragment
     */
    private fun buildViewRenderables() {
        ViewRenderable.builder()
            .setView(requireContext(), R.layout.flag_question_layout)
            .build()
            .thenAccept { flagQuestionRenderable = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.quiz_question_layout)
            .build()
            .thenAccept { quizQuestionRenderable = it }

        ViewRenderable.builder()
            .setView(requireContext(), R.layout.reward_layout)
            .build()
            .thenAccept { diamondRenderable = it }
    }

    /**
     * Create a location for the anchor, this is where the selected item (question or reward) will be placed
     */
    private fun createLocationAnchor(viewRenderable: ViewRenderable): TransformableNode? {
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


}