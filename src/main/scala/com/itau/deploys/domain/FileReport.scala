package com.itau.deploys.domain

import java.nio.file.Path

case class FileReport(
                       path: Path,
                       findings: List[String]
                     ) {

  def hasFindings: Boolean = findings.nonEmpty

  def filename: String = path.getFileName.toString

}
