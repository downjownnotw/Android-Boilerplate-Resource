
interface IBaseBottomSheetDialogFragment {
    fun lifecycleScope(): LifecycleCoroutineScope?

    fun BottomSheetBehavior<View>.createDialog()

    fun closeDialog()
    fun closeDialog(
        otherAction: suspend (CoroutineScope.()->Unit)
    )

    fun View.hideSoftKeyboard()
}