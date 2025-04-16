#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import com.google.gson.annotations.SerializedName

data class ${NAME}(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: ${Data_name},
    @SerializedName("code") val code: Int,
    @SerializedName("accessTime") val accessTime: String
)
