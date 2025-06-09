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

import forms.MultipleEmploymentsFormProvider
import models.MultipleEmployments
import play.api.Application
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.NewViewBehaviours
import views.html.MultipleEmploymentsView
class MultipleEmploymentsViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "multipleEmployments"

  val application: Application = applicationBuilder().build()

  val view: MultipleEmploymentsView = application.injector.instanceOf[MultipleEmploymentsView]

  val form = new MultipleEmploymentsFormProvider()()

  def createView(form: Form[_]): Html = view.apply(form)(fakeRequest, messages)

  "MultipleEmployments view" must {

    behave.like(normalPage(createView(form), messageKeyPrefix))
    behave.like(pageWithBackLink(createView(form)))

    "contain radio buttons for the value" in {
      val doc = asDocument(createView(form))
      for (option <- MultipleEmployments.options) {
        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
      }
    }

    for (option <- MultipleEmployments.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createView(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- MultipleEmployments.options.filterNot(_ == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
        }
      }
  }

  application.stop()
}
