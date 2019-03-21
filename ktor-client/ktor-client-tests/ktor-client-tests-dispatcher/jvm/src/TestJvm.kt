package io.ktor.client.tests.utils.dispatcher

import kotlinx.coroutines.*
import kotlinx.coroutines.debug.*
import kotlin.coroutines.*

/**
 * Test runner for jvm suspend tests.
 */
actual fun testSuspend(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Unit {
    DebugProbes.install()
//    GlobalScope.launch {
//        while (true) {
//            delay(1000)
//            println("TRACE START")
//            DebugProbes.dumpCoroutines()
//            println("TRACE END")
//        }
//    }
    runBlocking(context, block)
}
