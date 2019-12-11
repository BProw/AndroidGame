package com.android.accelerometer

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start_up.*
import java.util.*

import java.io.ByteArrayOutputStream


/**
 *
 * Login Screen for Reggie App. Handle and store login user data.
 *
 * @author Brian LeProwse (CS 3013 - Fall 2019)
 *

 */
class StartUp : AppCompatActivity() {
    private lateinit var nameField : EditText
    private lateinit var pwField : EditText
    private lateinit var nameBtn : ImageButton
    private lateinit var lookUpUser : Button                // Future use to find user***
    private lateinit var alreadyField : EditText
    var time = 0
    private var user: String = ""
    private var password : String = ""
    private var editor : SharedPreferences.Editor? = null
    private var sharedPref : SharedPreferences? = null
    private lateinit var highScores : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        nameField = findViewById(R.id.nameField)
        pwField = findViewById(R.id.pwField)
        nameBtn = findViewById(R.id.nameBtn)
        highScores = Intent(this,HighScores::class.java)

        sharedPref =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        // Write user name and pw.
        editor = sharedPref!!.edit()

        btnClicks()
    }

    /**
     *  Handle login button clicks.
     */
    private fun btnClicks() {

        time = (System.currentTimeMillis()/1000).toInt()

        nameBtn.setOnClickListener {

            user = nameField.text.toString()
            password = pwField.text.toString()

            if(user.isNotEmpty() && password.isNotEmpty()) {
                if(sharedPref!!.contains(user) ) {
                    if(password == sharedPref!!.getString(user, password)) {

                        Toast.makeText(this, "Hello again!",
                            Toast.LENGTH_SHORT).show()

                        highScores.putExtra("user", this.user)
                        startActivity(highScores)
                    } else {
                        Toast.makeText(this, "Incorrect password for: $user",
                            Toast.LENGTH_SHORT).show()
                    }

                } else if(!sharedPref!!.contains(user)) {
                    saveUserInfo(user, password)
                } else {
                    Toast.makeText(this, "User name in use! try again",
                                      Toast.LENGTH_LONG).show()
                    nameField.text.clear()
                    pwField.text.clear()
                }
            }
        }
    }

    /**
     *  *** Not in use (11.22.19). Future to prompt user for their password. ***
     */
    private fun promptForPW(user: String) {
        var pw = ""
        var inputPW = ""
        alreadyField.hint = "$user 's PW:"
        pw = alreadyField.text.toString()
        getPass(user)

    }
    /**
     *
     * Save new user info and start HighScores activity.
     *
     */
    private fun saveUserInfo(user : String, pw : String) {

        setUser(user)
        setPW(pw)
        editor!!.putString(user,pw)
        //editor!!.putString("pw",pw)
        //val intent = Intent(this,HighScores::class.java)

        val myintent = Intent(this,
        HighScores::class.java).putExtra("user", user)
        editor!!.commit()
        startActivity(myintent)

        Toast.makeText(this,
            "new user: $user \t$password", Toast.LENGTH_LONG)
            .show()


    }

    /**
     *  *** Not in use (11.22.19). Future ***
     */
    private fun findUserName(user: String) : Boolean {

        var n = sharedPref!!.getString(user, "")

        Toast.makeText(this, "$n", Toast.LENGTH_LONG).show()
        if(n == user) {
            Toast.makeText(this, "User MATCH", Toast.LENGTH_LONG).show()
            return true
        }
      return false
    }

    /**
     *  *** Not in use (11.22.19). Future, compare user PW to actual match from user name ***
     */
    private fun getPass(user : String) : String {
        if(sharedPref!!.contains(user)) {
            var p = sharedPref!!.getString(user, password)
            Toast.makeText(this, "PW MATCH! $p", Toast.LENGTH_LONG).show()
            return p
        } else {
            Toast.makeText(this, "PW not found!", Toast.LENGTH_LONG).show()
            return " "
        }
    }
    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            //imageView3.setImageBitmap(imageBitmap)



            // Create new intent to return to calling Activity.
            val intentw = Intent()

            /*
              Add extra data to return intent.
              (user chosen font size, color, typeface, and style)
             */
           // intent.putExtra("color", makeHex)
          //  intent.putExtra("font", font)

           // val myintent = Intent(this,
             //   HighScores::class.java).putExtra("image", imageBitmap)
//Convert to byte array
        //    val stream = ByteArrayOutputStream()
          //  imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
         //   val byteArray = stream.toByteArray()

         //   val myintent
          //          = Intent(this, HighScores::class.java)
         //   myintent.putExtra("image", byteArray)

          //  startActivity(myintent)
           // finish()




        }
    }
    private fun getUser() : String {
        return this.user
    }
    private fun setUser(user : String) {
        this.user = user
    }
    private fun getPW() : String {
        return password
    }
    private fun setPW(password : String) {
        this.password = password
    }
}
