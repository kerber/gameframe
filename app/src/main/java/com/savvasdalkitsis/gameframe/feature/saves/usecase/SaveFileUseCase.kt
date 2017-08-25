package com.savvasdalkitsis.gameframe.feature.saves.usecase

import com.savvasdalkitsis.gameframe.GameFrameApplication
import com.savvasdalkitsis.gameframe.feature.saves.model.FileAlreadyExistsException
import com.savvasdalkitsis.gameframe.infra.kotlin.ByteArrayProvider
import io.reactivex.Completable
import io.reactivex.Single
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class SaveFileUseCase(private val application: GameFrameApplication) {

    fun saveFile(dirName: String, fileName: String,
                 fileContentsProvider: ByteArrayProvider,
                 overwriteFile: Boolean = false): Single<File> =
            file(dirName)
                    .flatMap { dir ->
                        when {
                            dir.exists() -> Single.just(File(dir, fileName))
                            !dir.mkdirs() -> Single.error<File>(IOException("Could not create directory " + dir.absolutePath))
                            else -> Single.just(File(dir, fileName))
                        }
                    }
                    .flatMap { file -> if (file.exists() && !overwriteFile) {
                        Single.error<File>(FileAlreadyExistsException("The file '${file.absolutePath}' already exists"))
                    } else {
                        Single.just(file)
                    }}
                    .flatMap { file ->
                        try {
                            file.writeBytes(fileContentsProvider())
                            Single.just(file)
                        } catch (e: Throwable ) {
                            Single.error<File>(e)
                        }
                    }

    fun deleteDirectory(name: String): Completable = file(name)
            .flatMapCompletable { dir ->
                if (!dir.exists()) {
                    Completable.complete()
                } else try {
                    FileUtils.deleteDirectory(dir)
                    Completable.complete()
                } catch (e: IOException) {
                    Completable.error(e)
                }
            }

    private fun file(name: String) =
            Single.just(File(application.getExternalFilesDir(null), name))
}
