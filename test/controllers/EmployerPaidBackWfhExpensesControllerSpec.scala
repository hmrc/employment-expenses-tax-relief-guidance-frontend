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
import connectors.DataCacheConnector
import forms.EmployerPaidBackWfhExpensesFormProvider
import identifiers.{ClaimantId, EmployerPaidBackExpensesId}
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
import views.html.EmployerPaidBackWfhExpensesView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployerPaidBackWfhExpensesControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach
  with ScalaFutures with IntegrationPatience {

  def onwardRoute = routes.IndexController.onPageLoad()

  def employerPaidBackWfhExpensesRoute = routes.EmployerPaidBackWfhExpensesController.onPageLoad().url

  private val mockDataCacheConnector = mock[DataCacheConnector]
  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(),any(),any())(any())) thenReturn Future(new CacheMap("id", Map()))
  }

  val formProvider = new EmployerPaidBackWfhExpensesFormProvider()
  val form = formProvider(claimant)

  "EmployerPaidBackWfhExpenses Controller" must {

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val request = FakeRequest(GET, employerPaidBackWfhExpensesRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[EmployerPaidBackWfhExpensesView]

      status(result) mustBe OK
      contentAsString(result) mustBe view(form, claimant)(fakeRequest, messages).toString

      application.stop
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        EmployerPaidBackExpensesId.toString -> JsBoolean(true),
        ClaimantId.toString -> JsString(claimant.toString))

      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build
      val request = FakeRequest(GET, employerPaidBackWfhExpensesRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[EmployerPaidBackWfhExpensesView]

      contentAsString(result) mustEqual view(form.fill(true), claimant)(fakeRequest, messages).toString()

      application.stop
    }

    "redirect to the next page when valid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[DataCacheConnector].toInstance(mockDataCacheConnector)
        ).build

      val request = FakeRequest(POST, employerPaidBackWfhExpensesRoute)
        .withFormUrlEncodedBody("value" -> "true")
      val result = route(application, request).value

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build()
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val request = FakeRequest(POST, employerPaidBackWfhExpensesRoute)
      val result = route(application, request).value
      val view = application.injector.instanceOf[EmployerPaidBackWfhExpensesView]

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, claimant)(fakeRequest, messages).toString

      application.stop
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val application = applicationBuilder().build
      val request = FakeRequest(GET, employerPaidBackWfhExpensesRoute)
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder().build
      val request = FakeRequest(POST, employerPaidBackWfhExpensesRoute)
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe sessionExpiredUrl

      application.stop
    }
  }
}