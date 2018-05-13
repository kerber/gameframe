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
package com.savvasdalkitsis.gameframe.feature.control.view

import android.os.Bundle
import android.support.annotation.ArrayRes
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import com.savvasdalkitsis.gameframe.feature.control.R
import com.savvasdalkitsis.gameframe.feature.control.injector.ControlInjector
import com.savvasdalkitsis.gameframe.feature.control.presenter.ControlPresenter
import com.savvasdalkitsis.gameframe.feature.device.model.*
import com.savvasdalkitsis.gameframe.feature.message.Snackbars
import com.savvasdalkitsis.gameframe.feature.networking.model.IpAddress
import com.savvasdalkitsis.gameframe.infra.android.BaseFragment
import com.savvasdalkitsis.gameframe.infra.android.FragmentSelectedListener
import com.savvasdalkitsis.gameframe.kotlin.gone
import com.savvasdalkitsis.gameframe.kotlin.visible
import kotlinx.android.synthetic.main.fragment_control.*

class ControlFragment : BaseFragment<ControlView, ControlPresenter>(), ControlView, FragmentSelectedListener {

    override val presenter = ControlInjector.controlPresenter()
    override val view = this
    private lateinit var fab: FloatingActionButton
    private val coordinator: View get() = activity!!.findViewById(R.id.view_coordinator)

    override val layoutId: Int
        get() = R.layout.fragment_control

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab = activity!!.findViewById(R.id.view_fab_control)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_brightness.setOnSeekBarChangeListener(BrightnessChangedListener())
        view_playback_mode.adapter = adapter(R.array.playback_mode)
        view_cycle_interval.adapter = adapter(R.array.cycle_interval)
        view_display_mode.adapter = adapter(R.array.display_mode)
        view_clock_face.adapter = adapter(R.array.clock_face)

        view_menu.setOnClickListener {
            presenter.menu()
        }
        view_next.setOnClickListener {
            presenter.next()
        }
        view_control_setup.setOnClickListener {
            presenter.setup()
        }
        view_brightness_low.setOnClickListener {
            view_brightness.incrementProgressBy(-1)
        }
        view_brightness_high.setOnClickListener {
            view_brightness.incrementProgressBy(1)
        }
        view_playback_mode.onItemSelected { position ->
            presenter.changePlaybackMode(PlaybackMode.from(position))
        }
        view_cycle_interval.onItemSelected { position ->
            presenter.changeCycleInterval(CycleInterval.from(position))
        }
        view_display_mode.onItemSelected { position ->
            presenter.changeDisplayMode(DisplayMode.from(position))
        }
        view_clock_face.onItemSelected { position ->
            presenter.changeClockFace(ClockFace.from(position))
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVisible) {
            presenter.loadIpAddress()
        }
    }

    override fun onFragmentSelected() {
        presenter.loadIpAddress()
    }

    override fun onFragmentUnselected() {
        fab.gone()
    }

    override fun operationSuccess() = Snackbars.success(coordinator, R.string.success)

    override fun operationFailure(e: Throwable) {
        Log.e(ControlFragment::class.java.name, "Operation failure", e)
        Snackbars.error(coordinator, R.string.operation_failed)
    }

    override fun wifiNotEnabledError(e: Throwable) {
        Log.e(ControlFragment::class.java.name, "Operation failure", e)
        Snackbars.actionError(coordinator, R.string.wifi_not_enabled, R.string.enable) {
            presenter.enableWifi()
        }
    }

    override fun ipAddressLoaded(ipAddress: IpAddress) {
        view_ip.text = getString(R.string.game_frame_ip, ipAddress.toString())
        view_control_error.gone()
        view_control_content.visible()
        fab.visible()
        fab.setOnClickListener { presenter.togglePower() }
    }

    override fun ipCouldNotBeFound(throwable: Throwable) {
        Log.e(ControlFragment::class.java.name, "Could not find ip", throwable)
        view_control_error.visible()
        view_control_content.gone()
        fab.gone()
    }

    private fun adapter(@ArrayRes data: Int) =
            ArrayAdapter.createFromResource(context, data, android.R.layout.simple_spinner_item).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

    private fun <T: Adapter> AdapterView<T>.onItemSelected(action : (position: Int) -> Unit) {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                action(position)
            }

        }
    }

    private inner class BrightnessChangedListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, level: Int, b: Boolean) {
            presenter.changeBrightness(Brightness.from(level))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }
}
