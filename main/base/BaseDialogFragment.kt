#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ${PACKAGE_NAME}.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseDialogFragment<V: ViewDataBinding, VM: BaseViewModel>: DialogFragment() {
    @get:LayoutRes
    abstract val layout: Int

    private var _binding: V? = null
    val binding get() = _binding!!
    abstract fun getClassVM(): Class<VM>
    lateinit var viewModel: VM

    open var themeRes = R.style.DialogThemeNoMargin
    open var animation = R.style.DialogThemeNoSlideAnimation

    companion object{
        private const val TAG = "TAG_BaseDF"
    }

    open val onDialogFragmentBackPressAction: (()->Unit)?=null

    open val onDialogBackPressAction: ()->Unit = {
        closeDialog()
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), themeRes){
            override fun onBackPressed() {
                onDialogFragmentBackPressAction?.let {
                    it.invoke()
                    return
                }
                onDialogBackPressAction()
            }
        }
        dialog.window?.setWindowAnimations(
            if (savedInstanceState == null) animation
            else R.style.DialogThemeSlideAnimationRestore
        )
        return dialog
    }

    open fun V.createView() {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.apply {
            viewModel = ViewModelProvider(this@BaseDialogFragment)[getClassVM()]
            createView()
        }.root
    }

    abstract fun V.viewCreated()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewCreated()
    }

    private var jobOnCloseDialog: Job? = null
    private val scope = lifecycleScope
    open fun closeDialog(){
        jobOnCloseDialog?.cancel()
        jobOnCloseDialog = scope.launch(Dispatchers.Main){
            dismissAllowingStateLoss()
        }
    }

    open fun closeDialog(otherAction: (()->Unit)){
        jobOnCloseDialog?.cancel()
        jobOnCloseDialog = scope.launch(Dispatchers.Main){
            otherAction.invoke()
            dismissAllowingStateLoss()
        }
    }

    open fun closeDialog(otherAction: (()->Unit), delayTimeMillis: Long){
        jobOnCloseDialog?.cancel()
        jobOnCloseDialog = scope.launch(Dispatchers.Main){
            otherAction.invoke()
            delay(500)
            dismissAllowingStateLoss()
        }
    }

    open fun V.destroyView() {}
    override fun onDestroyView() {
        binding.destroyView()
        super.onDestroyView()
        closeDialog()
        _binding = null
    }
}
