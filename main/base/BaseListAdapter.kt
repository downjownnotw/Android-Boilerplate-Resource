#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<T, VH: RecyclerView.ViewHolder>(
    _list: List<T> = emptyList(),
    callback: DiffUtil.ItemCallback<T>
): ListAdapter<T, VH>(callback) {

    abstract val context: Context

    var list: List<T>
        get() = currentList
        set(value) = submitList(value)

    init {
        list = _list
    }

    open var onItemClickListener: ((data: T)->Unit)?=null

    /*
    * set current list as empty when current list not empty
    * setup current list with new list
    * condition: max list count is fix
    * */
    open fun resetList(newList: List<T>?=null){
        if (newList == null){
            submitList(emptyList())
            return
        }
        if (currentList.isNotEmpty()) submitList(emptyList())
        submitList(newList)
    }

    /*
    * Add current list with new list
    * condition: load more list
    * */
    open fun updateList(newList: List<T>){
        val myList = currentList.toMutableList()
        myList.addAll(newList)
        submitList(myList.toList())
    }

}