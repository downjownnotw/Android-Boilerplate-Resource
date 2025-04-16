
interface IPhotoAccess {
    var onBackActive: Boolean

    var tempUri: ((Uri)->Unit)?
    var takePhotoTemp: Uri?
    var takePhotoListener: ActivityResultLauncher<Uri>?
    var selectPhotoListener: ActivityResultLauncher<String>?

    val dir: String

    fun Fragment.takePhotoListenerAction(isSuccess: Boolean)
    fun selectPhotoListenerActionSingle(uri: Uri?)
    fun selectPhotoListenerActionMultiple(uriList: List<Uri>)
    fun Fragment.destroyViewPhotoAccess()
}