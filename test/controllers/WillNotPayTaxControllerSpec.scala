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

import controllers.actions._
import identifiers.ClaimantId
import models.Claimant.You
import play.api.libs.json.{JsArray, JsString}
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.FakeNavigator
import views.html.willNotPayTax

class WillNotPayTaxControllerSpec extends ControllerSpecBase {

  val claimant = You

  def onwardRoute = routes.IndexController.onPageLoad()

  val getValidPrecursorData = new FakeDataRetrievalAction(
    Some(
      CacheMap(
        cacheMapId,
        Map(
          ClaimantId.toString -> JsString(claimant.toString)
        )
      )
    )
  )

  def controller(dataRetrievalAction: DataRetrievalAction = getValidPrecursorData) =
    new WillNotPayTaxController(frontendAppConfig, messagesApi, new FakeNavigator(onwardRoute), dataRetrievalAction, new DataRequiredActionImpl,
      new GetClaimantActionImpl)

  def viewAsString() = willNotPayTax(frontendAppConfig, claimant, onwardRoute)(fakeRequest, messages).toString

  "WillNotPayTax Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
