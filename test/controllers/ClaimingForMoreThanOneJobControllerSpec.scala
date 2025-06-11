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
import models.ClaimingForMoreThanOneJob._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import connectors.DataCacheConnector
import forms.ClaimingForMoreThanOneJobFormProvider
import identifiers.ClaimingForMoreThanOneJobId
import models.ClaimingForMoreThanOneJob
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import play.api.data.Form
import play.api.libs.json.JsString
import utils.{CacheMap, Navigator, NavigatorSupport}

import scala.concurrent.Future

class ClaimingForMoreThanOneJobControllerSpec
    extends SpecBase
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience
    with NavigatorSupport {

  def onwardRoute: Call = Call("GET", "/foo")

  private val mockDataCacheConnector = mock[DataCacheConnector]

  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(), any(), any())(any())).thenReturn(Future(new CacheMap("id", Map())))
  }

  val formProvider                          = new ClaimingForMoreThanOneJobFormProvider()
  val form: Form[ClaimingForMoreThanOneJob] = formProvider()

  lazy val claimingForMoreThanOneJobRoute: String = routes.ClaimingForMoreThanOneJobController.onPageLoad().url

  "ClaimingForMoreThanOneJob Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()

      val request = FakeRequest(GET, claimingForMoreThanOneJobRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val validData   = Map(ClaimingForMoreThanOneJobId.toString -> JsString(values.head.toString))
      val application = applicationBuilder(Some(new CacheMap(cacheMapId, validData))).build()

      val request = FakeRequest(GET, claimingForMoreThanOneJobRoute)

      val result = route(application, request).value
      status(result) mustEqual OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(Some(claimantIdCacheMap))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[DataCacheConnector].toInstance(mockDataCacheConnector)
          )
          .build()

      val request =
        FakeRequest(POST, claimingForMoreThanOneJobRoute)
          .withFormUrlEncodedBody(("value", ClaimingForMoreThanOneJob.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()

      val request =
        FakeRequest(POST, claimingForMoreThanOneJobRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(application, request).value
      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder().build()

      val request = FakeRequest(GET, claimingForMoreThanOneJobRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder().build()

      val request =
        FakeRequest(POST, claimingForMoreThanOneJobRoute)
          .withFormUrlEncodedBody(("value", ClaimingForMoreThanOneJob.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
