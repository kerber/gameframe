package com.savvasdalkitsis.gameframe.feature.control.injector

import com.savvasdalkitsis.gameframe.feature.control.presenter.ControlPresenter
import com.savvasdalkitsis.gameframe.feature.device.injector.DeviceInjector.deviceCase
import com.savvasdalkitsis.gameframe.feature.ip.injector.IpInjector.ipNavigator
import com.savvasdalkitsis.gameframe.feature.ip.injector.IpInjector.ipRepository
import com.savvasdalkitsis.gameframe.feature.networking.injector.NetworkingInjector.wifiUseCase

object ControlInjector {

    fun controlPresenter() = ControlPresenter(deviceCase(), ipRepository(), ipNavigator(), wifiUseCase())
}