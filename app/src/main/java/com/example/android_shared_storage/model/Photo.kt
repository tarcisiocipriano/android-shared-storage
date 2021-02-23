package com.example.android_shared_storage.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(val id: Long, val name: String, val uri: Uri): Parcelable
