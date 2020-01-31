package io.contentchef.common.exception

/**
 * Exception thrown when the provided mapper throws an exception
 * [throwable] the cause of the Exception
 */
class UnableToUseProvidedMapperException(throwable: Throwable) : Throwable(throwable)