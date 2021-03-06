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

import forms.ClaimingForFormProvider
import models.{Claimant, ClaimingFor}
import play.api.data.Form
import utils.RadioOption
import views.behaviours.ViewBehaviours
import views.html.ClaimingForView

class ClaimingForViewSpec extends ViewBehaviours {

  val messageKeyPrefix = s"claimingFor.$claimant"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[ClaimingForView]

  val form = new ClaimingForFormProvider()(claimant)

  def createView(form: Form[_]) = view.apply(form, claimant)(fakeRequest, messages)

  "ClaimingFor view" must {
    behave like normalPage(createView(form), messageKeyPrefix)
  }

  "ClaimingFor view" when {

    behave like pageWithBackLink(createView(form))

    "rendered" must {
      "contain checkboxes for each option" in {
        val doc = asDocument(createView(form))
        for ((option, index) <- ClaimingFor.options(Claimant.You).zipWithIndex) {
          assertContainsRadioButton(doc, option.id, s"value[$index]", option.value, false)
        }
      }
    }

    for((option, index) <- ClaimingFor.values.zipWithIndex) {

      s"rendered with a value of '${option.toString}'" must {

        s"have the '${option.toString}' checkbox selected" in {
          val doc = asDocument(createView(form.fill(Set(option))))
          val radioOption = RadioOption("claimingFor", option.toString)
          assertContainsRadioButton(doc, radioOption.id, s"value[$index]", option.toString, true)
        }
      }
    }
  }

  application.stop
}
