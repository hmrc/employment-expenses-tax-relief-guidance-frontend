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
import play.api.data.Form
import play.api.libs.json.{JsArray, JsBoolean, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, UserAnswers}
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.WillPayTaxFormProvider
import identifiers.{ClaimantId, WillPayTaxId}
import models.Claimant.You
import org.scalatest.concurrent.ScalaFutures
import play.api.test.FakeRequest
import views.html.willPayTax

import scala.concurrent.ExecutionContext.Implicits.global

class WillPayTaxControllerSpec extends SpecBase with ScalaFutures {

  def onwardRoute = routes.IndexController.onPageLoad()

  val claimant = You

  val formProvider = new WillPayTaxFormProvider()

  val form = formProvider(claimant, frontendAppConfig.earliestTaxYear)

  val getValidPrecursorData = new FakeDataRetrievalAction(
    Some(
      CacheMap(
        cacheMapId,
        Map(
          ClaimantId.toString -> JsString(claimant.toString)
        )
      )
    )
  )


  "WillPayTax Controller" must {

    "return OK and the correct view for a GET" in {
      val validData = new CacheMap(
        cacheMapId,
        Map(
          ClaimantId.toString -> JsString(claimant.toString)
        )
      )

      val application = applicationBuilder(Some(validData)).build()

      val request = FakeRequest(GET, routes.WillPayTaxController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[willPayTax]

      val viewAsString = view(frontendAppConfig, form, claimant)(request, messages).toString

      status(result) mustBe OK

      contentAsString(result) mustBe viewAsString

      application.stop
    }

    /*
        "populate the view correctly on a GET when the question has previously been answered" in {
          val validData = Map(
            ClaimantId.toString -> JsString(claimant.toString),
            WillPayTaxId.toString -> JsBoolean(true)
          )

          val cacheMap = new CacheMap(cacheMapId, validData)

          val application = applicationBuilder(Some(cacheMap)).build

          val view = application.injector.instanceOf[willPayTax]

          def viewAsString(form: Form[_] = form) = view.apply(frontendAppConfig, form, claimant)(fakeRequest, messages).toString

          val request = FakeRequest(GET, routes.WillNotPayTaxController.onPageLoad().url)

          val result = route(application, request).value

          contentAsString(result) mustBe viewAsString(form.fill(true))
          application.stop
        }

        "redirect to the next page when valid data is submitted" in {
          val application = applicationBuilder().build

          val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER

          redirectLocation(result) mustBe Some(onwardRoute.url)

          application.stop
        }

        "return a Bad Request and errors when invalid data is submitted" in {

          val application = applicationBuilder().build

          val view = application.injector.instanceOf[willPayTax]

          def viewAsString(form: Form[_] = form) = view.apply(frontendAppConfig, form, claimant)(fakeRequest, messages).toString

          val request = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))

          val result = route(application, request).value

          val boundForm = form.bind(Map("value" -> "invalid value"))

          status(result) mustBe BAD_REQUEST
          contentAsString(result) mustBe viewAsString(boundForm)

          application.stop
        }

        "redirect to Session Expired for a GET if no existing data is found" in {

          val application = applicationBuilder().build

          val request = FakeRequest(GET, routes.WillNotPayTaxController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
          application.stop
        }

        "redirect to Session Expired for a POST if no existing data is found" in {
          val request = fakeRequest.withFormUrlEncodedBody(("value", "true"))

          val application = applicationBuilder().build

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
          application.stop
        }
    */
  }
}
