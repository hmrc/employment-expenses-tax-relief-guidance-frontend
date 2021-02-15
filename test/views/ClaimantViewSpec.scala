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

import play.api.data.Form
import forms.ClaimantFormProvider
import models.Claimant
import views.behaviours.ViewBehaviours
import views.html.ClaimantView

class ClaimantViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "claimant"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[ClaimantView]

  val form = new ClaimantFormProvider()()

  def createView(form: Form[_]) = view.apply(form)(fakeRequest, messages)

  "Claimant view" must {
    behave like normalPage(createView(form), messageKeyPrefix)
    behave like pageWithBackLink(createView(form))
  }

  "Claimant view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createView(form))
        for (option <- Claimant.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for(option <- Claimant.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createView(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for(unselectedOption <- Claimant.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }

  application.stop
}
