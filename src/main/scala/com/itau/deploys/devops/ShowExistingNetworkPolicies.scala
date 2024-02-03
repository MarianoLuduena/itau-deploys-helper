package com.itau.deploys.devops

import com.itau.deploys.domain.NetworkPolicy
import io.circe.parser

import scala.util.Using

@main
def showExistingNetworkPoliciesMain(): Unit = {
  ShowExistingNetworkPolicies().execute()
}

class ShowExistingNetworkPolicies {

  private val NETWORK_POLICIES_FILE = "src/test/resources/openshift/np-prod.json"
  private val NETWORK_POLICIES_FILE_ENC = "UTF-8"

  def execute(): Unit = {
    Using.resource(scala.io.Source.fromFile(NETWORK_POLICIES_FILE, NETWORK_POLICIES_FILE_ENC)) { source =>
      val networkPolicies = parser.parse(source.mkString) match
        case Right(value) => NetworkPolicy.from(value)
        case Left(error) => println(error.message); List.empty

      networkPolicies.groupBy(_.targetService).foreach { (service, policies) =>
        println(service)
        policies.foreach(policy => println(s"\t- ${policy.sourcePod},${policy.sourceNamespace}"))
        println()
      }
    }
  }

}
