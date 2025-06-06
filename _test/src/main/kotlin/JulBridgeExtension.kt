package io.toolisticon.kotlin.avro.test

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.logging.LogManager

/**
 * Redirects logger calls to java-util-logging to slf4j.
 */
class JulBridgeExtension : BeforeAllCallback {
  private val logger = KotlinLogging.logger {}

  override fun beforeAll(ctx: ExtensionContext) {
    logger.debug { "Init Slf4j-Bridge" }
    LogManager.getLogManager().reset()
    SLF4JBridgeHandler.install()
  }
}
