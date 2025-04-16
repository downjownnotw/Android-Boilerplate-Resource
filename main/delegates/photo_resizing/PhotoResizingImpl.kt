
class PhotoResizingImpl: IPhotoResizing {
    private val extension = ".jpeg"
    private val filename = "photo_"
    private val authority = "${BuildConfig.APPLICATION_ID}.provider"
    private val byte: Long = 1024
    private val max = 2000

    override var specificFileName: String = ""

    companion object{
        private const val TAG = "TAG_PhotoResizing"
    }

    override suspend fun Context.createTempUri(): Uri? {
        return try {
            val tempFile: File = withContext(Dispatchers.Main){ create(this@createTempUri) }
            withContext(Dispatchers.IO){ FileProvider.getUriForFile(applicationContext, authority, tempFile) }
        } catch (_: Exception){ null }
    }

    override suspend fun convertBitmapToFile(context: Context, bitmap: Bitmap): File {
        val output = withContext(Dispatchers.Main){ create(context) }
        try {
            val stream: OutputStream = withContext(Dispatchers.IO){ FileOutputStream(output) }
            bitmap.compress(/* format = */ JPEG, /* quality = */ 10, /* stream = */ stream)
            withContext(Dispatchers.IO){
                stream.flush()
                stream.close()
            }
        } catch (_: Exception) { }
        bitmap.recycle()
        return setupMaxFileSizeUnder2MB(context, output)
    }

    private fun isSizeMore2MB(fileLength: Long): Boolean{
        val size = fileLength / byte
        log(TAG, "isSizeMore2MB: $size Kb <> max: $max Kb")
        return size > max
    }

    private fun create(context: Context): File =
        File.createTempFile(
            filename+specificFileName,
            extension,
            context.getExternalFilesDir("")
        )

    override fun loadFileOnDevice(context: Context, uri: Uri): File {
        return File("${context.getExternalFilesDir("")}/${loadFilenameFromUri(context, uri)}")
    }

    private fun loadFilenameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use {cursor->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
            cursor.close()
        }
        return fileName
    }

    override suspend fun createFileToUnder2MBAndJpeg(context: Context, photo: Uri): File {
        val initUriToFile = withContext(Dispatchers.IO){ loadFileFromUri(context, photo) }
        log(TAG, "createFileToUnder2MBAndJpeg initUriToFile: $initUriToFile")
        val outputFile = withContext(Dispatchers.IO){ setupMaxFileSizeUnder2MB(context, initUriToFile) }
        log(TAG, "createFileToUnder2MBAndJpeg outputFile: $outputFile")
        return outputFile
    }

    override fun deleteSpecificsFile(context: Context, file: File) {
        if (file.exists()) file.delete()
    }

    private fun loadFileFromUri(context: Context, uri: Uri): File{
        val fileOnDevice = loadFileOnDevice(context, uri)
        if (!fileOnDevice.exists()){
            val createFileFromUri = create(context)
            val out: OutputStream = FileOutputStream(createFileFromUri)
            val buf = ByteArray(byte.toInt())
            var len: Int
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                while (inputStream.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                inputStream.close()
            }
            log(TAG, "createFileFromUri: $createFileFromUri")
            return createFileFromUri
        }
        log(TAG, "fileOnDevice: $fileOnDevice")
        return fileOnDevice
    }

    private suspend fun setupMaxFileSizeUnder2MB(context: Context, inputFile: File): File{
        if (!isSizeMore2MB(inputFile.length())) return inputFile
        val outputFile: File = withContext(Dispatchers.Main){ create(context) }
        val (width, height) = getResolutionPhotoLimitedByDeviceResolution(context, inputFile)
        log(TAG, "setupMaxFileSizeUnder2MB inputFile: $inputFile")
        Compressor.compress(context, inputFile){
            resolution(width, height)
            quality(100) // Set the desired quality (0-100)
            format(Bitmap.CompressFormat.JPEG) // Set the output format
            size(max * byte) // Set the target size limit 2 MB
            destination(outputFile) // Save the compressed image to the output file
        }
        deleteSpecificsFile(context, inputFile) // Delete oldest file
        log(TAG, "setupMaxFileSizeUnder2MB outputFile: $outputFile")
        return setupMaxFileSizeUnder2MB(context, outputFile)
    }

    private fun getResolutionPhotoLimitedByDeviceResolution(context: Context, file: File): Pair<Int, Int> {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Decode the source file to get its dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        val sourceWidth = options.outWidth
        val sourceHeight = options.outHeight
        log(TAG, "getCurrentFileResolution source: $sourceWidth, $sourceHeight")

        // Calculate the scale factor
        val widthScale = screenWidth.toFloat() / sourceWidth
        val heightScale = screenHeight.toFloat() / sourceHeight
        val scale = if (widthScale < heightScale) widthScale else heightScale

        // Calculate the target dimensions
        val targetWidth = (sourceWidth * scale).toInt()
        val targetHeight = (sourceHeight * scale).toInt()
        log(TAG, "getCurrentFileResolution target: $targetWidth, $targetHeight")

        return Pair(targetWidth, targetHeight)
    }
}