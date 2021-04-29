package com.droisys.htmleditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.ByteArrayOutputStream


class SignaturePad : AppCompatActivity() {

    lateinit var mSignaturePad:SignaturePad;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_pad)

        mSignaturePad = findViewById(R.id.signature_pad)

        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                //Event triggered when the pad is signed

            }

            override fun onClear() {
                //Event triggered when the pad is cleared
            }
        })

    }

    fun bmToB64(bitmap: Bitmap): String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

//    override fun onBackPressed() {
//        val uri = bmToB64(mSignaturePad.signatureBitmap)
////        val uri = getImageUri(this, mSignaturePad.signatureBitmap)
//        val intent = Intent()
//        intent.putExtra("uri", "data:image/jpeg;base64,$uri")
//        setResult(Activity.RESULT_OK, intent)
//
//        super.onBackPressed()
//    }

    fun onClick(view: View) {
        when(view.id){
            R.id.btnCancel-> { finish()}
            R.id.btnConfirm-> {
                val uri = bmToB64(mSignaturePad.signatureBitmap)
//        val uri = getImageUri(this, mSignaturePad.signatureBitmap)
                val intent = Intent()
                intent.putExtra("uri", "data:image/jpeg;base64,$uri")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}