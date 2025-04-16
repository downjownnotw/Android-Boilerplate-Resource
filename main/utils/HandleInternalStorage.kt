
object HandleInternalStorage {
    private const val TAG = "TAG_HandleInternalStorage"

    fun clearAll(context: Context, scope: LifecycleCoroutineScope){
        context.apply {
            scope.launch(Dispatchers.IO){
                val listDir = withContext(Dispatchers.IO){
                    listOf(cacheDir, filesDir, codeCacheDir, noBackupFilesDir, externalCacheDir)
                }
                container(listDir)
            }
        }
    }

    fun clearCacheAndFiles(context: Context, scope: LifecycleCoroutineScope){
        context.apply {
            scope.launch(Dispatchers.IO){
                val listDir = withContext(Dispatchers.IO){
                    listOf(cacheDir, filesDir, externalCacheDir, getExternalFilesDir(""))
                }
                container(listDir)
            }
        }
    }

    private suspend fun container(listDir: List<File?>){
        log(TAG, "running")
        for (i in listDir.indices) listDir[i]?.let {dir->
            clearSpecificDir(dir)
        }
        log(TAG, "done")
    }

    private suspend fun clearSpecificDir(dir: File){
        log(TAG, "clearing ${dir.name}")
        withContext(Dispatchers.IO){ dir.deleteRecursively() }
        log(TAG, "${dir.name} deleted")
    }
}
