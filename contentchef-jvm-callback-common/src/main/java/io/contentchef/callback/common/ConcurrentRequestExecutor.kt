package io.contentchef.callback.common

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Concurrently executes threads in background using a fixed thread pool
 */
internal object ConcurrentRequestExecutor : RequestExecutor {

    private const val MAX_THREADS_COUNT = 5
    private val backgroundPoolExecutor: ExecutorService =
        Executors.newFixedThreadPool(MAX_THREADS_COUNT)

    override fun executeRequest(requestToBeExecuted: () -> Unit) {
        backgroundPoolExecutor.execute(requestToBeExecuted)
    }

}