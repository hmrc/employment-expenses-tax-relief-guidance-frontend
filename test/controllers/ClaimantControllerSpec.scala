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

package controllers

import base.SpecBase
import forms.ClaimantFormProvider
import identifiers.ClaimingForId
import models.ClaimingFor.{MileageFuel, UniformsClothingTools}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsArray, JsString}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.ClaimantView

class ClaimantControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  def onwardRoute: Call = routes.IndexController.onPageLoad
  def claimantRoute: Call = routes.ClaimantController.onPageLoad()

  private val formProvider = new ClaimantFormProvider()
  private val form = formProvider()

  "Claimant Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(Some(CacheMap(cacheMapId, Map(
        ClaimingForId.toString -> JsArray(List(
          JsString(MileageFuel.toString),
          JsString(UniformsClothingTools.toString)))
      )
      ))).build()
      val request = FakeRequest(GET, claimantRoute.url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimantView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val request = FakeRequest(GET, claimantRoute.url)
        .withFormUrlEncodedBody(("value", claimant.toString))
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimantView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(claimant))(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
        .build()
      val request = FakeRequest(POST, claimantRoute.url)
        .withFormUrlEncodedBody(("value", claimant.toString))
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val boundForm = form.bind(Map("value" -> ""))
      val request = FakeRequest(POST, claimantRoute.url)
        .withFormUrlEncodedBody(("value", ""))
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimantView]

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe
        view.apply(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder().build
      val request = FakeRequest(GET, claimantRoute.url)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)

      application.stop
    }


    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder().build
      val request = FakeRequest(GET, claimantRoute.url)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(sessionExpiredUrl)

      application.stop
    }
  }
}
