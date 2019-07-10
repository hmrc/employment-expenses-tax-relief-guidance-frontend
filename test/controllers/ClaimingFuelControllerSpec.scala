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
import play.api.libs.json.{JsBoolean, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.FakeNavigator
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.ClaimingFuelFormProvider
import identifiers.{ClaimantId, ClaimingFuelId}
import models.Claimant.You
import play.api.test.FakeRequest
import views.html.{claimingFor, claimingFuel}

class ClaimingFuelControllerSpec extends SpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()
  def claimingFuelRoute = routes.ClaimingFuelController.onPageLoad().url

  val formProvider = new ClaimingFuelFormProvider()
  val form = formProvider(claimant)

/*
  def controller(dataRetrievalAction: DataRetrievalAction = getCacheMapWithClaimant(claimant)) =
    new ClaimingFuelController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl, new GetClaimantActionImpl, formProvider)

  def viewAsString(form: Form[_] = form) = claimingFuel(frontendAppConfig, form, claimant)(fakeRequest, messages).toString
*/

  "ClaimingFuel Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val request = FakeRequest(GET, claimingFuelRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[claimingFuel]

      status(result) mustBe OK
      contentAsString(result) mustBe view(frontendAppConfig, form, claimant)(fakeRequest, messages).toString

      application.stop

    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val validData = Map(
        ClaimingFuelId.toString -> JsBoolean(true),
        ClaimantId.toString -> JsString(claimant.toString)
      )

      val applicatation = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
      val request = FakeRequest(GET, claimingFuelRoute)
        .withFormUrlEncodedBody(("value" , "true"))
      val result = route(applicatation, request).value
      val view = applicatation.injector.instanceOf[claimingFuel]

      status(result) mustBe OK
      contentAsString(result) mustBe
        view(frontendAppConfig, form.fill(true), claimant)(fakeRequest, messages).toString

      applicatation.stop
    }

    "redirect to the next page when valid data is submitted" in {
/*
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
*/


    }

/*
    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)
    }*/
  }
}
