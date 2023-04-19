package com.livingtechusa.reflexion.util.json

import android.net.Uri
import java.io.File

data class ZipValues(
    val zipFile: File,
    val zipUri: Uri,
    val uriList: List<Uri>
)