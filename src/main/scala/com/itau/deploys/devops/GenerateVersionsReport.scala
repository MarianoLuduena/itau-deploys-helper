package com.itau.deploys.devops

import com.itau.deploys.domain.DeploymentConfig
import com.itau.deploys.http.{HttpWrapper, WebClient}

@main
def generateVersionsReportMain(): Unit = {
  val apiDevopsBaseUrl = System.getenv("API_DEVOPS_BASE_URL")
  GenerateVersionsReport(apiDevopsBaseUrl).execute()
}

class GenerateVersionsReport(apiDevopsUrl: String) extends HttpWrapper {

  private val appsToLookFor = List(
    "bff-agreement-payments",
    "bff-requests",
    "api-provider-payments",
    "api-authorizations",
    "api-transfers",
  )

  private val deploymentsPath = "/api/apiocp/infoapps"
  private val deploymentsUrl = apiDevopsUrl + deploymentsPath
  private val cluster = "pa"
  private val homoNamespaces = List("mercury-bff-homo", "mercury-back-homo")
  private val prodNamespaces = List("mercury-bff-prod", "mercury-back-prod")

  def execute(): Unit = {
    doWithHttpClient { webClient =>
      val url = apiDevopsUrl + deploymentsPath
      val deploymentConfigsInHomo = getHomoDeploymentConfigs(webClient)
      val deploymentConfigsInProd = getProdDeploymentConfigs(webClient)

      println("\nREPORT\n")
      println(s"${"SERVICIO".padTo(30, ' ')}|${" VERSION HOMO".padTo(20, ' ')}|${" VERSION PROD".padTo(20, ' ')}")
      println(s"${"-".repeat(30)}|${"-".repeat(20)}|${"-".repeat(20)}")
      appsToLookFor.foreach { app =>
        val oHomoDeploymentConfig = deploymentConfigsInHomo.get(app)
        val oProdDeploymentConfig = deploymentConfigsInProd.get(app)

        println(s"${app.padTo(30, ' ')}|" +
          s" ${oHomoDeploymentConfig.map(_.version).getOrElse("N/E").padTo(19, ' ')}|" +
          s" ${oProdDeploymentConfig.map(_.version).getOrElse("N/E").padTo(19, ' ')}")
      }
      println("\nEND REPORT\n")
    }
  }

  private def getHomoDeploymentConfigs(webClient: WebClient): Map[String, DeploymentConfig] =
    getDeploymentConfigsByName(webClient, homoNamespaces)

  private def getProdDeploymentConfigs(webClient: WebClient): Map[String, DeploymentConfig] =
    getDeploymentConfigsByName(webClient, prodNamespaces)

  private def getDeploymentConfigsByName(
                                          webClient: WebClient,
                                          namespaces: List[String]
                                        ): Map[String, DeploymentConfig] =
    namespaces.flatMap { namespace =>
      webClient.get[List[DeploymentConfig]](
        deploymentsUrl,
        Map.empty,
        Map("cluster" -> cluster, "namespace" -> namespace)
      ) match
        case Right(value) => value
        case Left(error) => println(error); List.empty
    }.groupBy(deploymentConfig => deploymentConfig.name).view.mapValues(_.head).toMap

}
