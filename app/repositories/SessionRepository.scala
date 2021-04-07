/*
 * Copyright 2021 HM Revenue & Customs
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

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{JsValue, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC)
                        )

object DatedCacheMap extends MongoDateTimeFormats {

  implicit val formats = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(appConfig: FrontendAppConfig, mongo: () => DefaultDB)
  extends ReactiveRepository[DatedCacheMap, BSONObjectID](appConfig.serviceName, mongo, DatedCacheMap.formats) {

  val fieldName = "lastUpdated"
  val createdIndexName = "userAnswersExpiry"
  val expireAfterSeconds = "expireAfterSeconds"
  val timeToLiveInSeconds = appConfig.mongo_ttl

  val ttlIndex = Index(
    key  = Seq(
      fieldName -> IndexType.Ascending
    ),
    name = Some(createdIndexName),
    options = BSONDocument(expireAfterSeconds -> timeToLiveInSeconds)
  )

  val idIndex = Index(
    key = Seq(
      "id" -> IndexType.Ascending
    ),
    name = Some("userAnswersId")
  )

  collection.indexesManager.ensure(ttlIndex).map {
    result => {
      logger.debug(s"set [userAnswersExpiry] with value $timeToLiveInSeconds -> result : $result")
      result
    }
  }.recover {
    case e =>
      logger.error("Failed to set TTL index", e)
      false
  }.flatMap {
    _ =>
      collection.indexesManager.ensure(idIndex)
  }

  collection.indexesManager.ensure(Index(Seq()))

  def upsert(cm: CacheMap): Future[Boolean] = {
    val selector = Json.obj("id" -> cm.id)
    val cmDocument = Json.toJson(DatedCacheMap(cm))
    val modifier = Json.obj("$set" -> cmDocument)

    collection.update(ordered = false).one(selector, modifier, upsert = true).map {
      lastError =>
        lastError.ok
    }
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(Json.obj("id" -> id), None).one[CacheMap]
  }
}

@Singleton
class SessionRepository @Inject()(appConfig: FrontendAppConfig, mongo: ReactiveMongoComponent) {

  private lazy val sessionRepository = new ReactiveMongoRepository(appConfig, mongo.mongoConnector.db)

  def apply(): ReactiveMongoRepository = sessionRepository
}

