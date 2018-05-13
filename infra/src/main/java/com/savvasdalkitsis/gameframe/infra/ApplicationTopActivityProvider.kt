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
package com.savvasdalkitsis.gameframe.infra

import android.app.Activity
import android.app.Application
import android.os.Bundle

import java.lang.ref.WeakReference

class ApplicationTopActivityProvider : TopActivityProvider, Application.ActivityLifecycleCallbacks {

    private var activity: WeakReference<Activity?> = WeakReference(null)

    override val topActivity: Activity?
        get() =  activity.get()

    override fun onActivityStarted(activity: Activity) {
        keep(activity)
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

    override fun onActivityResumed(activity: Activity) {
        keep(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    private fun keep(activity: Activity) {
        this.activity = WeakReference(activity)
    }
}
