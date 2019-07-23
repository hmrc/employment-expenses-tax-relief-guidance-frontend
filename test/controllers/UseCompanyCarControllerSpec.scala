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
import forms.UseCompanyCarFormProvider
import identifiers.{ClaimantId, UseCompanyCarId, UseOwnCarId}
import models.UsingOwnCar
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.UseCompanyCarView

class UseCompanyCarControllerSpec extends SpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()
  def useCompanyCarRoute = routes.UseCompanyCarController.onPageLoad().url


  val useOfOwnCar = UsingOwnCar
  val formProvider = new UseCompanyCarFormProvider()
  val form = formProvider(claimant, useOfOwnCar)

  "UseCompanyCar Controller" must {

    "return OK and the correct view for a GET" in {
      val validCacheMap = CacheMap(cacheMapId, Map(
        UseOwnCarId.toString -> JsBoolean(true),
        ClaimantId.toString -> JsString(claimant.toString)
      ))
      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(GET, useCompanyCarRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[UseCompanyCarView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(form, claimant, useOfOwnCar)(fakeRequest, messages).toString

      application.stop
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validCacheMap = CacheMap(cacheMapId, Map(
        UseOwnCarId.toString -> JsBoolean(true),
        UseCompanyCarId.toString -> JsBoolean(true),
        ClaimantId.toString -> JsString(claimant.toString)
      ))

      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(GET, useCompanyCarRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[UseCompanyCarView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(form.fill(true), claimant, useOfOwnCar)(fakeRequest, messages).toString

      application.stop
    }

    "redirect to the next page when valid data is submitted" in {
      val validCacheMap = CacheMap(cacheMapId, Map(
        UseOwnCarId.toString -> JsBoolean(true),
        ClaimantId.toString -> JsString(claimant.toString)
      ))
      val application = applicationBuilder(Some(validCacheMap))
        .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
        .build
      val request = FakeRequest(POST, useCompanyCarRoute)
        .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)

      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val validCacheMap = CacheMap(cacheMapId, Map(
        UseOwnCarId.toString -> JsBoolean(true),
        ClaimantId.toString -> JsString(claimant.toString)
      ))
      val application = applicationBuilder(Some(validCacheMap)).build
      val request = FakeRequest(POST, useCompanyCarRoute)
        .withFormUrlEncodedBody(("value", "invalid value"))
      val result = route(application, request).value
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val view = application.injector.instanceOf[UseCompanyCarView]

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe view(boundForm, claimant, useOfOwnCar)(fakeRequest, messages).toString

      application.stop
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val application = applicationBuilder().build
      val request = FakeRequest(GET, useCompanyCarRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)

      application.stop
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val application = applicationBuilder().build
      val request = FakeRequest(POST, useCompanyCarRoute)
        .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)

      application.stop
    }
  }
}
