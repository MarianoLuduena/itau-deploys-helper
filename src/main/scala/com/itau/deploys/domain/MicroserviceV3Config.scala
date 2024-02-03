package com.itau.deploys.domain

import com.itau.deploys.common.SnakeCaseJsonSerde
import io.circe.{Decoder, JsonObject}
import io.circe.generic.semiauto.deriveDecoder

object MicroserviceV3Config extends SnakeCaseJsonSerde {

  given Decoder[MicroserviceV3Config] = deriveSnakeCaseDecoder[MicroserviceV3Config]

}

case class MicroserviceV3Config(
                                 appName: String,
                                 appdynamicsv: String,
                                 claimName: String,
                                 compileCommand: String,
                                 containerPort1: String,
                                 environment: String,
                                 gitRepo: String,
                                 itcode: String,
                                 languageVersion: String,
                                 limitsCpu: String,
                                 limitsMemory: String,
                                 livenessPath: String,
                                 mountPath: String,
                                 namespace: String,
                                 readinessPath: String,
                                 replicas: String,
                                 requestCpu: String,
                                 requestMemory: String,
                                 route: String,
                                 routeHost: String,
                                 routePath: String,
                               )
