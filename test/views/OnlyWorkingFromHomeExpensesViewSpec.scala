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

import play.api.data.Form
import controllers.routes
import forms.OnlyWorkingFromHomeExpensesFormProvider
import views.behaviours.YesNoViewBehaviours
import views.html.OnlyWorkingFromHomeExpensesView

class OnlyWorkingFromHomeExpensesViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "onlyWorkingFromHomeExpenses"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[OnlyWorkingFromHomeExpensesView]

  val form = new OnlyWorkingFromHomeExpensesFormProvider()()

  def createView(form: Form[_]) = view.apply(form)(fakeRequest, messages)

  "OnlyWorkingFromHomeExpenses view" must {

    behave like normalPage(createView(form), messageKeyPrefix)

    behave like yesNoPage(createView, messageKeyPrefix, routes.OnlyWorkingFromHomeExpensesController.onSubmit().url)

    behave like pageWithBackLink(createView(form))
  }

  application.stop
}
