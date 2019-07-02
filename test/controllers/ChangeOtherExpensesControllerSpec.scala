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
import connectors.FakeDataCacheConnector
import controllers.actions.DataRequiredActionImpl
import org.scalatest.OptionValues
import play.api.test.Helpers._
import utils.FakeNavigator

import scala.concurrent.ExecutionContext.Implicits.global

class ChangeOtherExpensesControllerSpec extends SpecBase with OptionValues {

  def onwardRoute = routes.IndexController.onPageLoad()

  "ChangeOtherExpenses Controller" must {

    def controller() = new ChangeOtherExpensesController(
      frontendAppConfig, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), getEmptyCacheMap,
      new DataRequiredActionImpl, controllerComponents)

    "Redirect to the next page for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe onwardRoute.url
    }
  }
}
