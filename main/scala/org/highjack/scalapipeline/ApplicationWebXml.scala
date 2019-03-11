package org.highjack.scalapipeline

import org.highjack.scalapipeline.config.DefaultProfileUtil

import org.springframework.boot.builder.SpringApplicationBuilder

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

//remove if not needed
import scala.collection.JavaConversions._

/**
  * replacement to webxml, invoked only when app is deployed to a servlet container
  */
class ApplicationWebXml extends SpringBootServletInitializer {

  protected override def configure(
      application: SpringApplicationBuilder): SpringApplicationBuilder = {
    DefaultProfileUtil.addDefaultProfile(application.application())
    application.sources(classOf[ScalapipelineApp])
  }

}
