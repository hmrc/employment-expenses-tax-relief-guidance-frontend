/*
 * Copyright 2021 HM Revenue & Customs
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
import connectors.DataCacheConnector
import forms.{RegisteredForSelfAssessmentFormProvider, WhichYearsAreYouClaimingForFormProvider}
import identifiers.{ClaimAnyOtherExpenseId, ClaimantId, RegisteredForSelfAssessmentId, WhichYearsAreYouClaimingForId}
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.{RegisteredForSelfAssessmentView, WhichYearsAreYouClaimingForView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhichYearsAreYouClaimingForControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach
  with ScalaFutures with IntegrationPatience {

  def onwardRoute = routes.IndexController.onPageLoad()

  def whichYearsAreYouClaimingForRoute = routes.WhichYearsAreYouClaimingForController.onPageLoad().url

  private val mockDataCacheConnector = mock[DataCacheConnector]
  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(),any(),any())(any())) thenReturn Future(new CacheMap("id", Map()))
  }

  val formProvider = new WhichYearsAreYouClaimingForFormProvider()
  val form = formProvider(claimant)

  "WhichYearsAreYouClaimingFor Controller" must {

    "redirect to session expired controller/view when we have no existing session data" in {
      val application = applicationBuilder().build
      val request = FakeRequest(GET, whichYearsAreYouClaimingForRoute)
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)

      application.stop
    }

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val request = FakeRequest(GET, whichYearsAreYouClaimingForRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[WhichYearsAreYouClaimingForView]

      status(result) mustBe OK
      application.stop
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(ClaimantId.toString -> JsString(claimant.toString),
        RegisteredForSelfAssessmentId.toString -> JsBoolean(true))
      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
      val request = FakeRequest(GET, whichYearsAreYouClaimingForRoute)
      val result = route(application, request).value

      contentAsString(result).contains("Which years are you claiming tax relief for?") mustBe true

      application.stop
    }

    "redirect to the next page when valid data is submitted - just the current tax year 2021-2022" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString))

      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build

      val request = FakeRequest(POST, whichYearsAreYouClaimingForRoute)
        .withFormUrlEncodedBody("value" -> "1")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url + "/sa-check-disclaimer-current-year"

      application.stop
    }

    "redirect to the next page when valid data is submitted - previous tax years" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString))

      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build

      val request = FakeRequest(POST, whichYearsAreYouClaimingForRoute)
        .withFormUrlEncodedBody("value" -> "2")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url + "/use-self-assessment"

      application.stop
    }

    "redirect to the next page when valid data is submitted - both the current tax year and previous years" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString))

      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build

      val request = FakeRequest(POST, whichYearsAreYouClaimingForRoute)
        .withFormUrlEncodedBody("value" -> "3")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url + "/sa-check-disclaimer-all-years"

      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString))

      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build

      val request = FakeRequest(POST, whichYearsAreYouClaimingForRoute)

      val result = route(application, request).value

      status(result) mustBe BAD_REQUEST

      application.stop
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val application = applicationBuilder().build
      val request = FakeRequest(GET, whichYearsAreYouClaimingForRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop

    }
    "redirect to Session Expired for a POST if no existing data is found" in {
      val application = applicationBuilder().build
      val request = FakeRequest(POST, whichYearsAreYouClaimingForRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop
    }
  }

}
