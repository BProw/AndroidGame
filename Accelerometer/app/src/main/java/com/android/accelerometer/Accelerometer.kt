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
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import java.security.SecureRandom

/**
 * Game-play for Reggie app.
 *
 * @author Brian LeProwse (CS 3013 - Fall 2019)
 */
class Accelerometer : AppCompatActivity(), SensorEventListener {
    private var x : Float = 0f                      // Accelerometer of x.
    private var y : Float = 0f                      // Accelerometer of y.
    private var z : Float = 0f
    private lateinit var droid : ImageView          // Droid IV.
    private lateinit var displayTime : TextView
    private val PLR_SUCKS =
                arrayOf("Wow, you suck.", "Ya dingus!", "Loser", "You need practice")


    // Rect class Android Developer reference:
    // https://developer.android.com/reference/android/graphics/Rect

    private var droidRect = Rect()                   // Integer coordinates of droid.
    private lateinit var missile : ImageView         // Missile bad guy.
    private var missileRect = Rect()                 // Integer coordinates of missile.

    private lateinit var asteroid : ImageView        // Asteroid bad guy.
    private var asteroidRect = Rect()

    private lateinit var burrito : ImageView         // Burrito IV
    private var startBurrito = 0f
    private var burritoEaten = 0f

    // Msg to usr during game-play
    private lateinit var outputMsg : TextView
    private lateinit var layout : ConstraintLayout

    private lateinit var timer : TextView           // Game-play duration
    private var startTime : Long = 0
    private var currTime : Long = 0
    private var elapsedTime : Long = 0              // User timed score.
    private var sensor : SensorManager? = null      // Accelerometer

    private var up = 0f
    private var low = 0f

    private var trashSize = 0
    private var missileSize = 0
    private var collision = 0

    private var rand : SecureRandom =
                            SecureRandom()          // Random nums...

    private var countCollisions = 0

    private var rotation = 0
    // Initial velocity of bad guys & burrito.
    private var badGuySpeed = 2f
    private var burritoSpeed = 2f

    private var alternateMissile = 0f

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensor =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager

        outputMsg = findViewById(R.id.outputMsg)
        droid = findViewById(R.id.trash)
        missile = findViewById(R.id.missle)
        asteroid = findViewById(R.id.asteroid)
        burrito = findViewById(R.id.burrito)

        layout = findViewById(R.id.layout)

        timer = findViewById(R.id.timer)
        timer.visibility = View.INVISIBLE

        trashSize = droid.maxHeight + droid.maxWidth
        missileSize = missile.maxHeight + missile.maxWidth
        collision = (trashSize-missileSize)

        // Missile PNG x and y initial location.
        missile.x = 1200f
        missile.y = 790f
        // Asteroid PNG x and y initial location.
        asteroid.x = 1400f
        asteroid.y = 790f
        // Initial on screen burrito PNG location.
        startBurrito = -500f
        /*
            Decrease Burrito x-val when burrito collides with droid.
               in detectBurritoEaten()
         */

        burritoEaten = -500f
        // Initial burrito x value.
        burrito.x = startBurrito
        // Initial burrito y val (0-1600) random.
        burrito.y = rand.nextInt(1600).toFloat()

        rotation = rand.nextInt(2)
    }
    /**
     * Accuracy of sensor not needed rn (11.16.19)
     */
    override fun onAccuracyChanged(sensor : Sensor, accuracy : Int) {

    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onSensorChanged(event : SensorEvent) {
        var dm = resources.displayMetrics


        displayTime.visibility = View.INVISIBLE

        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

            currTime = System.currentTimeMillis()
            x -= event.values[0]
            y += event.values[1]
            z = event.values[2]     // Not using right now...

            droidMovement()         // Droid movement on screen.
            timerField()            // Time of game-play.
            missileMovement()       // Missile movement on screen.
            asteroidMovement()      // Asteroid movement on screen.
            burritoMovement()       // Burrito movement on screen.
            detectBurritoEaten()    // Droid ate a burrito.
            detectCollision()       // Droid hit missile or asteroid.
        }
    }

    /**
     *  Time display function.
     */
    private fun timerField() {
        timer.visibility = View.VISIBLE
        elapsedTime = ((currTime-startTime)/1000)           // Player time (seconds).
        timer.text = elapsedTime.toString()
        //   displayTime.text = droid.y.toString()
    }

    /**
     *  Determine if Reggie intersects with burrito
     *
     *  https://stackoverflow.com/questions/18398198/
     *  how-to-know-if-two-images-are-intersect-while-one-image-moving-in-android
     *  StackOverflow response for deterring collisions between ImageView items.
     *  "You should be able to use Rect.intersects(Rect, Rect), like this example:"
     *  Snippet borrowed from user, ter0, on 11.21.2019
     *
     *  Rect Class info from Android Studio:
     *  https://developer.android.com/reference/android/graphics/Rect
     */
    private fun detectBurritoEaten() {
        var rect1 = Rect()
        droid.getHitRect(rect1)

        var burritoRect = Rect()
        burrito.getHitRect(burritoRect)

        when {
            Rect.intersects(rect1, burritoRect) -> {     // Reggie ate a burrito.
                badGuySpeed = 2f                   // Reset bad guy speed to initial.
                // Decrease x-val each collision. (-500 -> -1000 -> -2000)
                burritoEaten *= 2f
                outputMsg.text = "Burrito!"        // Notify player.
                outputMsg.visibility = View.VISIBLE
                // Make burritos come less often by moving x-val *2 (negative value)
                burrito.x = burritoEaten
                // Restart y-value to rand 0-1599 on screen.
                burrito.y = rand.nextInt(1600).toFloat()
            }
        }
    }

    /**
     *  Determine burrito position and movement on screen.
     */
    private fun burritoMovement() {
        when {
            elapsedTime > 2 -> {                    // After 2 seconds, start burrito.
                burrito.x += burritoSpeed           // Increase speed across screen each pass.
                when {
                    burrito.x > 990 -> {            // Reset once once screen.
                        burrito.x = burritoEaten
                        // burritoSpeed += .5f
                        // Reposition burrito to random # 0-1599.
                        burrito.y = rand.nextInt(1600).toFloat()
                    }
                }
            }
        }
     /*
        if(elapsedTime > 2) {
            //    burrito.rotationX = burrito.x*y
            burrito.x += burritoSpeed
            when {
                burrito.x > 990 -> {
                    Log.i(("BURR HIT"), "$burritoEaten")
                    burrito.x = burritoEaten
                    // burritoSpeed += .5f
                    burrito.y = rand.nextInt(1600).toFloat()
                }
            }

            if(burrito.x > 990) {
                Log.i(("BURR HIT"), "$hitBurrito")
                burrito.x = hitBurrito
                // burritoSpeed += .5f
                burrito.y = rand.nextInt(1600).toFloat()
            }



        }

      */
    }

    /**
     *  Keeps Reggie on within screen bounds.
     */
    private fun droidMovement() {
        droid.x = x                     // Reggie x-coord in relation to accelerometer.
        droid.y = y                     // Reggie y-coord in relation to accelerometer.
        droid.rotationY = x-y           // Rotate Reggie on Y-axis.

        var i = 0
        when {
            droid.x >= 980f && droid.y <= 0f -> {
                droid.x = 980f
                droid.y =  0f
            }
            droid.x <= 0f && droid.y <= 0f -> {
                droid.x = 0f
                droid.y = 0f
            }
            droid.x <= 0f && droid.y >= 1630f -> {
                droid.x = 0f
                droid.y = 1630f
            }
            droid.x >= 980f && droid.y >= 1630f -> {
                droid.x = 980f
                droid.y = 1630f
            }
            droid.x < 0f -> {
                droid.x = 0f
                i++;
            }
            droid.x > 980f -> {
                droid.x = 980f
                i++;
            }
            droid.y < 0f -> {
                droid.y = 0f
                i++;
            }
            droid.y > 1630f -> {
                // droid.x = x
                droid.y = 1630f
                i++;
            }
        }
    }

    /**
     *  Determine asteroid ImageView movement, speed, and position.
     */
    private fun missileMovement() {
        missile.x -= badGuySpeed                        // Decrease missile x-coord by 2F.

        var upper = 2000
        var lower = 1980
        var randX = rand.nextInt((upper-lower) + lower).toFloat()
        var chode = missile.x - badGuySpeed

        when {
            missile.x < 0 -> {
                outputMsg.visibility = View.INVISIBLE   // Hide, "Burrito!" after missile exits screen.
                missile.x = 1200f                       // Reset x-value of missile IV.
                //  missile.y = y++
                var zero = 50
                var high = 1600
                alternateMissile  = rand.nextInt((high-zero)+zero).toFloat()

                up = rand.nextInt((800+20)).toFloat()
                low = rand.nextInt((800-40)).toFloat()
                missile.y = alternateMissile
                // missile.rotationX = missile.x*rotation
                badGuySpeed += .1f                      // Increase bad guy speed each pass.
            }
        }
    /*
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

     */
    }

    /**
     *  Determine asteroid ImageView movement, speed, and position.
     */
    private fun asteroidMovement() {
        when {
            elapsedTime > 10 -> {
                asteroid.x -= badGuySpeed
                // asteroid.rotationY = asteroid.x*x
                when {
                    asteroid.x < 0 -> {
                        asteroid.x = 2000f              // Reset x-value of asteroid IV.

                        when {                          // Ensure asteroid & missile not same location.
                            missile.y >= 800 -> {
                                // Random y-position from 0-799.
                                asteroid.y = rand.nextInt(800).toFloat()
                            }
                            missile.y < 800 -> {
                                var mid = 800
                                var high = 1600
                                var randRange = (800..1600).shuffled().first()
                                // Random y-position from 800-1599.
                                asteroid.y = rand.nextInt(randRange).toFloat()
                            }
                        }
                    }
                }
            }
        }
   /*
        if(elapsedTime > 10) {
            asteroid.x -= badGuySpeed
            //    asteroid.rotationY = asteroid.x*x

            if(asteroid.x < 0) {
                asteroid.x = 2000f
                if(missile.y >=  800) {
                    asteroid.y = rand.nextInt(800).toFloat()
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

    */
    }

    /**
     *  Detect collision with Reggie and bad guys.
     *
     *  https://stackoverflow.com/questions/18398198/
     *  how-to-know-if-two-images-are-intersect-while-one-image-moving-in-android
     *  StackOverflow response for deterring collisions between ImageView items.
     *  "You should be able to use Rect.intersects(Rect, Rect), like this example:"
     *  Snippet borrowed from user, ter0, on 11.21.2019
     *
     *
     * Additionally, Rect Class info from Android Studio:
     * https://developer.android.com/reference/android/graphics/Rect
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun detectCollision() {
        // var rect1 = Rect()
        // var rect2 = Rect()
        // var asteroidRect = Rect()
        droid.getHitRect(droidRect)
        missile.getHitRect(missileRect)
        asteroid.getHitRect(asteroidRect)

        if(Rect.intersects(droidRect, missileRect) ||       // Droid hits missile or asteroid.
            Rect.intersects(droidRect, asteroidRect)) {
            countCollisions++                               // Not in use?
            outputMsg.text = "You lost!"                    // Output error msg.
            outputMsg.visibility = View.VISIBLE
            sensor!!.unregisterListener(this)
        }
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
     * Pause execution upon starting Activity for 3 seconds to ready player.
     *
     * https://stackoverflow.com/questions/11548864/how-to-make-an-android-program-wait
     * "You can use the Handler class and the postDelayed() method to do that:"
     *
     * StackOverFlow response to how to pause program execution for a given time
     * Snippet borrowed from, FoamyGuy, in his response to
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
            h.postDelayed(r,3000) // (3000 milliseconds)
    }
    private fun deletedIsh() {
        //  droid.x = x
        //  droid.y = y
        // droid.rotationX = 5F
        //  droid.rotationY = x-y
        /*
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
                detectCollision()



              //  var trashWidth = droid.maxWidth
              //  var trashHeight = droid.maxHeight

             //   Log.i("X n Y:", droid.x.toString() + "\t" +  droid.y.toString() )
             //   var xHit = abs(droid.x- missile.x)
             //   var yHit = abs(droid.y- missile.y)
             //   Log.i("XHit:", xHit.toString())
             //   Log.i("YHit:", yHit.toString())
              //  if(xHit == 0f || yHit == 0f) {
             //       outputMsg.text = "You lost!"
             //       outputMsg.visibility = View.VISIBLE
              //      sensor!!.unregisterListener(this)
             //   }

            //    setMissileMovement(x,y, event)
            //    detectCollision(droid, missile)

                var i = 0
                    when {
                        droid.x >= 980f && droid.y <= 0f -> {
                            droid.x = 980f
                            droid.y =  0f

                        }

                        droid.x <= 0f && droid.y <= 0f -> {
                            droid.x = 0f
                            droid.y = 0f

                        }
                        droid.x <= 0f && droid.y >= 1630f -> {
                            droid.x = 0f
                            droid.y = 1630f

                        }
                        droid.x >= 980f && droid.y >= 1630f -> {
                            droid.x = 980f
                            droid.y = 1630f

                        }
                        droid.x < 0f -> {
                            droid.x = 0f
                            i++;
                            //  droid.y = y
                        }
                        droid.x > 980f -> {
                            droid.x = 980f
                            i++;
                            // droid.y = y
                        }
                        droid.y < 0f -> {
                            // droid.x = x
                            droid.y = 0f

                            i++;
                        }
                        droid.y > 1630f -> {
                            // droid.x = x
                            droid.y = 1630f
                            i++;

                        }

                    }
                    elapsedTime = ((currTime-startTime)/1000)

                        timer.text = elapsedTime.toString()
                 //   displayTime.text = droid.y.toString()

                    if(i > 0) {
                    //    outputMsg.text = "You lost!"
                    //    outputMsg.visibility = View.VISIBLE
                    //    sensor!!.unregisterListener(this)
                       // val intent = Intent(this,SuhDud::class.java)
                       // startActivity(intent)

                    }
    */

    }
}
