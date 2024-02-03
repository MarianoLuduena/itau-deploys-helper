package com.itau.deploys.domain

import com.itau.deploys.domain.MergeRequest.deriveSnakeCaseDecoder
import io.circe.Decoder

import java.time.ZonedDateTime

object DeploymentConfig {
  given Decoder[DeploymentConfig] = deriveSnakeCaseDecoder[DeploymentConfig]
}

case class DeploymentConfig(
                             projectOcp: String,
                             name: String,
                             labels: Map[String, String],
                             replicas: Int,
                             version: String,
                             `type`: String,
                             lastUpdateTimeProgressing: ZonedDateTime,
                             reasonProgressing: String,
                             statusProgressing: String,
                             messageProgressing: String,
                             lastUpdateTimeAvailable: ZonedDateTime,
                             statusAvailable: String,
                             messageAvailable: String,
                             availableReplicas: Int,
                             creationTimestamp: ZonedDateTime
                           ) {

  def isV3: Boolean = labels.getOrElse("pipeversion", "") == "v3"

  def itCode: Option[String] = labels.get("itcode")

}
