package com.saplin.dopetest_kotlin

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.absoluteValue

class Random2{
    private val MBIG = 2000000000
    private val MSEED = 161803398

    private var inext:Int = 0
    private var inextp: Int = 21
    private var seedArray: IntArray = IntArray(56)

    constructor(seed:Int) {
        var ii:Int = 0
        var mj:Int = 0
        var mk:Int = 0

        var subtraction:Int = seed.absoluteValue
        mj = MSEED - subtraction;
        seedArray[55] = mj;
        mk = 1;
        for (i in  1 until 55) {
            ii = (21 * i) % 55;
            seedArray[ii] = mk;
            mk = mj - mk;
            if (mk < 0) mk += MBIG;
            mj = seedArray[ii];
        }
        for (k in 1 until 5) {
            for (i in 1 until 56) {
                seedArray[i] -= seedArray[1 + (i + 30) % 55];
                if (seedArray[i] < 0) seedArray[i] += MBIG;
            }
        }
    }

    fun sample():Double {
        return (internalSample() * (1.0 / MBIG));
    }

    fun internalSample():Int {
        var retVal = 0;
        var locINext = inext;
        var locINextp = inextp;

        if (++locINext >= 56) locINext = 1;
        if (++locINextp >= 56) locINextp = 1;

        retVal = seedArray[locINext] - seedArray[locINextp];

        if (retVal == MBIG) retVal--;
        if (retVal < 0) retVal += MBIG;

        seedArray[locINext] = retVal;

        inext = locINext;
        inextp = locINextp;

        return retVal;
    }

    fun nextDouble():Double {
        return sample();
    }

    fun nextFloat():Float {
        return sample().toFloat();
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private var started = false;
    private var rand = Random2(0);
    private var width = 0
    private var height = 0
    private val handler:Handler = Handler()
    private val max = 600;
    private var processed = 0;
    private var prevTime:Long = 0L;
    private var prevCount = 0
    private var accum = 0.0
    private var accumN = 0

    fun loop(){
        val now = System.currentTimeMillis()

        //I have 16ms to put as much as possible into the current frame
        while (System.currentTimeMillis() - now < 16) {

            val label = TextView(this)
            label.text = "Dope"
            label.rotation = rand.nextFloat() * 360f
            label.translationX = rand.nextFloat() * width
            label.translationY = rand.nextFloat() * height
            label.setTextColor(
                Color.rgb(
                    (rand.nextDouble() * 255).toInt(),
                    (rand.nextDouble() * 255).toInt(),
                    (rand.nextDouble() * 255).toInt()
                )
            )

            if (processed > max) {
                relativeLayout.removeViewAt(0)

                if (prevTime == 0L) {
                    prevTime = System.currentTimeMillis()
                    prevCount = processed;
                }

                val diff = System.currentTimeMillis() - prevTime

                if (diff > 500) {
                    var value = (processed - prevCount) / diff.toDouble() * 1000.0;
                    dopesLabel.text = String.format("%.2f Dopes/s", value);

                    accum += value;
                    accumN++;

                    prevTime = System.currentTimeMillis()
                    prevCount = processed;
                }
            }

            relativeLayout.addView(label)

            processed++;
        }

        if (started) handler.post(::loop);
        else dopesLabel.text = String.format("%.2f  Dopes/s (AVG", accum / accumN)
    }

    fun startTest(){
        rand = Random2(0);
        width = relativeLayout.width
        height = relativeLayout.height
        processed = 0;
        started = true
        prevTime = 0L;
        prevCount = 0
        accum = 0.0
        accumN = 0
        handler.post(::loop);
    }

    fun buttonClick(view: View) {
        var button = (view as Button);
        if (!started) {
            button.text = "@ Stop"
            button.setBackgroundColor(Color.RED)
            dopesLabel.text = "Warming up.."
            dopesLabel.visibility = View.VISIBLE;
            dopesLabel.bringToFront()
            relativeLayout.removeAllViews()
            startTest();
        } else {
            button.text = "@ Start"
            button.setBackgroundColor(Color.rgb(0,200, 0))
            started = false
        }
    }
}