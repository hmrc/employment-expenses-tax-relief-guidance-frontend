/*
 * Copyright 2021 HM Revenue & Customs
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

package views

import controllers.routes
import views.behaviours.ViewBehaviours
import views.html.RegisterForSelfAssessmentView

class RegisterForSelfAssessmentViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerForSelfAssessment.you"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[RegisterForSelfAssessmentView]

  def onwardRoute = routes.IndexController.onPageLoad()

  def createView = view.apply(claimant, onwardRoute)(fakeRequest, messages)

  "RegisterForSelfAssessment view" must {

    behave like normalPage(createView, messageKeyPrefix)
    behave like pageWithBackLink(createView)
    behave like pageWithHyperLink(createView, frontendAppConfig.fileSelfAssessmentLoginUrl)
  }

  application.stop
}
