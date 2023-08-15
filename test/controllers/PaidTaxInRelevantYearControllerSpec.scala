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
import connectors.DataCacheConnector
import controllers.actions._
import forms.PaidTaxInRelevantYearFormProvider
import identifiers.{ClaimantId, PaidTaxInRelevantYearId}
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.{CacheMap, FakeNavigator, Navigator}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PaidTaxInRelevantYearControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  def onwardRoute = routes.IndexController.onPageLoad

  def paidTaxInRelevantYearRoute = routes.PaidTaxInRelevantYearController.onPageLoad().url

  private val mockDataCacheConnector = mock[DataCacheConnector]
  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(),any(),any())(any())) thenReturn Future(new CacheMap("id", Map()))
  }

  val formProvider = new PaidTaxInRelevantYearFormProvider()
  val form = formProvider(claimant, frontendAppConfig.earliestTaxYear)

  val getValidPrecursorData = new FakeDataRetrievalAction(
    Some(
      CacheMap(
        cacheMapId,
        Map(
          ClaimantId.toString -> JsString(claimant.toString)
        )
      )
    ),
    controllerComponents
  )

  "PaidTaxInRelevantYear Controller" must {

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val request = FakeRequest(GET, paidTaxInRelevantYearRoute)
      val result = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(ClaimantId.toString -> JsString(claimant.toString), PaidTaxInRelevantYearId.toString -> JsBoolean(true))
      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()
      val request = FakeRequest(GET, paidTaxInRelevantYearRoute)
      val result = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[DataCacheConnector].toInstance(mockDataCacheConnector)
        ).build()

      val request = FakeRequest(POST, paidTaxInRelevantYearRoute)
        .withFormUrlEncodedBody("value" -> "true")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val request = FakeRequest(POST, paidTaxInRelevantYearRoute)
        .withFormUrlEncodedBody("value" -> "invalid value")
      val result = route(application, request).value

      status(result) mustBe BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val application = applicationBuilder().build()
      val request = FakeRequest(GET, paidTaxInRelevantYearRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val application = applicationBuilder().build()
      val request = FakeRequest(POST, paidTaxInRelevantYearRoute)
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop()
    }
  }
}
