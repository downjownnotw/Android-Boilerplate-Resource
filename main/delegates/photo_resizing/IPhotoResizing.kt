
interface IPhotoResizing {
    var specificFileName: String

    suspend fun Context.createTempUri(): Uri?
    suspend fun convertBitmapToFile(context: Context, bitmap: Bitmap): File
    fun loadFileOnDevice(context: Context, uri: Uri): File
    suspend fun createFileToUnder2MBAndJpeg(context: Context, photo: Uri): File
    fun deleteSpecificsFile(context: Context, file: File)
}