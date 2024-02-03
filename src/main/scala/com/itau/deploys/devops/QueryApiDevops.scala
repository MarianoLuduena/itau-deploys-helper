package com.itau.deploys.devops

import com.itau.deploys.http.HttpWrapper
import com.itau.deploys.domain.MicroserviceV3Config

@main
def queryApiDevopsMain(): Unit = {
  val apiDevopsBaseUrl = System.getenv("API_DEVOPS_BASE_URL")
  QueryApiDevops(apiDevopsBaseUrl).execute()
}

class QueryApiDevops(baseUrl: String) extends HttpWrapper {

  private val services = List(
//    "it0784", // "it0784-mercury-bff-provider-payments",
//    "it0785", // "it0785-mercury-api-provider-payments",
//    "it0754", // "it0754-mercury-acl-bulkregistrations",
//    "it0519", // "it0519-mercury-bff-receipts",
//    "it0517", // "it0517-mercury-bff-transactions",
//    "it0760", // "it0760-mercury-api-multiple-salary-payment",
//    "it0665", // "it0665-mercury-api-enterprise-payments",
//    "it0667", // "it0667-mercury-acl-enterprise-payments",
    "it0477", // "it0477-mercury-api-clients",
    "it0669", // "it0669-mercury-api-agreements",
    "it0471", // "it0471-mercury-api-transfers",
  )

  def execute(): Unit = {
    doWithHttpClient(webClient => {
      services.foreach { itCode =>
        val url = baseUrl + s"?itcode=$itCode"
        webClient.get[List[MicroserviceV3Config]](url, Map.empty, Map.empty) match
          case Right(value) => println(value.filter(conf => conf.environment == "homo" || conf.environment == "prod"))
          case Left(error) => println(s"itCode = $itCode | " + error)
      }
    })
  }

}
