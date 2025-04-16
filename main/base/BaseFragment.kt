#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseFragment<V: ViewDataBinding, VM: BaseViewModel>: Fragment() {
    @get:LayoutRes
    abstract val layout: Int

    private var _binding: V? = null
    val binding get() = _binding!!
    abstract fun getClassVM(): Class<VM>
    lateinit var viewModel: VM

    companion object{
        private const val TAG = "TAG_BaseF"
    }

    open fun V.createView() {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.apply {
            viewModel = ViewModelProvider(this@BaseFragment)[getClassVM()]
            lifecycleCoroutineScope()?.launch(Dispatchers.Main){
                FirebaseCrashlytics.getInstance().log(this@BaseFragment.javaClass.simpleName) // logging latest fragment accessed
                createView()
                findNavController().previousBackStackEntry?.let {

                }
            }
        }.root
    }

    abstract fun V.viewCreated()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerOnBackPressedCallback()
        binding.viewCreated()
    }

    open fun navigate(direction: NavDirections) = findNavController().navigate(direction)

    open suspend fun V.resumeJobScope(){}
    private var resumeJob: Job? = null
    override fun onResume() {
        super.onResume()
        resumeJob?.cancel()
        resumeJob = lifecycleCoroutineScope()?.launch(Dispatchers.Main){
            delay(500)
            binding.resumeJobScope()
        }
    }

    open fun V.destroyView() {}
    override fun onDestroyView() {
        viewModel.resetUnusedLiveData()
        binding.destroyView()
        unregisterOnBackPressedCallback()
        _binding = null
        super.onDestroyView()
    }

    open fun onBackPressAction(){ onDoubleBackPressHandler() }
    private var onBackPressedCallback: OnBackPressedCallback? = null
    open fun registerOnBackPressedCallback(){
        // call function on View Created lifecycle
        onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() { onBackPressAction() }
        }
        onBackPressedCallback?.let { requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it) }
    }
    open fun unregisterOnBackPressedCallback(){
        // call function on Destroy View lifecycle
        onBackPressedCallback?.isEnabled = false
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
    }

    var isDoubleClick = false
    open fun onDoubleBackPressHandler(){
        when(isDoubleClick){
            true -> {
                // minimized app
                val minimize = Intent(Intent.ACTION_MAIN)
                minimize.addCategory(Intent.CATEGORY_HOME)
                minimize.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(minimize)
            }
            false -> {
                isDoubleClick = true
                // add toast here …
                lifecycleScope.launch(Dispatchers.IO){
                    delay(2000)
                    isDoubleClick = false
                }
            }
        }
    }

    fun lifecycleCoroutineScope() = try { viewLifecycleOwner.lifecycleScope } catch (_: Exception){ null }

}
