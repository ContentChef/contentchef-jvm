package io.contentchef.common.log

import java.util.logging.Level

/**
 * Empty implementation to avoid any log from ContentChef
 */
object NoLogger : Logger {

    override fun log(logLevel: Level, message: String?, throwable: Throwable?) {
        //it doesn't log anything!
    }

}