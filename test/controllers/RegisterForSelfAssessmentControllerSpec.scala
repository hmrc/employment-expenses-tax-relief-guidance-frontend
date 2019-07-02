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
import controllers.actions._
import models.Claimant
import play.api.test.Helpers._
import utils.FakeNavigator
import views.html.registerForSelfAssessment

import scala.concurrent.ExecutionContext.Implicits.global

class RegisterForSelfAssessmentControllerSpec extends SpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getCacheMapWithClaimant(Claimant.You)) =
    new RegisterForSelfAssessmentController(frontendAppConfig, new FakeNavigator(onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl, new GetClaimantActionImpl, controllerComponents)

  def viewAsString() = registerForSelfAssessment(frontendAppConfig, Claimant.You, onwardRoute)(fakeRequest, messages).toString

  "RegisterForSelfAssessment Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
