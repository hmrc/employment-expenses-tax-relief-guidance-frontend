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

import forms.ClaimAnyOtherExpenseFormProvider
import models.ClaimAnyOtherExpense
import play.api.data.Form
import views.behaviours.ViewBehaviours
import views.html.ClaimAnyOtherExpenseView

class ClaimAnyOtherExpenseViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "claimAnyOtherExpense"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[ClaimAnyOtherExpenseView]

  val form = new ClaimAnyOtherExpenseFormProvider()()

  def createView(form: Form[_]) = view.apply(form)(fakeRequest, messages)

  "ClaimAnyOtherExpenseViewSpec view" must {

    behave like normalPage(createView(form), messageKeyPrefix)

    behave like pageWithBackLink(createView(form))

    "contain radio buttons for the value" in {
      val doc = asDocument(createView(form))
      for (option <- ClaimAnyOtherExpense.options) {
        assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }
    }

    for(option <- ClaimAnyOtherExpense.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createView(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- ClaimAnyOtherExpense.options.filterNot(_ == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }


  application.stop
}
