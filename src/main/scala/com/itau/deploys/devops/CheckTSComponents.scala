package com.itau.deploys.devops

import com.itau.deploys.domain.FileReport

import java.nio.file.{Files, Path}
import scala.io.{Source, BufferedSource}
import scala.util.Using

@main
def checkTSComponentsMain(): Unit = {
  val directory = System.getenv("HBE_FRONT_APP_DIRECTORY")
  CheckTSComponents(directory).execute()
}

class CheckTSComponents(directory: String) {

  private val subscriptionRegex = "new\\s+Subscription\\s*\\(".r
  private val importRegex = "from\\s*'\\s*(.+)\\s*'".r

  def execute(): Unit = {
    Using.resource(Files.walk(java.io.File(directory).toPath)) { files =>
      files
        .filter(path => Files.isRegularFile(path))
        .forEach(path =>
          Using.resource(Source.fromFile(path.toFile, "UTF-8")) { src =>
            val report = path.getFileName.toString match {
              case filename if filename.endsWith(".spec.ts") =>
                checkTests(path, src)
              case filename if filename.endsWith(".component.ts") || filename.endsWith(".page.ts") =>
                checkTSComponent(path, src)
              case _ =>
                FileReport(path = path, findings = List.empty)
            }

            if (report.hasFindings) {
              println(report.filename + report.findings.mkString("\n\t - ", "\n\t - ", ""))
            }
          }
        )
    }
  }

  private def findDuplicatedImports(lines: List[String]): List[String] = {
    lines
      .flatMap(line =>
        importRegex.findAllIn(line) match
          case matchIterator if matchIterator.nonEmpty => Option(matchIterator.group(1))
          case _ => Option.empty
      )
      .groupBy(importPath => importPath)
      .filter((_, values) => values.size > 1)
      .keys
      .map(duplicatedImport => s"Duplicated import: $duplicatedImport")
      .toList
  }

  private def checkTSMemoryLeaks(lines: List[String]): List[String] = {
    val (numberOfSubscriptions, numberOfUnsubscribes) = lines.foldLeft((0, 0)) {
      case ((subscriptions, unsubscribes), line) if subscriptionRegex.findFirstIn(line).isDefined =>
        (subscriptions + 1, unsubscribes)
      case ((subscriptions, unsubscribes), line) if line.contains(".unsubscribe") =>
        (subscriptions, unsubscribes + 1)
      case ((subscriptions, unsubscribes), _) => (subscriptions, unsubscribes)
    }
    if (numberOfSubscriptions != numberOfUnsubscribes) {
      /*
        To review:
          - soft-token-email-confirmation.component.ts --> NO SE USA, ESTA OK
          - select-validation-option.component.ts --> NO SE USA, ESTA OK
          - textarea.component.ts
          - checks-issued-list.component.ts --> NO SE USA, ESTA OK
          - checks-received-list.component.ts --> NO SE USA, ESTA OK
          - qr-collections-list.component.ts --> NO SE USA, ESTA OK
          - last-statement.component.ts --> NO SE USA, ESTA OK
       */
      List(s"Possible memory leak (subscriptions = $numberOfSubscriptions)")
    } else {
      List.empty
    }
  }

  private def checkTSComponent(path: Path, src: BufferedSource): FileReport = {
    val lines = src.getLines().toList
    val memoryLeaksFindings = checkTSMemoryLeaks(lines)
    val duplicatedImportsFindings = findDuplicatedImports(lines)
    FileReport(
      path = path,
      findings = memoryLeaksFindings ++ duplicatedImportsFindings
    )
  }

  private def checkTests(path: Path, src: BufferedSource): FileReport = {
    val lines = src.getLines().toList
    val duplicatedImportsFindings = findDuplicatedImports(lines)
    FileReport(
      path = path,
      findings = duplicatedImportsFindings
    )
  }

}
