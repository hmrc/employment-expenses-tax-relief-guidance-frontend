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
import forms.RegisteredForSelfAssessmentFormProvider
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.NewYesNoViewBehaviours
import views.html.RegisteredForSelfAssessmentView

class RegisteredForSelfAssessmentViewSpec extends NewYesNoViewBehaviours {

  val messageKeyPrefix = s"registeredForSelfAssessment"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[RegisteredForSelfAssessmentView]

  val form = new RegisteredForSelfAssessmentFormProvider()()

  def createView(form: Form[_]): Html = view.apply(form)(fakeRequest, messages)

  "RegisteredForSelfAssessment view" must {

    behave.like(normalPage(createView(form), messageKeyPrefix))
    behave.like(yesNoPage(createView, messageKeyPrefix, routes.RegisteredForSelfAssessmentController.onSubmit().url))
    behave.like(pageWithBackLink(createView(form)))
    behave.like(
      pageWithBodyText(
        createView(form),
        "registeredForSelfAssessment.accordion.list1.item1",
        "registeredForSelfAssessment.accordion.guidance1",
        "registeredForSelfAssessment.accordion.list1.item1",
        "registeredForSelfAssessment.accordion.list1.item2",
        "registeredForSelfAssessment.accordion.guidance2",
        "registeredForSelfAssessment.accordion.list2.item1",
        "registeredForSelfAssessment.accordion.list2.item2",
        "registeredForSelfAssessment.accordion.list2.item3",
        "registeredForSelfAssessment.accordion.list2.item4"
      )
    )
  }

  application.stop()
}
