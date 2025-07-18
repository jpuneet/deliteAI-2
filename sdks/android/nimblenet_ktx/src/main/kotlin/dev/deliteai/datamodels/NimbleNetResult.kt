/*
 * SPDX-FileCopyrightText: (C) 2025 DeliteAI Authors
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.deliteai.datamodels

import dev.deliteai.impl.common.ERROR_CODES

class NimbleNetError(var code: Int = ERROR_CODES.KOTLIN_ERROR, var message: String = "") {
    private fun populateErrorObject(code: Int, message: String) {
        this.code = code
        this.message = message
    }

    override fun toString(): String {
        return "code: $code \n message: $message"
    }
}

class NimbleNetResult<T>(
    var status: Boolean = false,
    var payload: T?,
    var error: NimbleNetError? = NimbleNetError(),
) {
    override fun toString(): String {
        return "status: $status \nerror: $error \npayload: $payload"
    }
}
