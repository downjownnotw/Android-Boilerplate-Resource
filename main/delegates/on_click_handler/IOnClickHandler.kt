
interface IOnClickHandler {
    companion object{
        const val ZeroInterval: Long = 0
        const val DefaultInterval: Long = 1000
    }

    fun View.onShowDialog(
        dialog: DialogFragment,
        fragmentManager: FragmentManager,
        tag: String,
        preShowAction: (()->Unit)?=null,
        postShowAction: (()->Unit)?=null
    )

    fun View.onMultipleClick(
        onSafeClick: (View) -> Unit,
        interval: Long = DefaultInterval
    )

}