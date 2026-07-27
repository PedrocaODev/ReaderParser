package com.opus.readerparser.core.util

import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SourceMetadataCacheTest {

    private class FakeClock(startNanos: Long = 0L) {
        var nowNanos: Long = startNanos
            private set

        fun advanceMs(ms: Long) {
            nowNanos += TimeUnit.MILLISECONDS.toNanos(ms)
        }

        fun read(): Long = nowNanos
    }

    @Test
    fun `exact hit returns cached value`() {
        val clock = FakeClock()
        val cache = SourceMetadataCache<String>(maxEntries = 4, ttlMs = 1_000, nowNanos = clock::read)

        cache.put("k", "v")

        assertEquals("v", cache.get("k"))
    }

    @Test
    fun `miss returns null`() {
        val cache = SourceMetadataCache<String>(maxEntries = 4, ttlMs = 1_000)

        assertNull(cache.get("missing"))
    }

    @Test
    fun `entry expires after ttl`() {
        val clock = FakeClock()
        val cache = SourceMetadataCache<String>(maxEntries = 4, ttlMs = 10, nowNanos = clock::read)

        cache.put("k", "v")
        clock.advanceMs(11)

        assertNull(cache.get("k"))
    }

    @Test
    fun `entry expires exactly at ttl boundary`() {
        val clock = FakeClock()
        val cache = SourceMetadataCache<String>(maxEntries = 4, ttlMs = 10, nowNanos = clock::read)

        cache.put("k", "v")
        clock.advanceMs(10)

        assertNull(cache.get("k"))
    }

    @Test
    fun `entry survives nanoTime wraparound until ttl elapses`() {
        val ttlMs = 10L
        val clock = FakeClock(Long.MAX_VALUE - TimeUnit.MILLISECONDS.toNanos(5))
        val cache = SourceMetadataCache<String>(maxEntries = 4, ttlMs = ttlMs, nowNanos = clock::read)

        cache.put("k", "v")
        clock.advanceMs(9)

        assertEquals("v", cache.get("k"))

        clock.advanceMs(1)

        assertNull(cache.get("k"))
    }

    @Test
    fun `capacity limits evict eldest entry at 100 50 and 20`() {
        listOf(100, 50, 20).forEach { maxEntries ->
            val clock = FakeClock()
            val cache = SourceMetadataCache<String>(maxEntries = maxEntries, ttlMs = 1_000, nowNanos = clock::read)

            repeat(maxEntries) { index -> cache.put("k$index", "v$index") }
            cache.put("k$maxEntries", "v$maxEntries")

            assertNull(cache.get("k0"))
            assertEquals("v$maxEntries", cache.get("k$maxEntries"))
        }
    }

    @Test
    fun `lru eviction removes least recently used entry`() {
        val clock = FakeClock()
        val cache = SourceMetadataCache<String>(maxEntries = 3, ttlMs = 1_000, nowNanos = clock::read)

        cache.put("a", "A")
        cache.put("b", "B")
        cache.put("c", "C")

        assertEquals("A", cache.get("a"))

        cache.put("d", "D")

        assertNull(cache.get("b"))
        assertEquals("A", cache.get("a"))
        assertEquals("C", cache.get("c"))
        assertEquals("D", cache.get("d"))
    }

    @Test
    fun `different keys do not collide`() {
        val clock = FakeClock()
        val cache = SourceMetadataCache<String>(maxEntries = 4, ttlMs = 1_000, nowNanos = clock::read)

        cache.put("one", "value-one")
        cache.put("two", "value-two")

        assertEquals("value-one", cache.get("one"))
        assertEquals("value-two", cache.get("two"))
    }

    @Test
    fun `concurrent access does not lose entries or throw`() {
        val clock = FakeClock()
        val cache = SourceMetadataCache<String>(maxEntries = 2_000, ttlMs = 1_000, nowNanos = clock::read)
        val threads = 8
        val iterations = 200
        val start = CountDownLatch(1)
        val done = CountDownLatch(threads)
        val failure = AtomicReference<Throwable?>(null)

        repeat(threads) { threadIndex ->
            Thread {
                try {
                    start.await()
                    repeat(iterations) { iteration ->
                        val key = "t$threadIndex-$iteration"
                        val value = "v$threadIndex-$iteration"
                        cache.put(key, value)
                        assertEquals(value, cache.get(key))
                    }
                } catch (t: Throwable) {
                    failure.compareAndSet(null, t)
                } finally {
                    done.countDown()
                }
            }.start()
        }

        start.countDown()
        done.await()

        failure.get()?.let { throw it }

        assertEquals("v0-0", cache.get("t0-0"))
        assertEquals("v7-199", cache.get("t7-199"))
    }
}
