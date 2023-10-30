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
import forms.WhichYearsAreYouClaimingForFormProvider
import play.api.data.Form
import views.behaviours.RadioOptionViewBehaviours
import views.html.WhichYearsAreYouClaimingForView

class WhichYearsAreYouClaimingForViewSpec extends RadioOptionViewBehaviours {

  val messageKeyPrefix = s"whichYearsAreYouClaimingFor"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[WhichYearsAreYouClaimingForView]

  val form: Form[Int] = new WhichYearsAreYouClaimingForFormProvider()()

  def createView(isSaUser: Boolean, form: Form[_]) = view.apply(form, isSaUser)(fakeRequest, messages)

  val numberOfOptions: Int = 3

  "WhichYearsAreYouClaimingFor view" when {

    "the user won't complete their Self-Assessment return for this tax year" must {
      behave like normalPage(createView(false, form), messageKeyPrefix)
      behave like radioOptionPage(createView(false, _), messageKeyPrefix)
      behave like pageWithBackLink(createView(false, form))
    }

    "the user will complete their Self-Assessment return for this tax year" must {
      behave like normalPage(createView(true, form), messageKeyPrefix)
      behave like radioOptionPage(createView(true, _), messageKeyPrefix)
      behave like pageWithBackLink(createView(true, form))

      "show SA-specific hint text" in {
        val doc = asDocument(createView(true, form))
        assertContainsMessages(doc, s"whichYearsAreYouClaimingFor.current.tax.year.info.text")
        assertContainsMessages(doc, s"whichYearsAreYouClaimingFor.previous.tax.year.info.text")
        assertContainsMessages(doc, s"whichYearsAreYouClaimingFor.both.tax.years.info.text.1")
        assertContainsMessages(doc, s"whichYearsAreYouClaimingFor.both.tax.years.info.text.2")
      }
    }
  }

  application.stop()
}