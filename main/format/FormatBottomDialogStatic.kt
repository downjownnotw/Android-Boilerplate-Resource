#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import android.content.Context
import android.view.LayoutInflater

class ${NAME}(
    private val context: Context
): BaseBottomDialogStatic<${LAYOUT_BINDING}>(context) {

    override fun setupBinding(): ${LAYOUT_BINDING} {
        return ${LAYOUT_BINDING}.inflate(LayoutInflater.from(context))
    }

    fun setup() {
        initView()
    }

    override fun ${LAYOUT_BINDING}.bindView() {
        // declare component binding here …
    }

}
