#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider

abstract class ${NAME}<V: ViewDataBinding, VM: BaseViewModel>: AppCompatActivity() {

    @get:LayoutRes
    abstract val layout: Int

    private var _binding: V? = null
    val binding get() = _binding!!
    abstract fun getClassVM(): Class<VM>
    lateinit var viewModel: VM

    companion object {
        private const val TAG = "TAG_BaseA"
    }

    open fun preCreate() {}
    open fun postCreate() {}
    open fun V.create() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layout)
        viewModel = ViewModelProvider(this)[getClassVM()]
        preCreate()
        setContentView(binding.apply {
            create()
        }.root)
        postCreate()
    }

    open fun V.destroy() {}
    override fun onDestroy() {
        binding.destroy()
        super.onDestroy()
        _binding = null
    }

}
