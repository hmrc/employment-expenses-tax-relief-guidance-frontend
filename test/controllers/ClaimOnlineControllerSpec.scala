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

package controllers

import base.SpecBase
import identifiers.CovidHomeWorkingId
import models.ClaimingFor._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import viewmodels.OnwardJourney
import views.html.ClaimOnlineView

class ClaimOnlineControllerSpec extends SpecBase {

  def claimOnlineRoute = routes.ClaimOnlineController.onPageLoad.url

  "ClaimOnline Controller" must {

    "return OK and correct view for a GET when user eligible for employee expenses" in {

      val validCacheMap = CacheMap(cacheMapId, Map("claimingFor" -> Json.toJson(Seq(UniformsClothingTools.string))))
      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(GET, claimOnlineRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimOnlineView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(OnwardJourney.FixedRateExpenses)(fakeRequest, messages).toString

      application.stop
    }

    "return OK and correct view for a GET when user eligible for professional subscriptions" in {

      val validCacheMap = CacheMap(cacheMapId, Map("claimingFor" -> Json.toJson(Seq(FeesSubscriptions.string))))
      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(GET, claimOnlineRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimOnlineView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(OnwardJourney.ProfessionalSubscriptions)(fakeRequest, messages).toString

      application.stop
    }

    "return OK and correct view for a GET when user not eligible for either employee expenses or professional subscriptions" in {

      val validCacheMap = CacheMap(cacheMapId, Map("claimingFor" -> Json.toJson(Seq(UniformsClothingTools.string, MileageFuel.string))))
      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(GET, claimOnlineRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimOnlineView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(OnwardJourney.IForm)(fakeRequest, messages).toString

      application.stop
    }

    "return OK and correct view for a GET when user eligible for working from home expenses" in {

      val validCacheMap = CacheMap(cacheMapId, Map(CovidHomeWorkingId.toString -> Json.toJson(true)))
      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(GET, claimOnlineRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimOnlineView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(OnwardJourney.WorkingFromHomeExpensesOnly)(fakeRequest, messages).toString

      application.stop
    }

    "redirect for a GET if no data" in {

      val application = applicationBuilder().build
      val request = FakeRequest(GET, claimOnlineRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)

      application.stop
    }
  }
}
