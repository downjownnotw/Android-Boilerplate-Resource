
class PaginationHandlerImpl: IPaginationHandler {
    override var currentPage: Int = 1
    override var onRequest: Boolean = false

    override fun resetPage() {
        currentPage = 1
    }

    override fun cantLoadAgain(): Boolean = currentPage == 0

    override fun <T> List<T>.increasePage(limit: Int) {
        currentPage += 1
        if (isEmpty()) alreadyEndOfList()
        if (limit > 0 && size < limit) alreadyEndOfList() // condition when limit are set (value more than 0)
    }

    override fun alreadyEndOfList(){
        currentPage = 0
    }

    override fun onLoadMore(
        scope: LifecycleCoroutineScope?,
        rv: RecyclerView,
        loadMoreAction: () -> Unit,
        cantLoadMoreAction: (show: Boolean) -> Unit,
        extendsView: List<View>
    ) {
        /* Load More Page */
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            private var searchJob: Job? = null
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val view = recyclerView.getChildAt(recyclerView.childCount - 1)
                val difference: Int = view.bottom - (recyclerView.height + recyclerView.scrollY)
                if (difference <= 0) {
                    val showCantLoadMoreCondition = extendsView.showCantLoadMoreCondition(difference, newState)
                    if (!showCantLoadMoreCondition) return
                    cantLoadMoreAction.invoke(cantLoadAgain()) // handle some stuck UI onLoadMore (check by value cantLoadAgain())
                    when{
                        cantLoadAgain() -> {
                            searchJob?.cancel()
                            searchJob = scope?.launch{
                                delay(1000)
                                cantLoadMoreAction.invoke(false)
                            }
                        }
                        else -> loadMoreAction.invoke()
                    }
                }
            }
        })
    }

    override fun onLoadMore(
        scope: LifecycleCoroutineScope?,
        rv: RecyclerView,
        loadMoreAction: ()->Unit,
        cantLoadMoreAction: (show: Boolean)->Unit,
        extendView: View?
    ){
        var extendsView = emptyList<View>()
        extendView?.let { extendsView = listOf(it) }
        return onLoadMore(scope, rv, loadMoreAction, cantLoadMoreAction, extendsView)
    }

    private fun List<View>.showCantLoadMoreCondition(difference: Int, newState: Int): Boolean{
        if (isEmpty()) return true
        val height = sumOf { it.height }
        val onNotScrolled = ((difference * -1) <= height)
        val onScrolledUp = (newState == 0)
        return onNotScrolled && onScrolledUp
    }

    private var currentToast: Toast? = null
    override fun showToast(context: Context, message: String, isShow: Boolean){
        // If a Toast is already being displayed, cancel it
        currentToast?.cancel()

        // Show the new Toast
        if (isShow) currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT).apply {
            show()
        }
    }

}