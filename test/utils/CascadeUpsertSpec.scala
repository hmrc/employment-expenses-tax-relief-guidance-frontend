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

package utils

import play.api.libs.json._
import base.SpecBase
import identifiers._
import models._
import uk.gov.hmrc.http.cache.client.CacheMap

class CascadeUpsertSpec extends SpecBase {

  val cascadeUpsert = new CascadeUpsert

  object Key extends Identifier {
    override def toString: String = "key"
  }

  "using the apply method for a key that has no special function" when {
    "the key doesn't already exists" must {
      "add the key to the cache map" in {
        val originalCacheMap = new CacheMap("id", Map())
        val result = cascadeUpsert(Key, "value", originalCacheMap)
        result.data mustEqual Map(Key.toString -> JsString("value"))
      }
    }

    "data already exists for that key" must {
      "replace the value held against the key" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> JsString("original value")))
        val result = cascadeUpsert(Key, "new value", originalCacheMap)
        result.data mustEqual Map(Key.toString -> JsString("new value"))
      }
    }
  }

  "addRepeatedValue" when {
    "the key doesn't already exist" must {
      "add the key to the cache map and save the value in a sequence" in {
        val originalCacheMap = new CacheMap("id", Map())
        val result = cascadeUpsert.addRepeatedValue(Key, "value", originalCacheMap)
        result.data mustEqual Map(Key.toString -> Json.toJson(Seq("value")))
      }
    }

    "the key already exists" must {
      "add the new value to the existing sequence" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> Json.toJson(Seq("value"))))
        val result = cascadeUpsert.addRepeatedValue(Key, "new value", originalCacheMap)
        result.data mustEqual Map(Key.toString -> Json.toJson(Seq("value", "new value")))
      }
    }
  }

  ".apply" when {

    val fullCacheMap = new CacheMap("id", Map(
      ClaimantId.toString                          -> JsString(Claimant.You.toString),
      WillPayTaxId.toString                        -> JsBoolean(true),
      PaidTaxInRelevantYearId.toString             -> JsBoolean(true),
      RegisteredForSelfAssessmentId.toString       -> JsBoolean(false),
      ClaimingOverPayAsYouEarnThresholdId.toString -> JsBoolean(false),
      EmployerPaidBackExpensesId.toString          -> JsBoolean(false),
      ClaimingForId.toString                       -> JsArray(Seq(JsString(ClaimingFor.TravelExpenses.toString))),
      UseOwnCarId.toString                         -> JsBoolean(true),
      ClaimingMileageId.toString                   -> JsBoolean(true),
      UseCompanyCarId.toString                     -> JsBoolean(true),
      ClaimingFuelId.toString                      -> JsBoolean(true),
      MoreThanFiveJobsId.toString                  -> JsBoolean(false)
    ))

    "the answer for Claimant is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(ClaimantId, Claimant.SomeoneElse.toString, fullCacheMap)
        result.data.keySet mustEqual Set(ClaimantId.toString)
      }
    }

    "the answer for WillPayTax is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(WillPayTaxId, false, fullCacheMap)
        result.data.keySet mustEqual Set(ClaimantId.toString, WillPayTaxId.toString)
      }
    }

    "the answer for PaidTaxInRelevantYear is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(PaidTaxInRelevantYearId, false, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString
          )
      }
    }

    "the answer for RegisteredForSelfAssessment is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(RegisteredForSelfAssessmentId, true, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString
          )
      }
    }

    "the answer for ClaimingOverPayAsYouEarnThreshold is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(ClaimingOverPayAsYouEarnThresholdId, true, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString
          )
      }
    }

    "the answer for EmployerPaidBackExpenses is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(EmployerPaidBackExpensesId, true, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString,
            EmployerPaidBackExpensesId.toString
          )
      }
    }

    "the answer for ClaimingFor is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(ClaimingForId, List(ClaimingFor.HomeWorking.toString), fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString,
            EmployerPaidBackExpensesId.toString,
            ClaimingForId.toString
          )
      }
    }

    "the answer for UseOwnCar is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(UseOwnCarId, false, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString,
            EmployerPaidBackExpensesId.toString,
            ClaimingForId.toString,
            UseOwnCarId.toString
          )
      }
    }

    "the answer for ClaimingMileage is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(ClaimingMileageId, false, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString,
            EmployerPaidBackExpensesId.toString,
            ClaimingForId.toString,
            UseOwnCarId.toString,
            ClaimingMileageId.toString
          )
      }
    }

    "the answer for UseCompanyCar is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(UseCompanyCarId, false, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString,
            EmployerPaidBackExpensesId.toString,
            ClaimingForId.toString,
            UseOwnCarId.toString,
            ClaimingMileageId.toString,
            UseCompanyCarId.toString
          )
      }
    }

    "the answer for ClaimingFuel is changed" must {
      "delete data for all later screens" in {
        val result = cascadeUpsert(ClaimingFuelId, false, fullCacheMap)
        result.data.keySet mustEqual
          Set(
            ClaimantId.toString,
            WillPayTaxId.toString,
            PaidTaxInRelevantYearId.toString,
            RegisteredForSelfAssessmentId.toString,
            ClaimingOverPayAsYouEarnThresholdId.toString,
            EmployerPaidBackExpensesId.toString,
            ClaimingForId.toString,
            UseOwnCarId.toString,
            ClaimingMileageId.toString,
            UseCompanyCarId.toString,
            ClaimingFuelId.toString
          )
      }
    }
  }
}
