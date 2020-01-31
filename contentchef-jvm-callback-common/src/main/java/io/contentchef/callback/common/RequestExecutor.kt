package io.contentchef.callback.common

/**
 * Interface to execute requests: they could possibly be executed sequentially or in parallel
 */
internal interface RequestExecutor {

    /**
     * Executes the [requestToBeExecuted]
     */
    fun executeRequest(requestToBeExecuted: () -> Unit)
}