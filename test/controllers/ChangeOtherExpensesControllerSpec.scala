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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.{CacheMap, Navigator, NavigatorSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChangeOtherExpensesControllerSpec
    extends SpecBase
    with MockitoSugar
    with BeforeAndAfterEach
    with ScalaFutures
    with IntegrationPatience
    with NavigatorSupport {

  private val mockDataCacheConnector = mock[DataCacheConnector]

  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
    when(mockDataCacheConnector.save(any(), any(), any())(any())).thenReturn(Future(new CacheMap("id", Map())))
  }

  def onwardRoute = routes.IndexController.onPageLoad

  "ChangeOtherExpenses Controller" must {

    "Redirect to the next page for a GET" in {

      val application = applicationBuilder(Some(claimantIdCacheMap))
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[DataCacheConnector].toInstance(mockDataCacheConnector)
        )
        .build()

      val request = FakeRequest(GET, routes.ChangeOtherExpensesController.onPageLoad().url)

      val result = route(application, request)

      result.map { result =>
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }

      application.stop()
    }
  }

}
