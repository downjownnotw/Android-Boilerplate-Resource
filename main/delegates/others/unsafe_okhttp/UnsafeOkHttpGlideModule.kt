
@GlideModule
class UnsafeOkHttpGlideModule : AppGlideModule() {
    private val timeoutSeconds = 10L // timeout 10 seconds
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .build()
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        )
    }
}
