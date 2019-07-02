/*
 * Copyright 2019 HM Revenue & Customs
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
import forms.WillPayTaxFormProvider
import models.Claimant.You
import play.twirl.api.Html
import views.behaviours.YesNoViewBehaviours
import views.html.willPayTax

class WillPayTaxViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "willPayTax.you"

  val form = new WillPayTaxFormProvider()(You, frontendAppConfig.earliestTaxYear)

  def createView: () => Html = () => willPayTax(frontendAppConfig, form, You)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => Html = (form: Form[_]) => willPayTax(frontendAppConfig, form, You)(fakeRequest, messages)

  "WillPayTax view" must {

    "have the correct banner title" in {
      val doc = asDocument(createView())
      assertRenderedById(doc, "pageTitle")
    }

    "display the correct browser title" in {
      val doc = asDocument(createView())
      val expectedFullTitle = getFullTitle(s"$messageKeyPrefix.title")
      assertEqualsMessage(doc, "title", expectedFullTitle)
    }

    "display the correct heading" in {
      val doc = asDocument(createView())
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
    }

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.WillPayTaxController.onSubmit().url
    )

    behave like pageWithBackLink(createView)
  }
}
