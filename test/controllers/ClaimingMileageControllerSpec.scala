/*
 * Copyright 2019 HM Revenue & Customs
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
import forms.ClaimingMileageFormProvider
import identifiers.{ClaimantId, ClaimingMileageId}
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.ClaimingMileageView

class ClaimingMileageControllerSpec extends SpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()
  def claimingMileageRoute = routes.ClaimingMileageController.onPageLoad().url

  private val formProvider = new ClaimingMileageFormProvider()
  private val form = formProvider(claimant)

  "ClaimingMileage Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val request = FakeRequest(GET, claimingMileageRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimingMileageView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig, form, claimant)(fakeRequest, messages).toString

      application.stop
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString), ClaimingMileageId.toString -> JsBoolean(true))
      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
      val request = FakeRequest(GET, claimingMileageRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimingMileageView]

      contentAsString(result) mustEqual view(frontendAppConfig, form.fill(true), claimant)(fakeRequest, messages).toString()

      application.stop
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
        .build
      val request = FakeRequest(POST, claimingMileageRoute)
        .withFormUrlEncodedBody("value" -> "true")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val request = FakeRequest(POST, claimingMileageRoute)
        .withFormUrlEncodedBody("value" -> "invalid value")
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimingMileageView]

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustBe view(frontendAppConfig, boundForm, claimant)(fakeRequest, messages).toString

      application.stop
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder().build()
      val request = FakeRequest(GET, claimingMileageRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder().build()
      val request = FakeRequest(POST, claimingMileageRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop
    }
  }
}
