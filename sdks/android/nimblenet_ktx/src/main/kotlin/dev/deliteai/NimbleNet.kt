/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai

import dev.deliteai.datamodels.NimbleNetConfig
import dev.deliteai.datamodels.NimbleNetResult
import dev.deliteai.datamodels.NimbleNetTensor
import dev.deliteai.datamodels.UserEventData
import dev.deliteai.impl.DependencyContainer
import dev.deliteai.impl.common.MESSAGES
import dev.deliteai.impl.common.toNimbleNetResult
import dev.deliteai.impl.controllers.NimbleNetController
import dev.deliteai.impl.delitePy.proto.impl.ProtoObjectWrapper
import dev.deliteai.impl.loggers.LocalLogger
import android.app.Application
import org.json.JSONObject

object NimbleNet {

    @Volatile private lateinit var controller: NimbleNetController

    @Volatile private lateinit var localLogger: LocalLogger

    fun initialize(application: Application, config: NimbleNetConfig): NimbleNetResult<Unit> {
        val container = DependencyContainer.getInstance(application, config)
        controller = container.getNimbleNetController()
        localLogger = container.getLocalLogger()

        return runCatching { controller.initialize(config) }
            .onFailure(localLogger::e)
            .getOrElse { it.toNimbleNetResult() }
    }

    fun addEvent(eventMap: Map<String, Any>, eventType: String): NimbleNetResult<UserEventData> =
        safeCall {
            controller.addEvent(JSONObject(eventMap).toString(), eventType)
        }

    fun addEvent(serializedEventMap: String, eventType: String): NimbleNetResult<UserEventData> =
        safeCall {
            controller.addEvent(serializedEventMap, eventType)
        }

    fun addEvent(
        protoEvent: ProtoObjectWrapper,
        eventType: String,
    ): NimbleNetResult<UserEventData> = safeCall { controller.addEvent(protoEvent, eventType) }

    fun runMethod(
        methodName: String,
        inputs: HashMap<String, NimbleNetTensor>?,
    ): NimbleNetResult<HashMap<String, NimbleNetTensor>> = safeCall {
        controller.runMethod(methodName = methodName, inputs = inputs)
    }

    fun isReady(): NimbleNetResult<Unit> = safeCall { controller.isReady() }

    fun restartSession() {
        runCatching {
                checkInit()
                controller.restartSession("")
            }
            .onFailure(localLogger::e)
    }

    fun restartSessionWithId(sessionId: String) {
        runCatching {
                checkInit()
                controller.restartSession(sessionId)
            }
            .onFailure(localLogger::e)
    }

    private fun checkInit() {
        if (!this::controller.isInitialized || !controller.isNimbleNetInitialized()) {
            throw IllegalStateException(MESSAGES.SDK_NOT_INITIALIZED)
        }
    }

    private inline fun <T> safeCall(
        crossinline block: () -> NimbleNetResult<T>
    ): NimbleNetResult<T> =
        runCatching {
                checkInit()
                block()
            }
            .onFailure(localLogger::e)
            .getOrElse { it.toNimbleNetResult() }
}
