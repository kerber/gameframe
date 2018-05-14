/**
 * Copyright 2018 Savvas Dalkitsis
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 'Game Frame' is a registered trademark of LEDSEQ
 */
package com.savvasdalkitsis.gameframe.feature.workspace.storage

import com.savvasdalkitsis.gameframe.feature.authentication.model.SignedInAccount
import com.savvasdalkitsis.gameframe.feature.authentication.usecase.AuthenticationUseCase
import com.savvasdalkitsis.gameframe.feature.workspace.model.SaveContainer
import io.reactivex.Completable
import io.reactivex.Single
import java.io.Reader

class AuthenticationAwareWorkspaceStorage(private val authenticationUseCase: AuthenticationUseCase<*>,
                                          private val localWorkspaceStorage: LocalWorkspaceStorage,
                                          private val firebaseWorkspaceStorage: FirebaseWorkspaceStorage): WorkspaceStorage {

    private val accountState get() = authenticationUseCase.accountState().firstOrError()

    private val workspaceStorage get() = accountState.map {
        when (it) {
            is SignedInAccount -> firebaseWorkspaceStorage
            else -> localWorkspaceStorage
        }
    }

    override fun saveWorkspace(name: String, workspaceModel: SaveContainer): Completable =
            workspaceStorage.flatMapCompletable { it.saveWorkspace(name, workspaceModel) }

    override fun listProjectNames(): Single<List<String>> =
            workspaceStorage.flatMap { it.listProjectNames() }

    override fun readWorkspace(name: String): Single<Reader> =
            workspaceStorage.flatMap { it.readWorkspace(name) }

    override fun deleteWorkspace(name: String): Completable =
            workspaceStorage.flatMapCompletable { it.deleteWorkspace(name) }

}