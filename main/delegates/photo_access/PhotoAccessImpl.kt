
class PhotoAccessImpl: IPhotoAccess {
    override var onBackActive: Boolean = true

    override var tempUri: ((Uri) -> Unit)? = null
    override var takePhotoTemp: Uri? = null
    override var takePhotoListener: ActivityResultLauncher<Uri>? = null
    override var selectPhotoListener: ActivityResultLauncher<String>? = null

    override val dir: String = "image/*"

    override fun Fragment.takePhotoListenerAction(isSuccess: Boolean) {
        if (isSuccess) {
            takePhotoTemp?.let { uri-> tempUri?.invoke(uri) }
            return
        }
        // remove temp file when not success take photo
        takePhotoTemp?.path?.let {path->
            val file = requireContext().getExternalFilesDir(path.removePrefix("/files"))
            if (file?.exists() == true) file.delete()
        }
    }
    override fun selectPhotoListenerActionSingle(uri: Uri?) {
        uri?.let { tempUri?.invoke(uri) }
    }
    override fun selectPhotoListenerActionMultiple(uriList: List<Uri>) {
        uriList.forEach { uri-> selectPhotoListenerActionSingle(uri) }
    }
    override fun Fragment.destroyViewPhotoAccess() {
        HandleInternalStorage.clearCacheAndFiles(requireContext(), viewLifecycleOwner.lifecycleScope)
        takePhotoListener?.unregister()
        selectPhotoListener?.unregister()
    }
}