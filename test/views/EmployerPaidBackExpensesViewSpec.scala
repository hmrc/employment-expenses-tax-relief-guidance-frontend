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
import forms.EmployerPaidBackExpensesFormProvider
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.NewYesNoViewBehaviours
import views.html.EmployerPaidBackExpensesView

class EmployerPaidBackExpensesViewSpec extends NewYesNoViewBehaviours {

  val messageKeyPrefix = s"employerPaidBackExpenses"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[EmployerPaidBackExpensesView]

  val form = new EmployerPaidBackExpensesFormProvider()()

  def createView(form: Form[_]): Html = view.apply(form)(fakeRequest, messages)

  "EmployerPaidBackExpenses view" must {

    behave like normalPage(createView(form), messageKeyPrefix)

    behave like yesNoPage(createView, messageKeyPrefix, routes.EmployerPaidBackExpensesController.onSubmit().url)

    behave like pageWithBackLink(createView(form))
  }

  application.stop()
}
