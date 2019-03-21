package io.ktor.util

import kotlinx.coroutines.*
import kotlinx.coroutines.io.*
import kotlinx.io.core.*

private const val CHUNK_BUFFER_SIZE = 4088L

/**
 * Split source [ByteReadChannel] into 2 new one.
 * Cancel of one channel in split(input or both outputs) cancels other channels.
 */
@KtorExperimentalAPI
fun ByteReadChannel.split(coroutineScope: CoroutineScope): Pair<ByteReadChannel, ByteReadChannel> {
    val first = ByteChannel(autoFlush = true)
    val second = ByteChannel(autoFlush = true)

    coroutineScope.launch {
        try {
            var count = 0
            while (!this@split.isClosedForRead) {
                this@split.readRemaining(CHUNK_BUFFER_SIZE).use { chunk ->
                    val src = chunk.readBytes()
                    listOf(
                        async {
                            first.writeFully(src)
//                            first.writePacket(chunk.copy())
                        },
                        async {
                            count++
                            println("Start writing chunk: $count ${src.size} ${second.availableForRead} ${second.availableForWrite}")
                            if (count == 18) {
                                println("HERE")
                            }
                            second.writeFully(src)
                            println("Write chunk done")
                        }
                    ).awaitAll()
                }
            }
        } catch (cause: Throwable) {
            this@split.cancel(cause)
            first.cancel(cause)
            second.cancel(cause)
        } finally {
            first.close()
            second.close()
        }
    }

    return first to second
}

/**
 * Read channel to byte array.
 */
@KtorExperimentalAPI
suspend fun ByteReadChannel.toByteArray(): ByteArray = readRemaining().readBytes()
