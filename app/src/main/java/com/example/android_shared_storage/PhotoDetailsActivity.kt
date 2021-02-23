package com.example.android_shared_storage

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.android_shared_storage.databinding.ActivityPhotoDetailsBinding
import com.example.android_shared_storage.model.Photo


const val REQUEST_DELETE_CODE = 2

class PhotoDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoDetailsBinding

    private lateinit var photo: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<Photo>("PHOTO")?.let { photo = it }

        photo.let { photoItem ->
            binding.apply {
                imgViewPhoto.setImageURI(photoItem.uri)

                btnRemove.setOnClickListener {
                    deleteImage(photoItem.uri)
                }
            }
        }

    }


    private fun deleteImage(uri: Uri) {
        try {
            contentResolver.delete( uri,
                    "${MediaStore.Images.Media._ID} = ?",
                    arrayOf(ContentUris.parseId(uri).toString())
            )
        } catch (securityException: SecurityException) {
            val recoverSecException = securityException as? RecoverableSecurityException ?: throw securityException
            val intentSender = recoverSecException.userAction.actionIntent.intentSender
            startIntentSenderForResult(intentSender, REQUEST_DELETE_CODE, null, 0, 0, 0, null) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_DELETE_CODE) {
            deleteImage(photo.uri)
            val returnIntent = Intent()
//            returnIntent.putExtra(MainActivity.REQUEST_DELETE_CODE, "ae")
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

}