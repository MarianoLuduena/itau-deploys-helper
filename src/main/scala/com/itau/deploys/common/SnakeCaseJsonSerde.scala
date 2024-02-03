package com.itau.deploys.common

import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, JsonObject}

import scala.deriving.Mirror

trait SnakeCaseJsonSerde extends ToggleCase {

  final inline def deriveSnakeCaseDecoder[A](using inline A: _root_.scala.deriving.Mirror.Of[A]): Decoder[A] =
    deriveDecoder[A]
      .prepare(cursor =>
        cursor.withFocus(json =>
          json.mapObject(jsonObject =>
            JsonObject.fromIterable(jsonObject.toIterable.map((key, value) => (toCamelCase(key), value)))
          )
        )
      )

}
