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
import forms.EmployerPaidBackAnyExpensesFormProvider
import identifiers.{ClaimantId, EmployerPaidBackAnyExpensesId, RegisteredForSelfAssessmentId}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsBoolean, JsString}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.{CacheMap, Navigator, NavigatorSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployerPaidBackAnyExpensesControllerSpec
    extends SpecBase
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience
    with NavigatorSupport {

  def onwardRoute: Call = routes.IndexController.onPageLoad

  def employerPaidBackWFHExpensesRoute: String = routes.EmployerPaidBackAnyExpensesController.onPageLoad().url

  private val mockDataCacheConnector = mock[DataCacheConnector]

  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(), any(), any())(any())).thenReturn(Future(new CacheMap("id", Map())))
  }

  val formProvider = new EmployerPaidBackAnyExpensesFormProvider()
  val form         = formProvider()

  "EmployerPaidBackWFHExpenses Controller" must {

    "redirect to session expired controller/view when we have no existing session data" in {
      val application = applicationBuilder().build()
      val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
      val result      = route(application, request).value

      status(result) mustEqual SEE_OTHER

      application.stop()
    }

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
      val result      = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        EmployerPaidBackAnyExpensesId.toString -> JsString("noExpenses"),
        ClaimantId.toString                    -> JsString(claimant.toString)
      )

      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()
      val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
      val result      = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[DataCacheConnector].toInstance(mockDataCacheConnector)
        )
        .build()

      val request = FakeRequest(POST, employerPaidBackWFHExpensesRoute)
        .withFormUrlEncodedBody("value" -> "noExpenses")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val request = FakeRequest(POST, employerPaidBackWFHExpensesRoute)
        .withFormUrlEncodedBody(("value", "invalid value"))
      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val application = applicationBuilder().build()
      val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
      val result      = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder().build()
      val request     = FakeRequest(POST, employerPaidBackWFHExpensesRoute)
      val result      = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop()
    }

    "EmployerPaidBackWFHExpenses Controller's back button dynamic behaviour" must {

      "ensure there no back button override when RegisteredForSelfAssessmentId is true" in {
        val validData = Map(
          EmployerPaidBackAnyExpensesId.toString -> JsString("noExpenses"),
          RegisteredForSelfAssessmentId.toString -> JsBoolean(true),
          ClaimantId.toString                    -> JsString(claimant.toString)
        )

        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()
        val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
        val result      = route(application, request).value

        contentAsString(result).contains(frontendAppConfig.claimingForCurrentYearBackButtonOverride) mustBe false

        application.stop()
      }

      "ensure there no back button override when RegisteredForSelfAssessmentId is false" in {
        val validData = Map(
          EmployerPaidBackAnyExpensesId.toString -> JsString("noExpenses"),
          RegisteredForSelfAssessmentId.toString -> JsBoolean(false),
          ClaimantId.toString                    -> JsString(claimant.toString)
        )

        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()
        val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
        val result      = route(application, request).value

        contentAsString(result).contains(frontendAppConfig.claimingForCurrentYearBackButtonOverride) mustBe false

        application.stop()
      }

      "ensure there no back button override when RegisteredForSelfAssessmentId is missing" in {
        val validData = Map(
          EmployerPaidBackAnyExpensesId.toString -> JsString("noExpenses"),
          ClaimantId.toString                    -> JsString(claimant.toString)
        )

        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()
        val request     = FakeRequest(GET, employerPaidBackWFHExpensesRoute)
        val result      = route(application, request).value

        contentAsString(result).contains(frontendAppConfig.claimingForCurrentYearBackButtonOverride) mustBe false

        application.stop()
      }

    }
  }

}
