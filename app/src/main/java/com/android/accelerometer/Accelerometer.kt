package com.android.accelerometer

import android.content.Context
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
import java.security.SecureRandom


class Accelerometer : AppCompatActivity(), SensorEventListener {
    private var x : Float = 0f
    private var y : Float = 0f
    private var z : Float = 0f
    private lateinit var trash : ImageView
    private lateinit var missile : ImageView
    private lateinit var asteroid : ImageView
    private lateinit var burrito : ImageView
    private lateinit var outputMsg : TextView
    private lateinit var layout : ConstraintLayout
    private lateinit var timer : TextView
    private var sensor : SensorManager? = null


    private var up = 0f
    private var low = 0f

    private var startBurrito = 0f
    private var hitBurrito = 0f


    private var elapsedTime : Long = 0
    private var trashSize = 0
    private var missileSize = 0
    private var collision = 0
    private var startTime : Long = 0
    private var currTime : Long = 0
    private var rand : SecureRandom = SecureRandom()
    private var countCollisions = 0

    private var rotation = 0

    private var badGuySpeed = 2f
    private var burritoSpeed = 2f
    private var alternateMissile = 0f

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensor =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager
        trash = findViewById(R.id.trash)
        outputMsg = findViewById(R.id.outputMsg)
        missile = findViewById(R.id.missle)
        asteroid = findViewById(R.id.asteroid)
        burrito = findViewById(R.id.burrito)

        layout = findViewById(R.id.layout)
        timer = findViewById(R.id.timer)
        timer.visibility = View.INVISIBLE

        trashSize = trash.maxHeight + trash.maxWidth
        missileSize = missile.maxHeight + missile.maxWidth
        collision = (trashSize-missileSize)
        Log.i("Minus********:", (trashSize-missileSize).toString())
        missile.x = 1200f
        missile.y = 790f

        asteroid.x = 1400f
        asteroid.y = 790f

        startBurrito = -500f
        hitBurrito = -500f

        burrito.x = startBurrito
        burrito.y = rand.nextInt(1600).toFloat()
        rotation = rand.nextInt(2)

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


         timer.visibility = View.VISIBLE
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
            var cuck = rand.nextInt(1600).toFloat()
           // asteroid.rotationX = x-y

            missile.x -= badGuySpeed            // Increase

            var upDown = y+10 - y-2
            var upper = 2000
            var lower = 1980
            var randX = rand.nextInt((upper-lower) + lower).toFloat()
            var chode = missile.x - badGuySpeed

            if(missile.x < 0) {
                outputMsg.visibility = View.INVISIBLE
                missile.x = 1200f
              //  missile.y = y++
                var zero = 50
                var high = 1600
                alternateMissile  = rand.nextInt((high-zero)+zero).toFloat()

                up = rand.nextInt((800+20)).toFloat()
                low = rand.nextInt((800-40)).toFloat()
                missile.y = alternateMissile
            //    missile.rotationX = missile.x*rotation
                badGuySpeed += .1f

            }

          if(elapsedTime > 10) {
              asteroid.x -= badGuySpeed
          //    asteroid.rotationY = asteroid.x*x
              if(asteroid.x < 0) {
                  asteroid.x = 2000f
                  if(missile.y >=  800) {
                      asteroid.y = rand.nextInt(800).toFloat()

                    //  asteroid.y =

                  } else {
                      var mid = 800
                      var high = 1600
                      var randRange = (800..1600).shuffled().first()
                      asteroid.y = rand.nextInt(randRange).toFloat()

                  }
                  //asteroid.y = rand.nextInt(1600).toFloat()
                  // badGuySpeed += .1f
              }

          }


            if(elapsedTime > 2) {
            //    burrito.rotationX = burrito.x*y
                burrito.x += burritoSpeed
                if(burrito.x > 990) {
                    Log.i(("BURR HIT"), "$hitBurrito")
                    burrito.x = hitBurrito
                   // burritoSpeed += .5f
                    burrito.y = rand.nextInt(1600).toFloat()
                }

            }
            detectBurritoEaten()
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

                    timer.text = elapsedTime.toString()
             //   yTrash.text = trash.y.toString()

                if(i > 0) {
                //    outputMsg.text = "You lost!"
                //    outputMsg.visibility = View.VISIBLE
                //    sensor!!.unregisterListener(this)
                   // val intent = Intent(this,SuhDud::class.java)
                   // startActivity(intent)

                }
        }
    }
    private fun detectBurritoEaten() {
        var rect1 = Rect()
        trash.getHitRect(rect1)

        var burritoRect = Rect()
        burrito.getHitRect(burritoRect)
        if(Rect.intersects(rect1, burritoRect)) {
            badGuySpeed = 2f
            hitBurrito *= 2f
            outputMsg.text = "Burrito!"
            outputMsg.visibility = View.VISIBLE
            burrito.x = hitBurrito
            burrito.y = rand.nextInt(1600).toFloat()
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
        var asteroidRect = Rect()
        asteroid.getHitRect(asteroidRect)

        if(Rect.intersects(rect1, rect2) || Rect.intersects(rect1, asteroidRect)) {
            countCollisions++
            outputMsg.text = "You lost!"
            outputMsg.visibility = View.VISIBLE
            sensor!!.unregisterListener(this)
        }

        var trashWidth = trash.width
        var trashHeight = trash.height
      //  Log.i("WIDTH", "$trashWidth \t$trashHeight")
      //  Log.i("Missile X n Y: ", missile.x.toString() + "\t" + missile.y.toString())
     //   Log.i("TRASH X n Y: ", trash.x.toString() + "\t" + trash.y.toString())

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
