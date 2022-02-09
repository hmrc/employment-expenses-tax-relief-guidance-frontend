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

package utils

import javax.inject.Singleton

import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import identifiers._

@Singleton
class CascadeUpsert {

  val funcMap: Map[Identifier, (JsValue, CacheMap) => CacheMap] =
    Map()

  def apply[A](key: Identifier, value: A, originalCacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap =
    funcMap
      .get(key)
      .fold(clearDownstreamIfChanged(key, value, originalCacheMap)) {
        fn =>
          fn(Json.toJson(value), originalCacheMap)
      }

  def addRepeatedValue[A](key: Identifier, value: A, originalCacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap = {
    val values = originalCacheMap.getEntry[Seq[A]](key.toString).getOrElse(Seq()) :+ value
    originalCacheMap copy(data = originalCacheMap.data + (key.toString -> Json.toJson(values)))
  }

  private def store[A](key: Identifier, value: A, cacheMap: CacheMap)(implicit fmt: Format[A]) =
    cacheMap copy (data = cacheMap.data + (key.toString -> Json.toJson(value)))

  private def clearDownstreamIfChanged[A](key: Identifier, value: A, cacheMap: CacheMap)(implicit fmt: Format[A]) = {
    val mapToStore = if (cacheMap.getEntry[A](key.toString).contains(value)) {
      cacheMap
    } else {
      val keysToRemove = orderedIdentifiers.dropWhile(_ != key)

      cacheMap.copy(data = cacheMap.data.filterKeys(s => !keysToRemove.map(_.toString).contains(s)))
    }

    store(key, value, mapToStore)
  }

  lazy val orderedIdentifiers = List(
    ClaimAnyOtherExpenseId,
    ClaimantId,
    WillPayTaxId,
    PaidTaxInRelevantYearId,
    RegisteredForSelfAssessmentId,
    ClaimingOverPayAsYouEarnThresholdId,
    EmployerPaidBackExpensesId,
    ClaimingForId,
    UseOwnCarId,
    ClaimingMileageId,
    UseCompanyCarId,
    ClaimingFuelId,
    CovidHomeWorkingId,
    MoreThanFiveJobsId)
}
