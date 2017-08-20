package com.savvasdalkitsis.gameframe.injector.usecase

import com.savvasdalkitsis.gameframe.feature.raster.usecase.BmpUseCase
import com.savvasdalkitsis.gameframe.feature.composition.usecase.BlendUseCase
import com.savvasdalkitsis.gameframe.feature.ip.usecase.IpDiscoveryUseCase
import com.savvasdalkitsis.gameframe.feature.gameframe.usecase.GameFrameUseCase
import com.savvasdalkitsis.gameframe.feature.saves.usecase.SavedDrawingUseCase

import com.savvasdalkitsis.gameframe.injector.ApplicationInjector.application
import com.savvasdalkitsis.gameframe.injector.feature.gameframe.api.GameFrameApiInjector.gameFrameApi
import com.savvasdalkitsis.gameframe.injector.infra.android.AndroidInjector.wifiManager
import com.savvasdalkitsis.gameframe.injector.infra.network.OkHttpClientInjector.okHttpClient

object UseCaseInjector {

    private val IP_DISCOVERY_USE_CASE = IpDiscoveryUseCase()

    fun gameFrameUseCase() = GameFrameUseCase(
            okHttpClient(1).build(),
            wifiManager(),
            gameFrameApi(),
            ipDiscoveryUseCase()
    )

    fun ipDiscoveryUseCase() = IP_DISCOVERY_USE_CASE

    private fun bmpUseCase() = BmpUseCase()

    fun savedDrawingUseCase() = SavedDrawingUseCase(bmpUseCase(), application())

    fun blendUseCase() = BlendUseCase()
}
