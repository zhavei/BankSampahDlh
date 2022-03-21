package com.greenTangerangkota.banksampahdlh

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.greenTangerangkota.banksampahdlh.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val url = "https://green.tangerangkota.go.id/banksampahdroid/"
    private var currentUrl = ""
    private val FILECHOOSER_RESULTCODE = 190
    private var mInterstitialAd: InterstitialAd? = null
    private var interAdRequest: AdRequest? = null
    private var progressDialog: ProgressDialog? = null
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var prefManager: PrefManager? = null
    private var mAdIsLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        prefManager = PrefManager(baseContext)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Sedang Loading...")

        binding.fab.setOnClickListener {

            sendWhatsappMessage(
                "+628111631631",
                "Halo Admin Bank Sampah Kota Tangerang, Saya admin Bank Sampah : "
            )
        }


    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(url: String){

        binding.contentMain.webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                prefManager?.addVar()
                if (mI)
            }
        }


    }

    private fun isOnline(): Boolean{
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                return
            }
            val dataString = intent.dataString
            mUploadMessage!!.onReceiveValue(arrayOf(Uri.parse(dataString)))
            mUploadMessage = null
        }
    }

    /**
     * sendWhatsappMessage send message on number on click icon phone
     */
    private fun sendWhatsappMessage(phoneNumber: String, textInside: String) {
        val url = if (Intent().setPackage("com.whatsapp").resolveActivity(packageManager) != null) {
            "whatsapp://send?text=Hello&phone=$phoneNumber"
        } else {
            "https://api.whatsapp.com/send?phone=$phoneNumber&text=$textInside"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    /**
     * onBackPressed is backpress exit
     */
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "klik 2 kali untuk keluar", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

}