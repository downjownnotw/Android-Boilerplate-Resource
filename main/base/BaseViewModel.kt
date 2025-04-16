#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ${PACKAGE_NAME}.ApplicationController

abstract class  ${NAME}(application: Application): AndroidViewModel(application){

    open fun resetUnusedLiveData(){ }

    fun loadDisposable() = getApplication<ApplicationController>().loadContainerRequest()

}
