/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.mongo.play.json.formats.MongoJodaFormats.Implicits._
import org.mongodb.scala.model.{IndexModel, IndexOptions, UpdateOptions}
import org.mongodb.scala.model.Indexes._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import uk.gov.hmrc.mongo.play.json.Codecs
import utils.CacheMap

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.SECONDS

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC)
                        )

object DatedCacheMap extends MongoDateTimeFormats {

  implicit val formats = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(appConfig: FrontendAppConfig,
                              mongo: MongoComponent
                             )(implicit executionContext: ExecutionContext)
  extends PlayMongoRepository[DatedCacheMap](
    collectionName = appConfig.serviceName,
    mongoComponent = mongo,
    domainFormat = DatedCacheMap.formats,
    indexes = Seq(
      IndexModel(ascending("id"), IndexOptions().name("userAnswersId")),
      IndexModel(ascending("lastUpdated"),
        IndexOptions()
          .name("userAnswersExpiry")
          .expireAfter(appConfig.mongo_ttl, SECONDS))
      ),
    extraCodecs = Seq(Codecs.playFormatCodec(CacheMap.formats))
  ) {

  val fieldName = "lastUpdated"
  val createdIndexName = "userAnswersExpiry"
  val expireAfterSeconds = "expireAfterSeconds"
  val timeToLiveInSeconds = appConfig.mongo_ttl

  def upsert(cm: CacheMap): Future[Boolean] = {
    val dcm = DatedCacheMap(cm)
    collection.updateOne(
      filter = equal("id", dcm.id),
      update = combine(
        set("data", Codecs.toBson(dcm.data)),
        set("lastUpdated", Codecs.toBson(dcm.lastUpdated))),
      UpdateOptions().upsert(true)
    ).toFuture().map(_.wasAcknowledged())

  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find[CacheMap](and(equal("id", id))).headOption()
  }



}

@Singleton
class SessionRepository @Inject()(appConfig: FrontendAppConfig,
                                  mongo: MongoComponent
                                 )(implicit executionContext: ExecutionContext) {

  private lazy val sessionRepository = new ReactiveMongoRepository(appConfig, mongo)

  def apply(): ReactiveMongoRepository = sessionRepository
}

