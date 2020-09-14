/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.EmployerPaidBackWfhExpensesFormProvider
import play.api.Application
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.YesNoViewBehaviours
import views.html.EmployerPaidBackWfhExpensesView

class EmployerPaidBackWfhExpensesViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = s"employerPaidBackWfhExpenses.$claimant"

  val application: Application = applicationBuilder().build

  val view: EmployerPaidBackWfhExpensesView = application.injector.instanceOf[EmployerPaidBackWfhExpensesView]

  val form = new EmployerPaidBackWfhExpensesFormProvider()(claimant)

  def createView(form: Form[_]): Html = view.apply(form, claimant)(fakeRequest, messages)

  "EmployerPaidBackWfhExpenses view" must {

    behave like normalPage(createView(form), messageKeyPrefix)

    behave like yesNoPage(createView, messageKeyPrefix, routes.EmployerPaidBackWfhExpensesController.onSubmit().url)

    behave like pageWithBackLink(createView(form))
  }

  application.stop
}
