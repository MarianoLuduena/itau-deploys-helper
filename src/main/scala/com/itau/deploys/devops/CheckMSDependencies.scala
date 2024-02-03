package com.itau.deploys.devops

import com.itau.deploys.domain.FileReport

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors
import scala.util.Using
import scala.jdk.CollectionConverters.ListHasAsScala

@main
def checkMSDependenciesMain(): Unit = {
  val basePath = System.getenv("BASE_REPOSITORIES_PATH")
  CheckMSDependencies(basePath).execute()
}

class CheckMSDependencies(basePath: String) {

  private val pathToScan = "src/main"
  private val validFilePatterns = ".*\\.(yml|java)$".r
  private val valueRegex = "\\$\\{(.+)}".r // ${login.api.uri}
  private val blacklistedConfigKeys = Set(
    "SPRING_CLOUD_CONFIG_URL", "access-management.auth.server.url", "MERCURY_DATASOURCE_URL"
  )

  private val repositories =
    List(
//      "it0500-mercury-api-users",
//      "it0512-mercury-bff-registrations",
//      "it0520-mercury-api-credentials",
//      "it0665-mercury-api-enterprise-payments",
//      "it0667-mercury-acl-enterprise-payments",
//      "it0797-mercury-acl-providers",
//      "it0798-mercury-api-providers",
//      "it0799-mercury-bff-providers",
//      "it0784-mercury-bff-provider-payments",
//      "it0785-mercury-api-provider-payments",
//      "it0754-mercury-acl-bulkregistrations",
//      "it0519-mercury-bff-receipts",
//      "it0517-mercury-bff-transactions",
//      "it0760-mercury-api-multiple-salary-payment",
//      "it0514-mercury-api-authorization-workflow",
      "it0832-mercury-bff-agreement-payments",
      "it0518-mercury-bff-requests",
    ).map(directory => basePath + "/" + directory)

  def execute(): Unit = {
    repositories.foreach { repository =>
      val directoryToStartScanningFrom = repository + "/" + pathToScan
      Using.resource(Files.walk(Paths.get(directoryToStartScanningFrom))) { paths =>
        val findings = paths
          .filter(path => Files.isRegularFile(path) && validFilePatterns.matches(path.getFileName.toString))
          .map(path => evaluateFile(path).findings)
          .collect(Collectors.toUnmodifiableList)
          .asScala
          .flatten
          .distinct

        if (findings.nonEmpty) {
          println(s"\n$repository${findings.mkString("\n\t\t - ", "\n\t\t - ", "")}")
        }
      }
    }
  }

  private def evaluateFile(path: Path): FileReport = {
    val findings =
      Files.readAllLines(path).asScala
        .flatMap(line =>
          valueRegex.findFirstMatchIn(line)
            .map(aMatch => aMatch.group(1))
            .filter { configKey =>
              val sanitizedConfigKey = configKey.trim.toLowerCase
              !blacklistedConfigKeys.contains(configKey) &&
                (sanitizedConfigKey.contains("uri") || sanitizedConfigKey.contains("url"))
            }
        )
        .distinct
        .toList
    FileReport(path = path, findings = findings)
  }

}
