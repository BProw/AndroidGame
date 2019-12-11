package com.android.accelerometer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_high_scores.*
import java.util.*

/**
 *
 * High score screen Reggie App.
 *
 * @author Brian LeProwse (CS 3013 - Fall 2019)
 *

 */
class HighScores : AppCompatActivity() {
    private var highScore = " "
    private lateinit var iv : ImageView
    private lateinit var tv : TextView
    private lateinit var go : Button
    private lateinit var scoreField : TextView
    private lateinit var playGame : Intent
    private lateinit var button : Button
    private var rand : Random = Random()
    private var randReggQuote = 0

    private val REGGIE_QUOTES =
            arrayOf("\"Burritos are like a box of chocolates.\"",
                    "\"WHAT'S IN THE BURRITO!?!?!?\"",
                    "\"Believe you can Burrito and you're almost there.\"",
                    "\"Hey careful, man! There's a burrito here!\"",
                    "\"Sometimes you eat the burrito and well, sometimes the burrito eats you.\"")
    private val REGG_SIGNATURE = "  \n- Mission Specialist: Reggie"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        tv = findViewById(R.id.tv)
        iv = findViewById(R.id.iv)
        go = findViewById(R.id.startGame)
        scoreField = findViewById(R.id.scoreField)
        button = findViewById(R.id.button)

        tv.text = intent.getStringExtra("user")

        playGame = Intent(this,Accelerometer::class.java)
        playBtnClick()
    }
    private fun playBtnClick() {
        go.setOnClickListener {
           // randReggQuote = rand.nextInt(REGGIE_QUOTES.size)
            //textView8.text = REGGIE_QUOTES[randReggQuote]
            //Start game after save.
            startActivityForResult(playGame, 2)
        }
        button.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    override fun onRestart() {
        super.onRestart()
        randReggQuote = rand.nextInt(REGGIE_QUOTES.size-1)
        textView8.text = REGGIE_QUOTES[randReggQuote] + REGG_SIGNATURE
    }
    private fun findHighScore(data : Intent) {
        var c = data.getStringExtra("score").toInt()
       // scoreField.text = data.getStringExtra("score")
        var s = scoreField.text.toString().toInt()
        if(c > s) {
            scoreField.text = c.toString()
        } else {
            scoreField.text = s.toString()
        }
    }

    /**
     *  Borrowed snippet. See Sources *
     */
    private val REQUEST_IMAGE_CAPTURE = 1
    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    /**
     *  Borrowed snippet. See Sources *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                val imageBitmap = data!!.extras.get("data") as Bitmap
                iv.setImageBitmap(imageBitmap)
            }
            if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                findHighScore(data)
            }
        }
    }
}
