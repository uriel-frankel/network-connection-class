package com.facebook.network.connectionclass.sample

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.ConnectionClassManager.ConnectionClassStateChangeListener
import com.facebook.network.connectionclass.ConnectionQuality
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val TAG = "ConnectionClass-Sample"
    private var mConnectionClassManager: ConnectionClassManager? = null
    private var mDeviceBandwidthSampler: DeviceBandwidthSampler? = null
    private var mListener: ConnectionChangedListener? = null
    private var mTextView: TextView? = null
    private var mRunningBar: View? = null

    private val mURL = "https://source.unsplash.com/1600x900/?nature,water"
    private var mTries = 0
     var mConnectionClass: ConnectionQuality = ConnectionQuality.UNKNOWN
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mConnectionClassManager = ConnectionClassManager.getInstance()
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance()
        findViewById<View>(R.id.test_btn).setOnClickListener(testButtonClicked)
        mTextView = findViewById<View>(R.id.connection_class) as TextView
        mTextView!!.setText(mConnectionClassManager!!.getCurrentBandwidthQuality().toString())
        mRunningBar = findViewById(R.id.runningBar)
        mRunningBar!!.setVisibility(View.GONE)
        mListener = ConnectionChangedListener()
    }

    override fun onPause() {
        super.onPause()
        mConnectionClassManager!!.remove(mListener)
    }

    override fun onResume() {
        super.onResume()
        mConnectionClassManager!!.register(mListener)
    }

    /**
     * Listener to update the UI upon connectionclass change.
     */
    inner class ConnectionChangedListener : ConnectionClassStateChangeListener {
        override fun onBandwidthStateChange(bandwidthState: ConnectionQuality) {
            mConnectionClass = bandwidthState
            runOnUiThread(Runnable { mTextView!!.setText(mConnectionClass.toString()) })
        }
    }

    private val testButtonClicked = View.OnClickListener { DownloadImage().execute(mURL) }

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    inner class DownloadImage : AsyncTask<String?, Void?, Void?>() {
        override fun onPreExecute() {
            mDeviceBandwidthSampler!!.startSampling()
            mRunningBar!!.setVisibility(View.VISIBLE)
        }

         override fun doInBackground(vararg url: String?): Void? {
            val imageURL = url[0]
            try {
                // Open a stream to download the image from our URL.
                val connection = URL(imageURL).openConnection()
                connection.useCaches = false
                connection.connect()
                val input = connection.getInputStream()
                try {
                    val buffer = ByteArray(1024)

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error while downloading image.")
            }
            return null
        }

        protected override fun onPostExecute(v: Void?) {
            mDeviceBandwidthSampler!!.stopSampling()
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                mTries++
                DownloadImage().execute(mURL)
            }
            if (!mDeviceBandwidthSampler!!.isSampling()) {
                mRunningBar!!.setVisibility(View.GONE)
            }
        }


    }
}