#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleCoroutineScope
import ${PACKAGE_NAME}.ApplicationController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseBottomDialogStatic<V: ViewDataBinding>(context: Context) {

    private val dialog = (context.applicationContext as ApplicationController).setDialogBottomDialogStatic(context)

    private var _binding: V? = null
    val binding get() = _binding!!

    var isCanceling = false
    var onExpanded = false
    var onDismiss: (()->Unit)?=null

    abstract fun setupBinding(): V

    open val dialogLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    open fun initView(){
        /**
         * @param _binding declare from setupBinding()
         * @param _binding will reset when onDismissListener
         * */
        _binding = setupBinding()
        updateCancelable(isCanceling)
        dialog.setContentView(binding.apply { bindView() }.root)
        if (onExpanded) {
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.isDraggable = false
        }
        dialog.show()
        dialog.setOnDismissListener {
            _binding?.dismissView()
            _binding = null
        }
    }

    open fun V.bindView(){ }

    open fun V.dismissView(){ }

    open fun closeView(){
        dialog.dismiss()
    }

    open fun closeView(thread: LifecycleCoroutineScope?=null, closeAction: (()->Unit)?=null){
        closeView()
        thread?.launch(Dispatchers.Main){
            delay(500)
            closeAction?.invoke()
        }
    }

    open fun updateCancelable(isCanceling: Boolean){
        dialog.setCancelable(isCanceling)
        dialog.setCanceledOnTouchOutside(isCanceling)
    }

    open fun decorView() = dialog.window?.decorView

    open fun dialog(): BottomSheetDialog? = try { dialog } catch (_: Exception){ null }

}
