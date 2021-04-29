package com.droisys.htmleditor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.*
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class MainActivity2 : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var etUserId: EditText
    lateinit var button: Button
    private var m_downX = 0f
    private var progressBar: ProgressBar? = null
    private var mUploadMessage: ValueCallback<Array<Uri>>? = null
    var isCodeVisible = false
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        etUserId = findViewById(R.id.etUserId)
        button = findViewById(R.id.button)
        webView.visibility = View.VISIBLE
        webView.loadUrl("file:///android_asset/doc.html")
        initWebView()

        etUserId.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                inflateHtml(htm, p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

    }

    private fun inflateHtml(htm: String, id: String) {
        webView.visibility = View.VISIBLE
        if (id.isNotEmpty()){
            var doc: Document? = null

            try{
                doc = Jsoup.parse(htm)
                val onclick = doc.getElementsByAttribute("onclick")
                onclick.forEach {
                    if (it.attr("onclick").contains(id)) {
                        it.removeAttr("disabled")
                        it.removeAttr("readonly")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            webView.loadData(doc?.html(), "text/html", "utf-8")
        }
        else{
            webView.loadUrl("file:///android_asset/doc.html")
        }
    }

    private fun updateSign(img: String, id: String) {
        if (id.isNotEmpty()){
            var doc: Document? = null

            try{
                doc = Jsoup.parse(htm)
                val onclick = doc.getElementsByAttribute("onclick")
                onclick.forEach {
                    if (it.attr("onclick").contains(id)) {
                        it.removeAttr("disabled")
                        it.removeAttr("readonly")

                        if (it.tagName() == "img"){
                            it.attr("src", img)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            webView.loadData(doc?.html(), "text/html", "utf-8")
        }
        else{
            webView.loadUrl("file:///android_asset/doc.html")
        }
    }

    //Requesting permission
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
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
        permissions: Array<String>,
        grantResults: IntArray
    ) {

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
        this.startActivityForResult(
            Intent.createChooser(i, "File Chooser"),
            FILECHOOSER_RESULTCODE
        )
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            val result =
                if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
            if (result == null) {
                mUploadMessage!!.onReceiveValue(null)
            } else {
                mUploadMessage!!.onReceiveValue(arrayOf(result))
            }
            mUploadMessage = null
        }
        else if(requestCode == 101){
            val uri = if (intent == null || resultCode != Activity.RESULT_OK) "" else intent.getStringExtra("uri")
//            mUploadMessage!!.onReceiveValue(arrayOf(Uri.parse(uri)))

            updateSign(uri, etUserId.text.toString())
//
        }
    }

    fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        }
    }

    override fun onBackPressed() {
        button.text = if (isCodeVisible) "Show HTML" else "Show Renderer"
        isCodeVisible = !isCodeVisible
        if (webView!!.canGoBack()) {
            webView!!.goBack()
            return
        }
        super.onBackPressed()
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webChromeClient = MyWebChromeClient(this)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                progressBar!!.visibility = View.VISIBLE
                invalidateOptionsMenu()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar!!.visibility = View.GONE
                  //                mySwipeRefreshLayout.setRefreshing(false);
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")

                invalidateOptionsMenu()
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                progressBar!!.visibility = View.GONE
                //                mySwipeRefreshLayout.setRefreshing(false);
                invalidateOptionsMenu()
            }
        }
        webView.clearCache(true)
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.isHorizontalScrollBarEnabled = false
//        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;
        webView.settings.domStorageEnabled = true;
        webView.addJavascriptInterface(LoadListener(this), "HTMLOUT")


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



    fun onClick(view: View) {
        when(view.id){
            R.id.button -> {
                button.text = if (isCodeVisible) "Show HTML" else "Show Renderer"
                if (!isCodeVisible) {
                    var html = htm
                    webView.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        ValueCallback<String> { s ->
                            html = s
                            Log.d("LogName", s) // Prints: "this"
                            html = html.replace("<", "&lt").replace(">", "&gt").replace("\\u003C","&lt")
                            val data =
                                "<pre style='overflow:scroll;width:100vw;height:100vh;word-wrap: break-word;white-space: pre-wrap;'><h1>HTML CODE</h1><br/>$html</pre>"
                            Log.d("data:", data)
                            webView.loadData(data, "text/html", "utf-8")
                            isCodeVisible = true
                        })
//            inflateHtml(htm,etUserId.text.toString())

                }
                else{
                    onBackPressed()
                    isCodeVisible = false
                }
            }
            R.id.buttonSign -> {
                startActivityForResult(Intent(this, SignaturePad::class.java), 101)
            }
        }
    }

    private inner class MyWebChromeClient(var context: Context) :
        WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            mUploadMessage = filePathCallback
            requestStoragePermission()
            return true
        }

    }

    //show no internet dialog
    val internetStatus: Boolean
        get() {
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

    companion object {
        private var htm: String = ""
        private const val STORAGE_PERMISSION_CODE = 123
        private const val FILECHOOSER_RESULTCODE = 1
        private fun log(msg: String, vararg vals: String) {
            println(String.format(msg, *vals))
        }
    }

    class LoadListener(mainActivity2: MainActivity2) {
        @JavascriptInterface
        fun processHTML(html: String) {
            Log.e("result", html)
            htm = html
        }
    }

}