
class ApplicationController : Application(), LifecycleObserver {
    private var recordedActivity: Activity? = null
    private var recordBottomDialogStatic: BottomSheetDialog? = null

    private var containerRequest: CompositeDisposable? = null

    companion object{
        private const val TAG = "TAG_Application"
    }

    /**
     * in onCreate -> initiate repo and show log only on debug version
     * setCurrentActivity for update current activity in apps
     * getCurrentActivity for return current activity
     * createAlertDialog for record builder alert dialog
     * getCurrentAlertDialog for return current showed alert dialog
     * */

    override fun onCreate() {
        super.onCreate()
        containerRequest = CompositeDisposable() // initialize containerRequest

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    fun setCurrentActivity(updatedActivity: Activity) {
        if (recordedActivity == null || recordedActivity != updatedActivity ){
            recordedActivity = updatedActivity
        }
    }

    fun getCurrentActivity(): Activity? {
        return recordedActivity
    }

    fun setDialogBottomDialogStatic(context: Context): BottomSheetDialog{
        try {
            recordBottomDialogStatic?.let {
                if (it.isShowing) it.dismiss()
                recordBottomDialogStatic = null
            }
        } catch (_: Exception){
            recordBottomDialogStatic = null
        }
        recordBottomDialogStatic = BottomSheetDialog(context, R.style.ThemeOverlay_TopRounded_BottomSheetDialog)
        return recordBottomDialogStatic!!
    }

    fun loadContainerRequest(): CompositeDisposable{
        if (containerRequest == null) {
            containerRequest = CompositeDisposable()
        }
        return containerRequest!!
    }
}