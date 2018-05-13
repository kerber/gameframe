/**
 * Copyright 2017 Savvas Dalkitsis
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
package com.savvasdalkitsis.gameframe.feature.changelog.usecase

import com.savvasdalkitsis.gameframe.feature.changelog.BuildConfig
import com.savvasdalkitsis.gameframe.infra.injector.RxSharedPreferencesInjector
import com.savvasdalkitsis.gameframe.infra.preferences.RxSharedPreferences

private const val KEY = "has_seen_change_log_for_version"

class ChangeLogUseCase(private val preferences: RxSharedPreferences = RxSharedPreferencesInjector.rxSharedPreferences()) {

    fun hasSeenChangeLog(): Boolean = preferences.getString(KEY).blockingGet() == BuildConfig.VERSION_NAME

    fun markChangeLogSeen() {
        preferences.setString(KEY, BuildConfig.VERSION_NAME)
    }
}