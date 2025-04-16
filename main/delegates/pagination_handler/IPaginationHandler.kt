
interface IPaginationHandler {
    var currentPage: Int
    var onRequest: Boolean

    fun resetPage()
    fun cantLoadAgain(): Boolean
    fun <T> List<T>.increasePage(limit: Int = 0)
    fun alreadyEndOfList()

    fun onLoadMore(
        scope: LifecycleCoroutineScope?,
        rv: RecyclerView,
        loadMoreAction: () -> Unit,
        cantLoadMoreAction: (show: Boolean) -> Unit,
        extendsView: List<View>
    )

    fun onLoadMore(
        scope: LifecycleCoroutineScope?,
        rv: RecyclerView,
        loadMoreAction: () -> Unit,
        cantLoadMoreAction: (show: Boolean) -> Unit,
        extendView: View?=null
    )

    fun showToast(context: Context, message: String, isShow: Boolean)
}