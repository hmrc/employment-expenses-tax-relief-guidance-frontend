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

package views

import controllers.routes
import play.api.Application
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.NewViewBehaviours
import views.html.RegisterForSelfAssessmentView

class RegisterForSelfAssessmentViewSpec extends NewViewBehaviours {

  val messageKeyPrefix: String = "registerForSelfAssessment"

  val application: Application = applicationBuilder().build()

  val view: RegisterForSelfAssessmentView = application.injector.instanceOf[RegisterForSelfAssessmentView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  def createView: HtmlFormat.Appendable = view.apply(onwardRoute)(fakeRequest, messages)

  "RegisterForSelfAssessment view" must {

    behave.like(normalPage(createView, messageKeyPrefix))
    behave.like(pageWithBackLink(createView))
    behave.like(pageWithHyperLink(createView, frontendAppConfig.fileSelfAssessmentLoginUrl))
    behave.like(
      pageWithBodyText(
        createView,
        "registerForSelfAssessment.link.external.label"
      )
    )
  }

  application.stop()
}
