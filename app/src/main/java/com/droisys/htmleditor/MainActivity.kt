package com.droisys.htmleditor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var m_downX = 0f
    private lateinit var progressBar: ProgressBar

    val STORAGE_PERMISSION_CODE = 123
    private val FILECHOOSER_RESULTCODE = 1
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
//    private val READ_STORAGE_PERMISSION_REQUEST_CODE: Int = 100
//    private var mFilePathCallback: ValueCallback<Array<Uri?>?> = ValueCallback { }
//    private lateinit var uploadMessage: ValueCallback<Uri?>
//    private lateinit var webView: WebView
//    private val FILE_CHOOSER_RESULT_CODE = 1
//    private lateinit var mCameraPhotoPath: String
//
////    var webView: WebView? = null
//    private var m_downX = 0f
//    private val progressBar: ProgressBar? = null
//
//    private val STORAGE_PERMISSION_CODE = 123
//    private val FILECHOOSER_RESULTCODE = 1
//    private var mUploadMessage: ValueCallback<Array<Uri>>? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        getInternetStatus()
        webView.loadUrl("file:///android_asset/doc.html")
        initWebView()
//        val settings = webView.settings
//        settings.domStorageEnabled = true
//        settings.javaScriptEnabled = true
//        settings.domStorageEnabled = true
//        settings.loadWithOverviewMode = true
//        settings.allowFileAccess = true
////        settings.useWideViewPort = true
////        settings.builtInZoomControls = true
////        settings.displayZoomControls = false
////        settings.setSupportZoom(true)
//        settings.defaultTextEncodingName = "utf-8"
//        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(
//                    view: WebView,
//                    request: WebResourceRequest
//            ): Boolean {
//                view.loadUrl(request.url.toString())
////                view.clearHistory()
//                return false
//            }
//        }
//        webView.setWebChromeClient(object : WebChromeClient() {
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri?>?>, fileChooserParams: FileChooserParams): Boolean {
//                val intent = fileChooserParams.createIntent()
//                try {
//                    if(checkPermissionForReadExtertalStorage())
//                        startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
//                    else
//                        requestPermissionForReadExtertalStorage()
//                } catch (e: ActivityNotFoundException) {
//                    Toast.makeText(applicationContext, "Cannot open file chooser", Toast.LENGTH_LONG).show()
//                    return false
//                }
//                return false
//            }
//        })
//
//        webView.loadUrl("file:///android_asset/doc.html")

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode !== FILE_CHOOSER_RESULT_CODE || mFilePathCallback == null) {
//            super.onActivityResult(requestCode, resultCode, data)
//            return
//        }
//
//        var results: Array<Uri?>? = null
//
//        // Check that the response is a good one
//
//        // Check that the response is a good one
//        if (resultCode == Activity.RESULT_OK) {
//            if (data == null) {
//                // If there is not data, then we may have taken a photo
//                results = arrayOf(Uri.parse(mCameraPhotoPath))
//            } else {
//                val dataString: String? = data.dataString
//                if (dataString != null) {
//                    results = arrayOf(Uri.parse(dataString))
//                }
//            }
//        }
//
//        mFilePathCallback.onReceiveValue(results)
//        println("RESults:")
//        println(getPath(results?.get(0)))
//        mFilePathCallback = ValueCallback { }
//        webView.reload()
////        webView.loadUrl(getPath(results?.get(0)))
//    }
//
//    fun getPath(pickedUri: Uri?):String {
//        var imagePath = ""
//        val imgData = arrayOf(MediaStore.Images.Media.DATA)
//        val imgCursor: Cursor? = managedQuery(pickedUri, imgData, null, null, null)
//        imagePath = if (imgCursor != null) {
//            val index: Int = imgCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            imgCursor.moveToFirst()
//            imgCursor.getString(index)
//        } else pickedUri?.path!!
//        return imagePath
//    }
//
//    fun checkPermissionForReadExtertalStorage(): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val result: Int = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//            return result == PackageManager.PERMISSION_GRANTED
//        }
//        return false
//    }
//
//    @Throws(Exception::class)
//    fun requestPermissionForReadExtertalStorage() {
//        try {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    READ_STORAGE_PERMISSION_REQUEST_CODE)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }
//    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun initWebView() {
        webView.setWebChromeClient(MyWebChromeClient(this))
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                invalidateOptionsMenu()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                //                mySwipeRefreshLayout.setRefreshing(false);
                invalidateOptionsMenu()
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                progressBar.visibility = View.GONE
                //                mySwipeRefreshLayout.setRefreshing(false);
                invalidateOptionsMenu()
            }
        }
        webView.clearCache(true)
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.isHorizontalScrollBarEnabled = false
        webView.setOnTouchListener(OnTouchListener { v, event ->
            if (event.pointerCount > 1) {
                //Multi touch detected
                return@OnTouchListener true
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    // save the x
                    m_downX = event.x
                }
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {

                    // set x so that it doesn't move
                    event.setLocation(m_downX, event.y)
                }
            }
            false
        })
    }

    private class MyWebChromeClient(var context: Context) :
        WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            (context as MainActivity).mUploadMessage = filePathCallback
            (context as MainActivity).requestStoragePermission()
            return true
        }

    }

    //Requesting permission
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            openFileExplorer()
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileExplorer()
                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun openFileExplorer() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        this@MainActivity.startActivityForResult(
            Intent.createChooser(i, "File Chooser"),
            FILECHOOSER_RESULTCODE
        )
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            val result =
                if (intent == null || resultCode != RESULT_OK) null else intent.data
            if (result == null) {
                mUploadMessage?.onReceiveValue(null)
            } else {
                mUploadMessage?.onReceiveValue(arrayOf(result))
            }
            mUploadMessage = null
        }
    }

    private fun getInternetStatus(): Boolean {
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            //show no internet dialog
            val df = Df(this)
            df.showNoInternetDialog()
        }
        return isConnected
    }

   /* fun onClick(view: View) {
//        view as MaterialButton

        webView.evaluateJavascript(
            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"

            // code here
        ) { html -> Log.d("HTML_CODE", html) }
    }*/
}
