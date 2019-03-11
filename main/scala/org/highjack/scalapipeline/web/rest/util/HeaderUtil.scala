package org.highjack.scalapipeline.web.rest.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

//REST header alert and param handling for entity processing
object HeaderUtil {
  private val log: Logger = LoggerFactory.getLogger(HeaderUtil.getClass)
  private val APPLICATION_NAME: String = "scalapipeline"

  def createAlert(message: String, param: String): HttpHeaders = {
    val headers: HttpHeaders = new HttpHeaders()
    headers.add("X-" + APPLICATION_NAME + "-alert", message)
    headers.add("X-" + APPLICATION_NAME + "-params", param)
    headers
  }

  def createFailureAlert(entityName: String,
                         errorKey: String,
                         defaultMessage: String): HttpHeaders = {
    log.error("Entity processing failed, {}", defaultMessage)
    val headers: HttpHeaders = new HttpHeaders()
    headers.add("X-" + APPLICATION_NAME + "-error", defaultMessage)
    headers.add("X-" + APPLICATION_NAME + "-params", entityName)
    headers
  }

}
