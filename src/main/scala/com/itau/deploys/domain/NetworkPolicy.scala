package com.itau.deploys.domain

import io.circe.{Json, JsonObject}

object NetworkPolicy {

  def from(networkPolicyList: Json): List[NetworkPolicy] = {
    networkPolicyList.asObject
      .flatMap(_.apply("items").flatMap(_.asArray))
      .getOrElse(Vector.empty)
      .flatMap { itemJson =>
        val spec = itemJson.asObject.flatMap(_.apply("spec").flatMap(_.asObject)).get
        val targetService = podNameSelector(spec).getOrElse("SERVICE_NOT_FOUND")
        spec("ingress").flatMap(_.asArray).getOrElse(Vector.empty)
          .flatMap { ingressJson =>
            ingressJson.asObject.flatMap(_.apply("from").flatMap(_.asArray)).getOrElse(Vector.empty)
              .map { fromJson =>
                val fromObject = fromJson.asObject.get
                val podSelector = podNameSelector(fromObject).getOrElse("POD_NOT_FOUND")
                val namespaceSelector =
                  fromObject("namespaceSelector").flatMap(_.asObject)
                    .flatMap(nameSelector)
                    .getOrElse("NAMESPACE_NOT_FOUND")

                NetworkPolicy(
                  targetService = targetService,
                  sourcePod = podSelector,
                  sourceNamespace = namespaceSelector
                )
              }
          }
      }.toList
  }

  private def podNameSelector(json: JsonObject): Option[String] =
    json.apply("podSelector")
      .flatMap(_.asObject)
      .flatMap(nameSelector)

  private def nameSelector(json: JsonObject): Option[String] =
    json.apply("matchLabels")
      .flatMap(_.asObject)
      .flatMap(matchLabelObject => matchLabelObject.apply("name").flatMap(_.asString))

}

case class NetworkPolicy(
                          targetService: String,
                          sourcePod: String,
                          sourceNamespace: String
                        )
