package com.example.chatapplication.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var imageCapture: ImageCapture
    private lateinit var imgCaptureExecutor: ExecutorService
    private lateinit var selectPhoto: AppCompatImageButton
    private val PICK_IMAGE_REQUEST = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        val takePhoto: AppCompatImageButton = findViewById(R.id.camera_btn)
        selectPhoto = findViewById(R.id.selectImage)

        var actionBar = getSupportActionBar()

        actionBar!!.setDisplayHomeAsUpEnabled(true)


        val cameraProviderResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    startCamera()
                } else {
                    Toast.makeText(this, "Some Text", Toast.LENGTH_LONG).show()
                }
            }
        cameraProviderResult.launch(android.Manifest.permission.CAMERA)


        takePhoto.setOnClickListener {
            captureImage()
        }

        selectPhoto.setOnClickListener {
            Dexter.withContext(this)
                .withPermissions(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(result: MultiplePermissionsReport?) {
                        result?.let {
                            if(result.areAllPermissionsGranted()){
                                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                resultLauncher.launch(intent)
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showDialogPermissions()
                    }

                })
                .onSameThread().check()

        }


    }

    @SuppressLint("SuspiciousIndentation")
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                val  imagePath = result.data?.data
                println("Get Image from file : $imagePath")

                val intent = Intent()
                intent.putExtra("ImageURI", imagePath.toString())
                setResult(RESULT_OK, intent)
                finish()

            }

        }

    private fun captureImage() {

        imageCapture?.let {
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        //Toast.makeText(this@MainActivity, "ImageSaved", Toast.LENGTH_LONG).show()
                        Log.d("Camera Example", "Image Saved at ${file.toURI()}")
                        val intent = Intent()
                        intent.putExtra("ImageURI", file.toURI().toString())
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            this@ImagePreviewActivity,
                            "Error while taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    private fun startCamera() {
        val preview: androidx.camera.view.PreviewView = findViewById(R.id.camera_preview)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(preview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()// assure e that there wlll only one listener
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d("CameraExample", "Exception=" + e.message)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    fun showDialogPermissions(){
        AlertDialog.Builder(this).setMessage("You Need to allow permission required for running these features.You can enable it under application settings")
            .setPositiveButton("Go to Settings"){
                    dialog, which
                ->

                try{
                    val intent =Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)

                }
                catch(e:java.lang.Exception){
                    e.printStackTrace()

                }

            }
            .setNegativeButton("Cancel"){
                    dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}