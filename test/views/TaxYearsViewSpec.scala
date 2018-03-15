/*
 * Copyright 2018 HM Revenue & Customs
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
import forms.TaxYearsFormProvider
import models.Claimant.You
import models.ClaimYears
import utils.RadioOption
import views.behaviours.ViewBehaviours
import views.html.taxYears

class TaxYearsViewSpec extends ViewBehaviours {

  val claimant = You
  val messageKeyPrefix = s"taxYears.$claimant"

  val form = new TaxYearsFormProvider()(claimant)

  def createView = () => taxYears(frontendAppConfig, form, claimant)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => taxYears(frontendAppConfig, form, claimant)(fakeRequest, messages)

  "TaxYears view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "TaxYears view" when {
    "rendered" must {
      "contain checkboxes for each option" in {
        val doc = asDocument(createViewUsingForm(form))
        for ((option, index) <- ClaimYears.options.zipWithIndex) {
          assertContainsRadioButton(doc, option.id, s"value[$index]", option.value, false)
        }
      }
    }

    for((option, index) <- ClaimYears.values.zipWithIndex) {

      s"rendered with a value of '${option.toString}'" must {

        s"have the '${option.toString}' checkbox selected" in {
          val doc = asDocument(createViewUsingForm(form.fill(Set(option))))
          val radioOption = RadioOption("taxYears", option.toString)
          assertContainsRadioButton(doc, radioOption.id, s"value[$index]", option.toString, true)
        }
      }
    }
  }
}
