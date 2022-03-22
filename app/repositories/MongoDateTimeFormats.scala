/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{JsValue, Json, Reads, Writes}

trait MongoDateTimeFormats {
  import play.api.libs.json.__

  implicit val localDateTimeRead: Reads[DateTime] =
    (__ \ "$date").read[Long].map {
      millis:Long => new DateTime(millis, DateTimeZone.UTC)
    }

  implicit val localDateTimeWrite: Writes[DateTime] = new Writes[DateTime] {
    def writes(dateTime: DateTime): JsValue = Json.obj(
      "$date" -> dateTime.toInstant.getMillis
    )
  }
}
