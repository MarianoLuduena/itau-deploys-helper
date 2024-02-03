package com.itau.deploys.domain

object MicroserviceDefinition {

  private val BFF_DESA_NAMESPACE = "mercury-bff-desa"
  private val BFF_INTE_NAMESPACE = "mercury-bff-inte"
  private val BFF_HOMO_NAMESPACE = "mercury-bff-homo"
  private val BFF_PROD_NAMESPACE = "mercury-bff-prod"

  private val BFF_DESA_ROUTE = "apps.ocp-np.sis.ad.bia.itau"
  private val BFF_INTE_ROUTE = "apps.ocp-np.sis.ad.bia.itau"
  private val BFF_HOMO_ROUTE = "ocph.sis.ad.bia.itau"
  private val BFF_PROD_ROUTE = "itau.com.ar"

  private val BFF_DESA_ROUTE_HOST = "mercury-bff"
  private val BFF_INTE_ROUTE_HOST = BFF_INTE_NAMESPACE
  private val BFF_HOMO_ROUTE_HOST = BFF_HOMO_NAMESPACE
  private val BFF_PROD_ROUTE_HOST = "mcy"

  private val BACK_DESA_NAMESPACE = "mercury-back-desa"
  private val BACK_INTE_NAMESPACE = "mercury-back-inte"
  private val BACK_HOMO_NAMESPACE = "mercury-back-homo"
  private val BACK_PROD_NAMESPACE = "mercury-back-prod"

  private val BACK_DESA_ROUTE = BFF_DESA_ROUTE
  private val BACK_INTE_ROUTE = BFF_INTE_ROUTE
  private val BACK_HOMO_ROUTE = BFF_HOMO_ROUTE
  private val BACK_PROD_ROUTE = "ocp.sis.ad.bia.itau"

  private val BACK_DESA_ROUTE_HOST = BACK_DESA_NAMESPACE
  private val BACK_INTE_ROUTE_HOST = BACK_INTE_NAMESPACE
  private val BACK_HOMO_ROUTE_HOST = BACK_HOMO_NAMESPACE
  private val BACK_PROD_ROUTE_HOST = BACK_PROD_NAMESPACE

  def bffs(
            appName: String,
            itCode: String,
            port: String,
            routePath: String,
            healthPath: String
          ): List[MicroserviceDefinition] = {
    List(
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "desa",
        limitsCpu = "1000m", limitsMemory = "512Mi", healthPath = healthPath, claimName = "",
        namespace = BFF_DESA_NAMESPACE, route = BFF_DESA_ROUTE, routeHost = BFF_DESA_ROUTE_HOST, routePath = routePath),
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "inte",
        limitsCpu = "1000m", limitsMemory = "512Mi", healthPath = healthPath, claimName = "",
        namespace = BFF_INTE_NAMESPACE, route = BFF_INTE_ROUTE, routeHost = BFF_INTE_ROUTE_HOST, routePath = routePath),
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "homo",
        limitsCpu = "1000m", limitsMemory = "512Mi", healthPath = healthPath, claimName = "",
        namespace = BFF_HOMO_NAMESPACE, route = BFF_HOMO_ROUTE, routeHost = BFF_HOMO_ROUTE_HOST, routePath = routePath),
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "prod",
        limitsCpu = "1000m", limitsMemory = "512Mi", healthPath = healthPath, claimName = "",
        namespace = BFF_PROD_NAMESPACE, route = BFF_PROD_ROUTE, routeHost = BFF_PROD_ROUTE_HOST, routePath = routePath),
    )
  }

  def back(
            appName: String,
            itCode: String,
            port: String,
            routePath: String,
            healthPath: String,
            claimName: String
          ): List[MicroserviceDefinition] = {
    List(
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "desa",
        limitsCpu = "1000m", limitsMemory = "512Mi", healthPath = healthPath, claimName = claimName,
        namespace = BACK_DESA_NAMESPACE, route = BACK_DESA_ROUTE, routeHost = BACK_DESA_ROUTE_HOST,
        routePath = routePath),
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "inte",
        limitsCpu = "1000m", limitsMemory = "512Mi", healthPath = healthPath, claimName = claimName,
        namespace = BACK_INTE_NAMESPACE, route = BACK_INTE_ROUTE, routeHost = BACK_INTE_ROUTE_HOST,
        routePath = routePath),
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "homo",
        limitsCpu = "1000m", limitsMemory = "768Mi", healthPath = healthPath, claimName = claimName,
        namespace = BACK_HOMO_NAMESPACE, route = BACK_HOMO_ROUTE, routeHost = BACK_HOMO_ROUTE_HOST,
        routePath = routePath),
      MicroserviceDefinition(appName = appName, itCode = itCode, port = port, environment = "prod",
        limitsCpu = "1000m", limitsMemory = "768Mi", healthPath = healthPath, claimName = claimName,
        namespace = BACK_PROD_NAMESPACE, route = BACK_PROD_ROUTE, routeHost = BACK_PROD_ROUTE_HOST,
        routePath = routePath),
    )
  }

}

case class MicroserviceDefinition(
                                   appName: String,
                                   itCode: String,
                                   port: String,
                                   environment: String,
                                   limitsCpu: String,
                                   limitsMemory: String,
                                   healthPath: String,
                                   claimName: String,
                                   namespace: String,
                                   route: String,
                                   routeHost: String,
                                   routePath: String,
                                 )
