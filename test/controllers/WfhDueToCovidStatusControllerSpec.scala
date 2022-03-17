/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.actions.{DataRetrievalAction, FakeDataRetrievalAction}
import forms.CovidHomeWorkingFormProvider
import identifiers.{ClaimantId, ClaimingForId, ClaimingMileageId, CovidHomeWorkingId, WhichYearsAreYouClaimingForId}
import models.ClaimingFor.values
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.{JsArray, JsBoolean, JsNumber, JsString, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{FakeNavigator, Navigator}
import views.html.WfhDueToCovidView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WfhDueToCovidStatusControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach
  with ScalaFutures with IntegrationPatience {

  private val mockDataCacheConnector = mock[DataCacheConnector]

  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
  }

  val DefaultSessionId = "session-f13749ae-7f9a-4510-a65e-f247c4954c2f"

  "Covid Home Working Status Controller" must {

    "should return value from service" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString), WhichYearsAreYouClaimingForId.toString -> JsNumber(1))

      when(mockDataCacheConnector.fetchBySessionId(any())) thenReturn Future(Some(new CacheMap(cacheMapId, validData)))
      val wfhDueToCovidStatusService = new WfhDueToCovidStatusService(mockDataCacheConnector)

      val application = applicationBuilder()
        .overrides(bind[WfhDueToCovidStatusService].toInstance(wfhDueToCovidStatusService)).build

      val request = FakeRequest(GET,  routes.WfhDueToCovidStatusController.getStatusBySessionId(DefaultSessionId).toString)
      val result = route(application, request).value

      status(result) mustEqual OK
      val jsonResult = contentAsString(result)
      jsonResult mustEqual """{"WfhDueToCovidStatus":1}"""
      application.stop
    }

    "should return missing default value from service" in {

      val validData = Map(ClaimantId.toString -> JsString(claimant.toString))

      when(mockDataCacheConnector.fetchBySessionId(any())) thenReturn Future(Some(new CacheMap(cacheMapId, validData)))
      val wfhDueToCovidStatusService = new WfhDueToCovidStatusService(mockDataCacheConnector)

      val application = applicationBuilder()
        .overrides(bind[WfhDueToCovidStatusService].toInstance(wfhDueToCovidStatusService)).build

      val request = FakeRequest(GET,  routes.WfhDueToCovidStatusController.getStatusBySessionId(DefaultSessionId).toString)
      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND
      application.stop
    }

  }

  "should handle missing value or exception in service layers" in {

    when(mockDataCacheConnector.fetchBySessionId(any())) thenThrow new RuntimeException("Exception")
    val wfhDueToCovidStatusService = new WfhDueToCovidStatusService(mockDataCacheConnector)

    val application = applicationBuilder()
      .overrides(bind[WfhDueToCovidStatusService].toInstance(wfhDueToCovidStatusService)).build

    val request = FakeRequest(GET,  routes.WfhDueToCovidStatusController.getStatusBySessionId(DefaultSessionId).toString)
    val result = route(application, request).value

    status(result) mustEqual INTERNAL_SERVER_ERROR
    application.stop
  }
}
