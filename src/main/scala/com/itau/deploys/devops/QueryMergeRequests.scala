package com.itau.deploys.devops

import com.itau.deploys.domain.MergeRequest
import com.itau.deploys.http.{HttpWrapper, WebClient}

import scala.annotation.tailrec

@main
def queryMergeRequestsMain(): Unit = {
  val token = System.getenv("GITLAB_BEARER_TOKEN")
  val gitlabBaseUrl = System.getenv("GITLAB_BASE_URL")
  QueryMergeRequests(token, gitlabBaseUrl).execute()
}

class QueryMergeRequests(token: String, gitlabBaseUrl: String) extends HttpWrapper {

  private val headers = Map(("Authorization", s"Bearer $token"))
  private val mergeRequestsUrl = gitlabBaseUrl + "/v4/merge_requests"
  private val baseQueryParams = Map(
    "per_page" -> "100",
    "created_after" -> "2023-12-01T00:00:00Z",
    "target_branch" -> "develop",
    "scope" -> "all"
  )
  private val userStoriesNumbers = List(
    // Cloud
    "231", // Rechazar/cancelar solicitud
    "238", // Ver pagos puntuales
    "239", // Ver detalle de solicitud con destinatarios
    "266", // Quitar pagos puntuales del listado de pagos masivos
  )
  private val keywords = userStoriesNumbers.map(number => s"(?i).*(NHEP(\\s*|-*)$number).*".r)

  def execute(): Unit = {
    doWithHttpClient { webClient =>
      println("Finding merge requests mentioning the following user stories: " + userStoriesNumbers)
      getMergeRequests(webClient).foreach { mergeRequest =>
        if (keywords.exists(keyword => mergeRequest.matches(keyword))) {
          println(s"${mergeRequest.webUrl} | ${mergeRequest.title}")
        }
      }
    }
  }

  private def getMergeRequests(webClient: WebClient): List[MergeRequest] =
    doGetMergeRequests(webClient, 1, List.empty)

  @tailrec
  private def doGetMergeRequests(webClient: WebClient, page: Int, accum: List[MergeRequest]): List[MergeRequest] = {
    println("Querying page " + page + "...")
    webClient.get[List[MergeRequest]](
      mergeRequestsUrl,
      headers,
      baseQueryParams + ("page" -> page.toString)
    ) match
      case Right(mergeRequests) if mergeRequests.isEmpty => accum
      case Right(mergeRequests) => doGetMergeRequests(webClient, page + 1, mergeRequests :++ accum)
      case Left(error) => println(error); accum
  }

}
