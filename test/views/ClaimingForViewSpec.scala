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

import forms.ClaimingForFormProvider
import models.{Claimant, ClaimingFor}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import views.behaviours.CheckboxViewBehaviours
import views.html.ClaimingForView
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint

class ClaimingForViewSpec extends CheckboxViewBehaviours[ClaimingFor] {

  val messageKeyPrefix: String = s"claimingFor"

  val application: Application = applicationBuilder().build()

  val view: ClaimingForView = application.injector.instanceOf[ClaimingForView]

  val form = new ClaimingForFormProvider()()

  def createView(form: Form[_]): HtmlFormat.Appendable = view.apply(form)(fakeRequest, messages)

  def checkboxItem(keyPrefix: String): CheckboxItem = {
    new CheckboxItem(
      name = Some("value[0]"),
      id = Some(s"claimingFor.$keyPrefix"),
      value = keyPrefix,
      content = Text(messages(s"claimingFor.$keyPrefix")),
      hint = Some(Hint(
        content = HtmlContent(messages(s"claimingFor.$keyPrefix.$claimant.description")))
      )
    )
  }

  "ClaimingFor view" must {
    behave like normalPage(createView(form), messageKeyPrefix)
    behave like checkboxPage(form, createView, messageKeyPrefix, ClaimingFor.options())
  }

  "ClaimingFor view" when {
    "rendered" must {
      "contain checkboxes for each option" in {
        val doc = asDocument(createView(form))
        for ((option, index) <- ClaimingFor.options().zipWithIndex) {
          assertContainsRadioButton(doc, option.id.get, s"value[$index]", option.value, false)
        }
      }
    }
  }

  application.stop()
}
