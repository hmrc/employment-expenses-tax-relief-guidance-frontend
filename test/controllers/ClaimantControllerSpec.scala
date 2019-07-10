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
import play.api.libs.json.JsString
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.ClaimantFormProvider
import identifiers.ClaimantId
import models.Claimant
import models.Claimant.SomeoneElse
import play.api.inject.bind
import play.api.test.FakeRequest
import views.html.claimant

class ClaimantControllerSpec extends SpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  def claimantRoute = routes.ClaimantController.onPageLoad()

  val formProvider = new ClaimantFormProvider()
  val form = formProvider()

  /*def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new ClaimantController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, formProvider)

  def viewAsString(form: Form[_] = form) = claimant(frontendAppConfig, form)(fakeRequest, messages).toString
*/
  "Claimant Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder().build()

      val request = FakeRequest(GET, claimantRoute.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[claimant]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(frontendAppConfig, form)(fakeRequest, messages).toString

      application.stop
    }


    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()

      val request = FakeRequest(GET, claimantRoute.url)
        .withFormUrlEncodedBody(("value", claimant.toString))

      val result = route(application, request).value

      val view = application.injector.instanceOf[claimant]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(frontendAppConfig, form.fill(claimant))(fakeRequest, messages).toString

      application.stop
    }

    "redirect to the next page when valid data is submitted" in {
      val application = applicationBuilder()
        .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
        .build()

      val request = FakeRequest(POST, claimantRoute.url)
        .withFormUrlEncodedBody(("value", claimant.toString))

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)

      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val view = application.injector.instanceOf[claimant]
      val request = FakeRequest(POST, claimantRoute.url)
        .withFormUrlEncodedBody(("value", "invalid value"))
      val result = route(application, request).value
      val boundForm = form.bind(Map("value" -> "invalid value"))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe
        view.apply(frontendAppConfig, boundForm)(fakeRequest, messages).toString

      application.stop
    }
  }
}