
class OnClickHandlerImpl: IOnClickHandler {
    companion object{
        private var lastTimeClicked: Long = ZeroInterval
    }

    inner class SafeClickListener(
        private var defaultInterval: Long = DefaultInterval,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        override fun onClick(v: View) {
            val timeClicked = SystemClock.elapsedRealtime()
            if ((timeClicked - lastTimeClicked) < defaultInterval) return
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    override fun View.onShowDialog(
        dialog: DialogFragment,
        fragmentManager: FragmentManager,
        tag: String,
        preShowAction: (()->Unit)?,
        postShowAction: (()->Unit)?
    ){
        onMultipleClick({
            preShowAction?.invoke()
            dialog.show(fragmentManager, tag)
            postShowAction?.invoke()
        })
    }

    override fun View.onMultipleClick(
        onSafeClick: (View) -> Unit,
        interval: Long
    ) {
        val safeClickListener = SafeClickListener(interval) {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

}