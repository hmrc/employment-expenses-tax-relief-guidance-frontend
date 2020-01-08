/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.Singleton
import config.FrontendAppConfig
import javax.inject.Inject
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.http.cache.client.CacheMap

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

@Singleton
class ReactiveMongoRepository @Inject()(mongo: ReactiveMongoApi, appConfig: FrontendAppConfig) {

  private val collectionName = appConfig.serviceName

  val fieldName = "lastUpdated"
  val createdIndexName = "userAnswersExpiry"
  val expireAfterSeconds = "expireAfterSeconds"
  val timeToLiveInSeconds = appConfig.mongo_ttl

  createIndex(fieldName, createdIndexName, timeToLiveInSeconds)

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private def createIndex(field: String, indexName: String, ttl: Int): Future[Boolean] = {
    collection.flatMap {
      _.indexesManager.ensure(Index(Seq((field, IndexType.Ascending)), Some(indexName),
        options = BSONDocument(expireAfterSeconds -> ttl))) map {
        result => {
          Logger.debug(s"set [$indexName] with value $ttl -> result : $result")
          result
        }
      } recover {
        case e => Logger.error("Failed to set TTL index", e)
          false
      }
    }
  }

  def upsert(cm: CacheMap): Future[Boolean] = {
    val selector = Json.obj("id" -> cm.id)
    val modifier = Json.obj("$set" -> DatedCacheMap(cm))

    collection.flatMap {
      _.update(ordered = false).one(selector, modifier, upsert = true).map { lastError =>
        lastError.ok
      }
    }
  }

  def get(id: String): Future[Option[CacheMap]] =
    collection.flatMap {
      _.find(Json.obj("id" -> id), None).one[CacheMap]
    }
}
