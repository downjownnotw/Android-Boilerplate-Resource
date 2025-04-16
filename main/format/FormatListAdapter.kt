#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ${PACKAGE_NAME}.${NAME}.${View_Holder_name}

class ${NAME}(
    override val context: Context,
    _list: List<${List_Item_name}> = emptyList()
): BaseListAdapter<${List_Item_name}, ${View_Holder_name}>(_list, Callback) {

    private object Callback: DiffUtil.ItemCallback<${List_Item_name}>() {
        override fun areItemsTheSame(
            oldItem: ${List_Item_name},
            newItem: ${List_Item_name}
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ${List_Item_name},
            newItem: ${List_Item_name}
        ): Boolean {
        // need compare specifics parameter here …
        // for Example:  return oldItem.id == newItem.id
        return oldItem == newItem
        }
    }

    inner class ${View_Holder_name} (
        private val binding: ${Binding_name}
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ${List_Item_name}) = with(binding) {
        // declare component binding here …
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ${View_Holder_name}(
        ${Binding_name}.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
        )
    )

    override fun onBindViewHolder(holder: ${View_Holder_name}, position: Int) {
        return holder.bind(list[position])
    }

}
