package com.opus.readerparser.domain.usecase

import com.opus.readerparser.domain.model.Series

fun interface CatalogBatchPersister {
    suspend fun persistBatch(stubs: List<Series>): Int
}
