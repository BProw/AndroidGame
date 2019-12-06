package com.android.accelerometer

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

import android.view.View

import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.security.SecureRandom
import kotlin.math.abs


class Accelerometer : AppCompatActivity(), SensorEventListener {
    private var x : Float = 0f
    private var y : Float = 0f
    private var z : Float = 0f
    private lateinit var trash : ImageView
    private lateinit var missile : ImageView
    private lateinit var missile2 : ImageView
    private lateinit var outputMsg : TextView
    private lateinit var layout : ConstraintLayout
    private var sensor : SensorManager? = null

	// comments for code
    private var elapsedTime : Long = 0
    private var trashSize = 0
    private var missileSize = 0
    private var collision = 0
    private var startTime : Long = 0
    private var currTime : Long = 0
    private var rand : SecureRandom = SecureRandom()
    private var countCollisions = 0
    private var badGuySpeed = 2f


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensor =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager
        trash = findViewById(R.id.trash)
        outputMsg = findViewById(R.id.outputMsg)
        missile = findViewById(R.id.missle)

        layout = findViewById(R.id.layout)

        trashSize = trash.maxHeight + trash.maxWidth
        missileSize = missile.maxHeight + missile.maxWidth
        collision = (trashSize-missileSize)
        Log.i("Minus********:", (trashSize-missileSize).toString())
        missile.x = 1200f
        missile.y = 790f


        setDims()
    }
    private fun setDims() {

        // frameHeight = frame.height
       // frameWidth = frame.width

    }

    /**
     * not in use...
     */
    override fun onAccuracyChanged(sensor : Sensor, accuracy : Int) {

    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onSensorChanged(event : SensorEvent) {
        var dm = resources.displayMetrics
        val layHeight = dm.heightPixels
        val layWidth = dm.widthPixels


        val xTrash = findViewById<TextView>(R.id.xTrash)
        val yTrash = findViewById<TextView>(R.id.yTrash)
        yTrash.visibility = View.INVISIBLE

        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

            currTime = System.currentTimeMillis()
            x -= event.values[0]
            y += event.values[1]
            z = event.values[2]


            trash.x = x
            trash.y = y
            //   trash.rotationX = 5F
            trash.rotationY = x-y
            /**
             *
             */
            missile.x -= badGuySpeed
           // missile.y += badGuySpeed
            var upper = 2000
            var lower = 1980
            var randX = rand.nextInt((upper-lower) + lower).toFloat()
            if(missile.x < 0) {
                badGuySpeed += .1f
                missile.x = 1000f
                missile.y = rand.nextInt(1600).toFloat()

            }
            detectCollision(x, y)

          //  var trashWidth = trash.maxWidth
          //  var trashHeight = trash.maxHeight

         //   Log.i("X n Y:", trash.x.toString() + "\t" +  trash.y.toString() )
         //   var xHit = abs(trash.x- missile.x)
         //   var yHit = abs(trash.y- missile.y)
         //   Log.i("XHit:", xHit.toString())
         //   Log.i("YHit:", yHit.toString())
          //  if(xHit == 0f || yHit == 0f) {
         //       outputMsg.text = "You lost!"
         //       outputMsg.visibility = View.VISIBLE
          //      sensor!!.unregisterListener(this)
         //   }

        //    setMissileMovement(x,y, event)
        //    detectCollision(trash, missile)
            var i = 0
                when {
                    trash.x >= 980f && trash.y <= 0f -> {
                        trash.x = 980f
                        trash.y =  0f

                    }

                    trash.x <= 0f && trash.y <= 0f -> {
                        trash.x = 0f
                        trash.y = 0f

                    }
                    trash.x <= 0f && trash.y >= 1630f -> {
                        trash.x = 0f
                        trash.y = 1630f

                    }
                    trash.x >= 980f && trash.y >= 1630f -> {
                        trash.x = 980f
                        trash.y = 1630f

                    }
                    trash.x < 0f -> {
                        trash.x = 0f
                        i++;
                        //  trash.y = y
                    }
                    trash.x > 980f -> {
                        trash.x = 980f
                        i++;
                        // trash.y = y
                    }
                    trash.y < 0f -> {
                        // trash.x = x
                        trash.y = 0f

                        i++;
                    }
                    trash.y > 1630f -> {
                        // trash.x = x
                        trash.y = 1630f
                        i++;

                    }

                }
                elapsedTime = ((currTime-startTime)/1000)


                xTrash.text = elapsedTime.toString()
             //   yTrash.text = trash.y.toString()

                if(i > 0) {
                    outputMsg.text = "You lost!"
                    outputMsg.visibility = View.VISIBLE
                   sensor!!.unregisterListener(this)
                   // val intent = Intent(this,SuhDud::class.java)
                   // startActivity(intent)

                }



            /*
            if(trash.x < 0 && trash.y >= 1630) {
                trash.x = 0f
                trash.y = 1630f
            } else if(trash.x > 980 && trash.y > 0) {
                trash.x = 980f
                trash.y =  0f
            } else if(trash.x < 0 && trash.y >= 0) {
                trash.x = 0f
                trash.y =  0f
            } else if( trash.x > 980 && trash.y > 1630) {
                trash.x > 980 && trash.y >= 1630
                trash.x = 980f
                trash.y = 1630f

            }
*/

        }
    }
    private fun setMissileMovement(x : Float, y : Float, event : SensorEvent) {
        var missX = event.values[0]
        var missY = event.values[1]

        val secureRandom = SecureRandom()
        secureRandom.nextInt(100)

        missile.x -= 3f
        if(missile.x < 0) {
            missile.x = 1200f
        }


        when {
            missile.x >= 980f && missile.y <= 0f -> {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun detectCollision(x : Float, y : Float) {
        var rect1 = Rect()
        trash.getHitRect(rect1)
        var rect2 = Rect()
        missile.getHitRect(rect2)

        if(Rect.intersects(rect1, rect2)) {
            countCollisions++
            outputMsg.text = "Collisions: $countCollisions"
            outputMsg.visibility = View.VISIBLE
            sensor!!.unregisterListener(this)
        }

        var trashWidth = trash.width
        var trashHeight = trash.height
        Log.i("WIDTH", "$trashWidth \t$trashHeight")
        Log.i("Missile X n Y: ", missile.x.toString() + "\t" + missile.y.toString())
        Log.i("TRASH X n Y: ", trash.x.toString() + "\t" + trash.y.toString())

            if(trash.x - missile.x <= 0 && missile.width < 4) {

            }

      //  if(trash.x < missile.x && missile.x < (trash.x + trashWidth) &&
      //      trash.y < missile.y && missile.y < (trash.y + trashHeight)) {


      //  }



    }
    override fun onPause() {
        super.onPause()
      //  sensor!!.unregisterListener(this)
     //   sensor!!.registerListener(this,sensor!!
     //       .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
      //      SensorManager.SENSOR_DELAY_FASTEST)
       // val intent = Intent(this,SuhDud::class.java)

       // startActivity(intent)

    }

    /**
     *
     *
     *
     *
     * https://stackoverflow.com/questions/11548864/how-to-make-an-android-program-wait
     * StackOverFlow response to how to pause program execution for a given time
     * Snippet borrowed from, FoamyGuy in his response to
     * "How to make an Android program 'wait'", accessed on 11.20.2019
     */
    override fun onResume() {
        super.onResume()
            outputMsg.text = "Prepare to begin..."
            outputMsg.visibility = View.VISIBLE

            // Borrowed from FoamyGuy, see onResume() doc.
            val r = Runnable {
                startTime = System.currentTimeMillis()
                outputMsg.visibility = View.INVISIBLE
                sensor!!.registerListener(this,sensor!!
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST)

            }
            val h = Handler()
            h.postDelayed(r,3000) // <-- the "1000" is the delay time in miliseconds


    }
}
