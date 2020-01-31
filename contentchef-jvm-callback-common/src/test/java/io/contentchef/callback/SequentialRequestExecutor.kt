package io.contentchef.callback

import io.contentchef.callback.common.RequestExecutor

/**
 * Sequantially executes threads in background using a fixed thread pool
 * Used ONLY for testing purposes
 */
object SequentialRequestExecutor : RequestExecutor {

    override fun executeRequest(requestToBeExecuted: () -> Unit) {
        requestToBeExecuted()
    }

}