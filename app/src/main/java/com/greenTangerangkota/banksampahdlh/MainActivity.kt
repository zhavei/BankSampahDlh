package com.greenTangerangkota.banksampahdlh

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.greenTangerangkota.banksampahdlh.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val url = "https://green.tangerangkota.go.id/banksampahdroid/"
    private val urlTester = "https://zapyatransfer.com"
    private var currentUrl = ""
    private val FILECHOOSER_RESULTCODE = 190

    //private var mInterstitialAd: InterstitialAd? = null
    //private var interAdRequest: AdRequest? = null
    private var progressDialog: ProgressDialog? = null
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    private var prefManager: PrefManager? = null
    private var mAdIsLoading: Boolean = false

    @RequiresApi(api = Build.VERSION_CODES.P)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.hide()

        prefManager = PrefManager(baseContext)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Sedang Loading...")



        binding.fab.setOnClickListener {

            sendWhatsappMessage(
                "+6285715018087",
                "Halo Admin Bank Sampah Kota Tangerang, Saya admin Bank Sampah : "
            )
        }

        if (isOnline()) {
            binding.contentMain.llError.rlContent.visibility = View.GONE
            binding.contentMain.webView.visibility = View.VISIBLE
            startWebView(url)
        } else {
            binding.contentMain.llError.rlContent.visibility = View.VISIBLE
            binding.contentMain.webView.visibility = View.GONE
            if (progressDialog != null && progressDialog?.isShowing == true)
                progressDialog?.dismiss()
        }


    }

    //backpressed on webview not quit call this nice
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK && binding.contentMain.webView.canGoBack()) {
//            binding.contentMain.webView.goBack()
//            Toast.makeText(this, "kembali", Toast.LENGTH_SHORT).show()
//            return true
//        } else {
//            isOnline()
//            onBackPressed()
//            return false
////            startWebView(url)
////            onBackPressed()
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> if (binding.contentMain.webView.canGoBack()) {
                    binding.contentMain.webView.goBack()
                    Toast.makeText(this, "kembali", Toast.LENGTH_SHORT).show()
                } else {
                    //onBackPressed()
                    return false
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    /** private fun loadInterAds() {
    var interAdRequest = AdRequest.Builder().build()

    InterstitialAd.load(
    this, getString(R.string.interstitial_full_screen), interAdRequest,
    object : InterstitialAdLoadCallback() {
    override fun onAdFailedToLoad(adError: LoadAdError) {
    mInterstitialAd = null
    mAdIsLoading = false
    }

    override fun onAdLoaded(interstitialAd: InterstitialAd) {
    mInterstitialAd = interstitialAd
    mAdIsLoading = false
    }
    }
    )
    }

    private fun showInterAds(url: String) {
    if (mInterstitialAd != null) {
    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
    override fun onAdDismissedFullScreenContent() {
    binding.contentMain.webView.loadUrl(url)
    loadInterAds()
    }

    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {

    }

    override fun onAdShowedFullScreenContent() {
    mInterstitialAd = null;
    }
    }
    mInterstitialAd?.show(this)
        } else {
            binding.contentMain.webView.loadUrl(url)
        }
    } **/


    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(url: String) {


        binding.contentMain.webView.webViewClient = object : WebViewClient() {

//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                prefManager
//                if (mInterstitialAd != null && prefManager?.tVar?.rem(PrefManager.ADS_SHOW_TIME) == 0) {
//                    view?.loadUrl(url.toString())
//                } else{
//                    view?.loadUrl(url.toString())
//                }
//                return true
//            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                prefManager
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                try {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        prefManager!! // here added .addVar
                        progressDialog = null
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (progressDialog != null && progressDialog?.isShowing == false) progressDialog?.dismiss()
            }

        }

        binding.contentMain.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mUploadMessage?.onReceiveValue(null)
                mUploadMessage = filePathCallback
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(i, "Pilih File"),
                    FILECHOOSER_RESULTCODE
                )
                println(" logged open file image")
                return true
            }


        }

        //other webView options
        binding.contentMain.webView.settings.javaScriptEnabled = true
        binding.contentMain.webView.settings.allowFileAccess = true
        binding.contentMain.webView.settings.cacheMode = WebSettings.LOAD_NORMAL //new added
        binding.contentMain.webView.settings.allowContentAccess = true //new added
        binding.contentMain.webView.settings.setAppCacheEnabled(true) //new added
        binding.contentMain.webView.settings.domStorageEnabled = true

        //opther webView options
        binding.contentMain.webView.settings.loadWithOverviewMode = true
        binding.contentMain.webView.settings.useWideViewPort = true
        binding.contentMain.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        binding.contentMain.webView.isScrollbarFadingEnabled = false
        binding.contentMain.webView.settings.builtInZoomControls = false

        //still dint worked
        binding.contentMain.webView.settings.setAppCachePath(applicationContext.filesDir.absolutePath + "/cache")
        // binding.contentMain.webView.settings.databaseEnabled(applicationContext.filesDir.absolutePath + "/databases")

        //another im added webview options
        binding.contentMain.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.contentMain.webView.settings.databaseEnabled = true
        binding.contentMain.webView.requestFocus()
        binding.contentMain.webView.settings.databasePath
        // but seems didnt worked

        binding.contentMain.webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        })

        currentUrl = url
        binding.contentMain.webView.loadUrl(url)
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
        Toast.makeText(this, "klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
            doubleBackToExitPressedOnce = false
        }, 5000)
    }

}

