package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dicegame.ui.theme.DiceGameTheme
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import org.w3c.dom.Text
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val delayTime: Long = 20
    private val rollAnimations: Int = 40
    private val diceImages: IntArray = intArrayOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )
    private var player1score = 0
    private var player2score = 0
    private var currentCorrectAnswer: Int? = null
    private var jackpotAmount = 5
    private val targetScore = 20
    private var currentPlayer = 1
    private var pointsEarned = 0
    private var lastRolledNumber: Int = 0

    var doublePoints = false
    var jackpotPoints = false

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DiceGameTheme {
        Greeting("Android")
    }
}

lateinit var diceImage: ImageView
lateinit var playerTurnTextView: TextView
lateinit var playerRowLinear: LinearLayout
lateinit var editTextNumberBox: EditText
lateinit var jackpotTextQuestionBox: TextView

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.setOnClickListener {
            rollDice()
        }

        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {

            answerHandler()

            //Clear editTextNumber
            editTextNumberBox = findViewById(R.id.editTextNumber)
            editTextNumberBox.text.clear()

        }

        diceImage = findViewById(R.id.dice_image)
        playerTurnTextView = findViewById(R.id.player_turn)
        updatePlayerTurnText()
    }

    private fun rollDice() {
        //enables submit button
        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.isEnabled = true

        val handler = Handler(Looper.getMainLooper())
        var rollCount = 0
        val runnable = object : Runnable {
            override fun run() {
                val randomInt = Random.nextInt(6)

                diceImage.setImageResource(diceImages[randomInt])
                rollCount++
                if (rollCount < rollAnimations) {
                    handler.postDelayed(this, delayTime)
                } else {
                    // Display the final rolled number
                    val rolledNumber = randomInt + 1
                    lastRolledNumber = rolledNumber
                    // You can display the rolledNumber wherever you want, for example:
                    // Toast.makeText(applicationContext, "Dice landed on $rolledNumber", Toast.LENGTH_SHORT).show()
                    updatePlayerTurnText()


                    when (rolledNumber)
                    {
                        1 -> handleAddition()
                        2 -> handleSubtraction()
                        3 -> handleMultiplication()
                        4 -> handleDoublePoints()
                        5 -> handleLoseTurn()
                        6 -> handleJackpot()
                    }

                }
            }
        }
        handler.postDelayed(runnable, delayTime)
    }

    private fun handleAddition()
    {
        //prevents player from rerolling when not allowed
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.isEnabled = false

        val num1 = Random.nextInt(100)
        val num2 = Random.nextInt(100)
        displayQuestion("$num1 + $num2 =", num1 + num2)

    }

    private fun handleSubtraction()
    {
        //prevents player from rerolling when not allowed
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.isEnabled = false

        val num1 = Random.nextInt(100)
        val num2 = Random.nextInt(100)
        displayQuestion("$num1 - $num2 =", num1 - num2)
    }

    private fun handleMultiplication()
    {
        //prevents player from rerolling when not allowed
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.isEnabled = false

        val num1 = Random.nextInt(20)
        val num2 = Random.nextInt(20)
        displayQuestion("$num1 x $num2 =", num1 * num2)
    }

    private fun handleDoublePoints()
    {
        displayQuestion("Roll again for double points!!", -1)
        doublePoints = true

        //prevents player from continuously submitting the same answer
        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.isEnabled = false
    }

    private fun handleLoseTurn()
    {
        displayQuestion("Lose a turn", -2)
        switchPlayer()

        //prevents player from continuously submitting the same answer
        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.isEnabled = false
    }

    private fun handleJackpot()
    {
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.isEnabled = false

        displayQuestion("Try for Jackpot Amount", -3)
        jackpotPoints = true

        val jackpotQuestionType = Random.nextInt(3)

        if (jackpotQuestionType == 1) // Addition Problem
        {
            val num1 = Random.nextInt(100)
            val num2 = Random.nextInt(100)
            displayJackpotQuestion("$num1 + $num2 =", num1 + num2)
        }
        else if (jackpotQuestionType==2) // Subtraction Problem
        {
            val num1 = Random.nextInt(100)
            val num2 = Random.nextInt(100)
            displayJackpotQuestion("$num1 - $num2 =", num1 - num2)
        }
        else // Multiplication Problem
        {
            val num1 = Random.nextInt(20)
            val num2 = Random.nextInt(20)
            displayJackpotQuestion("$num1 x $num2 =", num1 * num2)
        }
    }

    private fun displayQuestion(question: String, correctAnswer: Int)
    {
        val questionTextView: TextView = findViewById(R.id.question_text)
        questionTextView.text = question
        Log.d("displayQuestion", "Question is $question")
        currentCorrectAnswer = correctAnswer
        Log.d("displayQuestion", "currentCorrectAnswer is $currentCorrectAnswer")
    }

    private fun displayJackpotQuestion(question: String, correctAnswer: Int)
    {
        val questionTextView: TextView = findViewById(R.id.jackpot_question_text)
        questionTextView.text = question
        Log.d("displayQuestion", "Question is $question")
        currentCorrectAnswer = correctAnswer
        Log.d("displayQuestion", "currentCorrectAnswer is $currentCorrectAnswer")
    }


    private fun answerHandler() {
        Log.d("AnswerHandler", "Answer handler function has been called")

        //prevents player from continuously submitting the same answer
        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.isEnabled = false

        val userInputEditText: EditText = findViewById(R.id.editTextNumber)
        val userInput = userInputEditText.text.toString().toIntOrNull()
        val correctAnswer = currentCorrectAnswer

        if (correctAnswer != null) {
            Log.d("AnswerHandler", "Answer has been found")
            // Check if the answer is inputted
            if (userInput == correctAnswer)
            {
                pointsEarned = calculatePointsEarned(userInput, correctAnswer)
                updatePlayerScores()
                switchPlayer()

                if (jackpotPoints)
                {
                    Log.d("answerQuestion", "jackpot bonus = $jackpotPoints")


                    jackpotAmount = 5
                    val jackpotAmountTextView: TextView = findViewById(R.id.jackpot_amount)
                    jackpotAmountTextView.text = jackpotAmount.toString() + " points"
                    Log.d("handleWrongAnswer", "Jackpot Amount displayed")


                }
            }
            else
            {
                Log.d("AnswerHandler", "Wrong Answer has been found")
                // Wrong answer
                handleWrongAnswer()
                switchPlayer()
            }
        } else {
            Log.d("AnswerHandler", "No correct answer has been found")
            // Handle the case where there is no current correct answer
            Toast.makeText(applicationContext, "Please roll the dice, then put in an answer before submitting", Toast.LENGTH_SHORT).show()
        }
        Log.d("answerQuestion", "jackpot amount = $jackpotAmount" )
        doublePoints = false
        jackpotPoints = false


        val jackpotQuestion = "Jackpot will be displayed here!!"
        jackpotTextQuestionBox = findViewById(R.id.jackpot_question_text)
        jackpotTextQuestionBox.text = jackpotQuestion
        //enables roll dice
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.isEnabled = true
    }

    //instead of returning 0 when the answer is wrong, let a function add it to the jackpot instead of the player score
    private fun calculatePointsEarned(userInput: Int?, correctAnswer: Int?): Int {
       Log.d("calculatePointsEarned", "calculatePointsEarned called")

        Log.d("calculatePointsEarned", "The value of correct answer is $correctAnswer")
        Log.d("calculatePointsEarned", "The value of userInput is $userInput")
        Log.d("calculatePointsEarned", "The value of the last rolled Number is $lastRolledNumber")
        Log.d("calculatePointsEarned", "The value of pointsEarned is $pointsEarned")

        if(doublePoints)
        {
            if (lastRolledNumber == 1)
            {
                pointsEarned = 1 * 2
                doublePoints = false
                return pointsEarned
            }
            else if(lastRolledNumber == 2)
            {
                pointsEarned = 2 * 2
                doublePoints = false
                return pointsEarned
            }
            else if(lastRolledNumber == 3)
            {
                pointsEarned = 3 * 2
                doublePoints = false
                return pointsEarned
            }
            else if(lastRolledNumber == 4)
            {
                pointsEarned = 4 * 2
                doublePoints = false
                return pointsEarned
            }
            else if(lastRolledNumber == 5)
            {
                pointsEarned = 0
                doublePoints = false
                return pointsEarned
            }
            else
            {
                pointsEarned = jackpotAmount * 2
                doublePoints = false
                return pointsEarned
            }
        }
        else if (jackpotPoints)
        {
            pointsEarned = jackpotAmount
            return pointsEarned
        }
        else
        {
            pointsEarned = when (lastRolledNumber)
            {
                1 -> 1
                2 -> 2
                3 -> 3
                4 -> 10000
                5 -> 0
                else -> jackpotAmount
            }
            return pointsEarned
        }
    }


    private fun handleWrongAnswer()
    {
        Log.d("handleWrongAnswer", "Wrong Answer function has been called")

        jackpotAmount += lastRolledNumber
        Log.d("handleWrongAnswer", "Jackpot Amount changed to $jackpotAmount")
        val jackpotAmountTextView: TextView = findViewById(R.id.jackpot_amount)
        jackpotAmountTextView.text = jackpotAmount.toString() + " points"
        Log.d("handleWrongAnswer", "Jackpot Amount displayed")

        switchPlayer()
    }

    private fun updatePlayerScores()
    {
        Log.d("updatePlayerScores", "Update Player Scores called")
        if (currentPlayer == 1)
        {
            Log.d("updatePlayerScores","player1 score change")
            player1score += pointsEarned
        }
        else
        {
            Log.d("updatePlayerScores", "player2 score change")
            player2score += pointsEarned
        }

        updatePlayerScoresUI()

        checkForWinner()
    }

    private fun updatePlayerScoresUI()
    {
        Log.d("updatePlayerScoresUI", "UpdatePlayerScoresUI called")
        val player1scoreTextView: TextView = findViewById(R.id.player1_score)
        val player2scoreTextView: TextView = findViewById(R.id.player2_score)

        player1scoreTextView.text = "Player 1: $player1score"
        player2scoreTextView.text = "Player 2: $player2score"
    }

    private fun switchPlayer()
    {
        Log.d("switchPlayer", "Switch Player Called")
        currentPlayer = if (currentPlayer == 1) 2 else 1
        updatePlayerTurnText()

        if (checkForWinner())
        {
            Log.d("switchPlayer", "Winner detected")
            if(player1score > player2score && player1score >= targetScore)
            {
                Log.d("switchPlayer", "Player 1 found as winner")
                displayQuestion("PLAYER 1 WINS THE GAME !!!", -1)

                //Locks Roll Dice Button
                val rollButton: Button = findViewById(R.id.roll_button)
                rollButton.isEnabled = true
            }
            else
            {
                Log.d("switchPlayer", "Player 2 found as winner")
                displayQuestion("PLAYER 2 WINS THE GAME !!! ", -1)

                //Locks Roll Dice Button
                val rollButton: Button = findViewById(R.id.roll_button)
                rollButton.isEnabled = true
            }

            val rollButton: Button = findViewById(R.id.roll_button)
            rollButton.isEnabled = false

        }

    }


    private fun checkForWinner(): Boolean
    {
        Log.d("checkForWinner", "checkForWinner called")
        return player1score >= targetScore || player2score >= targetScore
    }

    //Update the Player Turn UI
    private fun updatePlayerTurnText() {

        playerRowLinear = findViewById(R.id.playerRowLinear)
        var backgroundColor: Int

        Log.d("updatePlayerTurnText", "updatePlayerTurnText called")
        val playerTurn = if (currentPlayer == 1)
        {
            Log.d("updatePlayerTurnText", "Player 1's Turn")
            backgroundColor = ContextCompat.getColor(this, R.color.player1_color_purple)
            playerRowLinear.setBackgroundColor(backgroundColor)
            getString(R.string.player1_turn)
        }
        else
        {
            Log.d("updatePlayerTurnText", "Player 2's Turn")
            backgroundColor = ContextCompat.getColor(this, R.color.player2_color_red)
            playerRowLinear.setBackgroundColor(backgroundColor)
            getString(R.string.player2_turn)
        }

        Log.d("updatePlayerTurnText", "Applying Player Turn")
        playerTurnTextView.text = playerTurn
}



}
