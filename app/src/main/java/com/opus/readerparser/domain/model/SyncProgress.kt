package com.opus.readerparser.domain.model

data class SyncProgress(
    val pagesCrawled: Int = 0,
    val itemsIngested: Int = 0,
    val isFinished: Boolean = false,
    val error: Throwable? = null,
) {
    val isError: Boolean get() = error != null
}
