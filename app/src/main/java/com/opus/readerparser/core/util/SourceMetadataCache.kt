package com.opus.readerparser.core.util

import java.util.Collections
import java.util.LinkedHashMap
import java.util.concurrent.TimeUnit

/**
 * Small in-memory TTL cache for source metadata results.
 *
 * Access order is used so the least-recently-used entry is evicted first when
 * the cache reaches [maxEntries]. Time is monotonic via [nowNanos].
 */
class SourceMetadataCache<V>(
    private val maxEntries: Int,
    ttlMs: Long,
    private val nowNanos: () -> Long = System::nanoTime,
) {

    private val ttlNanos = TimeUnit.MILLISECONDS.toNanos(ttlMs)
    private val entries = Collections.synchronizedMap(
        LinkedHashMap<String, CachedValue<V>>(16, 0.75f, true),
    )

    init {
        require(maxEntries > 0) { "maxEntries must be positive" }
        require(ttlMs >= 0) { "ttlMs must be non-negative" }
    }

    fun get(key: String): V? = synchronized(entries) {
        val now = nowNanos()
        pruneExpired(now)
        entries[key]?.value
    }

    fun put(key: String, value: V) = synchronized(entries) {
        val now = nowNanos()
        pruneExpired(now)
        entries[key] = CachedValue(value = value, createdAtNanos = now)
        evictIfNeeded()
    }

    fun clear() = synchronized(entries) {
        entries.clear()
    }

    private fun pruneExpired(nowNanos: Long) {
        val iterator = entries.entries.iterator()
        while (iterator.hasNext()) {
            if (nowNanos - iterator.next().value.createdAtNanos >= ttlNanos) {
                iterator.remove()
            }
        }
    }

    private fun evictIfNeeded() {
        while (entries.size > maxEntries) {
            val eldest = entries.entries.iterator().next()
            entries.remove(eldest.key)
        }
    }

    private data class CachedValue<V>(
        val value: V,
        val createdAtNanos: Long,
    )
}
