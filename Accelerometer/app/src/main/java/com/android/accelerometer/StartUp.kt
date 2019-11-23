package com.android.accelerometer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start_up.*
import java.util.*

class StartUp : AppCompatActivity() {
    private lateinit var nameField : EditText
    private lateinit var pwField : EditText
    private lateinit var nameBtn : ImageButton
    private lateinit var pwBtn : Button
    private lateinit var lookUpUser : Button
    private lateinit var alreadyField : EditText
    var time = 0
    var date : Date? = null
    private var user: String = ""
    private var password : String = ""
    private var editor : SharedPreferences.Editor? = null
    private var sharedPref : SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        nameField = findViewById(R.id.nameField)
        pwField = findViewById(R.id.pwField)
        nameBtn = findViewById(R.id.nameBtn)

      //  alreadyField.visibility = View.INVISIBLE
      //  pwBtn.visibility = View.INVISIBLE


        sharedPref =
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        // Write user name and pw.
        editor = sharedPref!!.edit()

        btnClicks()
    }
    private fun btnClicks() {
        var name = ""
        var pass = ""
        time = (System.currentTimeMillis()/1000).toInt()

       // var seconds = date.seconds
        //enterBtn.text = seconds.toString()

        nameBtn.setOnClickListener {
           // editor!!.clear().commit()
            user = nameField.text.toString()
            password = pwField.text.toString()

            if(user.isNotEmpty() && password.isNotEmpty()) {
                if(sharedPref!!.contains(user) ) {
                    if(password == sharedPref!!.getString(user, password)) {

                        Toast.makeText(this,
                            "Hello again!",
                            Toast.LENGTH_SHORT).show()

                        val intent = Intent(this,Accelerometer::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this,
                            "Incorrect password for: $user",
                            Toast.LENGTH_SHORT).show()
                    }


                } else if(!sharedPref!!.contains(user)) {
                    saveUserInfo(user, password)
                } else {
                    Toast.makeText(this,
                                    "User name in use! try again",
                                      Toast.LENGTH_LONG).show()
                    nameField.text.clear()
                    pwField.text.clear()
                }
            }
        }

    }
    private fun promptForPW(user: String) {
        var pw = ""
        var inputPW = ""
        alreadyField.hint = "$user 's PW:"
        pw = alreadyField.text.toString()
        getPass(user)

    }
    private fun saveUserInfo(user : String, pw : String) {

        setUser(user)
        setPW(pw)
        editor!!.putString(user,pw)
        //editor!!.putString("pw",pw)

        editor!!.commit()


        Toast.makeText(this,
            "new user: $user \t$password", Toast.LENGTH_LONG)
            .show()

        val intent = Intent(this,Accelerometer::class.java)
        startActivity(intent)


    }
    private fun findUserName(user: String) : Boolean {

        var n = sharedPref!!.getString(user, "")

        Toast.makeText(this, "$n", Toast.LENGTH_LONG).show()
        if(n == user) {
            Toast.makeText(this, "User MATCH", Toast.LENGTH_LONG).show()
            return true
        }
      return false
    }
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
    private fun getUser() : String {
        return user
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
