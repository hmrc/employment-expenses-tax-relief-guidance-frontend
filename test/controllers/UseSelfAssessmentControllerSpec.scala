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

package controllers

import base.SpecBase
import identifiers.{ClaimantId, ClaimingForCurrentYearId, EmployerPaidBackWfhExpensesId, RegisteredForSelfAssessmentId}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.UseSelfAssessmentView
import play.api.libs.json.{JsBoolean, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap

class UseSelfAssessmentControllerSpec extends SpecBase {

  def useSelfAssessmentRoute = routes.UseSelfAssessmentController.onPageLoad().url

  "UseSelfAssessment Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val request = FakeRequest(GET, useSelfAssessmentRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[UseSelfAssessmentView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(claimant, None)(request, messages).toString

      application.stop
    }

    "UseSelfAssessment Controller's back button dynamic behaviour" must {

      "ensure there no back button override when ClaimingForCurrentYearId is true" in {

        val validData = Map(
          RegisteredForSelfAssessmentId.toString -> JsBoolean(true),
          ClaimingForCurrentYearId.toString -> JsBoolean(true),
          ClaimantId.toString -> JsString(claimant.toString))

        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
        val request = FakeRequest(GET, useSelfAssessmentRoute)
        val result = route(application, request).value

        contentAsString(result).contains(frontendAppConfig.claimingForCurrentYearBackButtonOverride) mustBe false

        application.stop
      }

      "ensure there is no back button override when ClaimingForCurrentYearId is false" in {

        val validData = Map(
          RegisteredForSelfAssessmentId.toString -> JsBoolean(true),
          ClaimingForCurrentYearId.toString -> JsBoolean(false),
          ClaimantId.toString -> JsString(claimant.toString))

        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
        val request = FakeRequest(GET, useSelfAssessmentRoute)
        val result = route(application, request).value

        contentAsString(result).contains(frontendAppConfig.claimingForCurrentYearBackButtonOverride) mustBe false

        application.stop
      }


      "ensure there is no back button override when RegisteredForSelfAssessmentId missing" in {

        val validData = Map(ClaimantId.toString -> JsString(claimant.toString))

        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
        val request = FakeRequest(GET, useSelfAssessmentRoute)
        val result = route(application, request).value

        contentAsString(result).contains(frontendAppConfig.claimingForCurrentYearBackButtonOverride) mustBe false

        application.stop
      }

    }
  }
}
