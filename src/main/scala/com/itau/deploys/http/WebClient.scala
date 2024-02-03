package com.itau.deploys.http

import io.circe
import io.circe.Decoder
import sttp.client3.{Identity, Response, ResponseException, SttpBackend, UriContext, basicRequest}
import sttp.client3.circe.SttpCirceApi

class WebClient(private val backend: SttpBackend[Identity, Any]) extends SttpCirceApi {

  def get[T](url: String, headers: Map[String, String], params: Map[String, String])
            (using Decoder[T]): Either[RestClientError, T] = {
    val response =
      basicRequest
        .get(uri"${buildUri(url, params)}")
        .headers(headers)
        .response(asJson[T])
        .send(backend)

    handleResponse(response)
  }

  private def handleResponse[T](response: Identity[Response[Either[ResponseException[String, circe.Error], T]]])
  : Either[RestClientError, T] =
    response.body match {
      case Left(value) =>
        Left(RestClientError(
          status = response.code.code,
          statusText = response.statusText,
          body = Option(value.getMessage),
          headers = response.headers.map(h => (h.name, h.value)).toMap
        ))
      case Right(value) => Right(value)
    }

  private def buildUri(url: String, params: Map[String, String]): String =
    url + params.foldLeft("") {
      case (accum, (name, value)) if accum.isBlank => "?" + name + "=" + value
      case (accum, (name, value)) => accum + "&" + name + "=" + value
    }

}

case class RestClientError(status: Int, statusText: String, body: Option[String], headers: Map[String, String])
