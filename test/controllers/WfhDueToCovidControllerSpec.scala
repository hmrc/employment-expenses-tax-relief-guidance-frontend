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
import forms.CovidHomeWorkingFormProvider
import identifiers.CovidHomeWorkingId
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.JsBoolean
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.WfhDueToCovidView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WfhDueToCovidControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach
  with ScalaFutures with IntegrationPatience {

  def onwardRoute: Call = routes.IndexController.onPageLoad
  def covidHomeWorkingRoute: Call = routes.WfhDueToCovidController.onPageLoad()

  private val formProvider = new CovidHomeWorkingFormProvider()
  private val form = formProvider()

  private val mockDataCacheConnector = mock[DataCacheConnector]
  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(),any(),any())(any())) thenReturn Future(new CacheMap("id", Map()))
  }

  "Covid Home Working Controller" must {

    "redirect to session expired controller/view when we have no existing session data" in {
      val application = applicationBuilder().build
      val request = FakeRequest(GET, covidHomeWorkingRoute.url)
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)

      application.stop
    }

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val request = FakeRequest(GET, covidHomeWorkingRoute.url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[WfhDueToCovidView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form)(request, messages).toString

      application.stop
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      for (answer <- Seq(true, false)) {

        val validData = Map(CovidHomeWorkingId.toString -> JsBoolean(answer))
        val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()
        val request = FakeRequest(GET, covidHomeWorkingRoute.url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[WfhDueToCovidView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(answer))(request, messages).toString()

        application.stop
      }

    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
        .build()
      val request = FakeRequest(POST, covidHomeWorkingRoute.url)
        .withFormUrlEncodedBody("value" -> "true")
      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val request = FakeRequest(POST, covidHomeWorkingRoute.url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[WfhDueToCovidView]

      status(result) mustBe BAD_REQUEST

      contentAsString(result) mustBe view.apply(boundForm)(request, messages).toString

      application.stop
    }
  }
}
