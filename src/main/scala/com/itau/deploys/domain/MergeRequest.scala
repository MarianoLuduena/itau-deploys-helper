package com.itau.deploys.domain

import com.itau.deploys.common.SnakeCaseJsonSerde
import io.circe.{Decoder, JsonObject}

import java.time.ZonedDateTime
import scala.util.matching.Regex

object MergeRequest extends SnakeCaseJsonSerde {

  given Decoder[MergeRequest] = deriveSnakeCaseDecoder[MergeRequest]

}

case class MergeRequest(
                         id: Int,
                         iid: Int,
                         projectId: Int,
                         title: String,
                         description: String,
                         state: String,
                         mergedAt: Option[ZonedDateTime],
                         createdAt: ZonedDateTime,
                         updatedAt: ZonedDateTime,
                         targetBranch: String,
                         sourceBranch: String,
                         webUrl: String
                       ) {

  def matches(r: Regex): Boolean =
    r.matches(sourceBranch) || r.matches(title) || r.matches(description) || r.matches(targetBranch)

}
