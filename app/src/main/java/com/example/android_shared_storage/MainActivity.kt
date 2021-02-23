package com.example.android_shared_storage

import android.Manifest
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android_shared_storage.adapter.PhotoAdapter
import com.example.android_shared_storage.databinding.ActivityMainBinding
import com.example.android_shared_storage.model.Photo
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_CODE = 1
        const val REQUEST_DELETE_CODE = 2
    }

    private lateinit var binding: ActivityMainBinding

    private val adapter by lazy {
        PhotoAdapter { photo ->
            val intent = Intent(this, PhotoDetailsActivity::class.java)
            intent.putExtra("PHOTO", photo)
            startActivityForResult(intent, REQUEST_DELETE_CODE)
        }
    }

    private var photosList: ArrayList<Photo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()
        initUI()
        loadImages()
        updateList()
    }

    private fun initUI() {
        binding.apply {
            rvPhotos.adapter = adapter
            rvPhotos.layoutManager = GridLayoutManager(this@MainActivity, 2)
        }
    }

    private fun updateList() {
        adapter.submitList(photosList)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE,
        )
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission allowed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImages() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            photosList.clear()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)

                photosList.add(Photo(
                        id,
                        displayName,
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                ))

                Log.d("FILE", "Media ID: $id - displayName: $displayName")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && REQUEST_DELETE_CODE == requestCode) {
            Toast.makeText(this, "Foto excluída", Toast.LENGTH_LONG).show()
            loadImages()
            updateList()
        }
    }

}