package com.opus.readerparser.data.source

import com.opus.readerparser.domain.model.Series
import kotlinx.coroutines.flow.Flow

interface CatalogHarvester {
    suspend fun harvestCatalog(): Flow<List<Series>>
}
