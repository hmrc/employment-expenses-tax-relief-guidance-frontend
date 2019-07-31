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
import connectors.DataCacheConnector
import forms.ClaimantFormProvider
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.ClaimantView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClaimantControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach with ScalaFutures with IntegrationPatience {

  def onwardRoute = routes.IndexController.onPageLoad()

  def claimantRoute = routes.ClaimantController.onPageLoad()

  private val mockDataCacheConnector = mock[DataCacheConnector]
  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(),any(),any())(any())) thenReturn Future(new CacheMap("id", Map()))
  }

  val formProvider = new ClaimantFormProvider()
  val form = formProvider()

  implicit lazy val materializer = app.materializer

  "Claimant Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder().build()

      implicit lazy val materializer = application.materializer

      val request = FakeRequest(GET, claimantRoute.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ClaimantView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(fakeRequest, messages).toString

      application.stop
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()

      val request = FakeRequest(GET, claimantRoute.url)
        .withFormUrlEncodedBody(("value", claimant.toString))

      val result = route(application, request).value

      val view = application.injector.instanceOf[ClaimantView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(claimant))(fakeRequest, messages).toString

      application.stop
    }

    "redirect to the next page when valid data is submitted" in {
      val application = applicationBuilder()
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[DataCacheConnector].toInstance(mockDataCacheConnector)
      ).build()

      val request = FakeRequest(POST, claimantRoute.url)
        .withFormUrlEncodedBody(("value", claimant.toString))

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)

      application.stop
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build
      val view = application.injector.instanceOf[ClaimantView]
      val request = FakeRequest(POST, claimantRoute.url)
        .withFormUrlEncodedBody(("value", "invalid value"))
      val result = route(application, request).value
      val boundForm = form.bind(Map("value" -> "invalid value"))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe
        view.apply(boundForm)(fakeRequest, messages).toString

      application.stop
    }
  }
}