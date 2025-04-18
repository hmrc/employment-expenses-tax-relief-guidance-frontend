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
import forms.PaidTaxInRelevantYearFormProvider
import play.api.data.Form
import play.twirl.api.Html
import views.behaviours.NewYesNoViewBehaviours
import views.html.PaidTaxInRelevantYearView

class PaidTaxInRelevantYearViewSpec extends NewYesNoViewBehaviours {

  val messageKeyPrefix = s"paidTaxInRelevantYear"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[PaidTaxInRelevantYearView]

  val form = new PaidTaxInRelevantYearFormProvider()(frontendAppConfig.earliestTaxYear)

  def createView(form: Form[_]): Html = view.apply(form)(fakeRequest, messages)

  "PaidTaxInRelevantYear view" must {

    "have the correct banner title" in {
      val doc    = asDocument(createView(form))
      val banner = doc.select(".govuk-header__service-name")

      banner.text() mustEqual messages("site.service_name")
    }

    "display the correct browser title" in {
      val doc               = asDocument(createView(form))
      val expectedFullTitle = getFullTitle(s"$messageKeyPrefix.title", frontendAppConfig.earliestTaxYear)
      assertEqualsMessage(doc, "title", expectedFullTitle)
    }

    "display the correct heading" in {
      val doc = asDocument(createView(form))
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", frontendAppConfig.earliestTaxYear)
    }

    behave.like(
      yesNoPage(
        createView,
        messageKeyPrefix,
        routes.PaidTaxInRelevantYearController.onSubmit().url,
        frontendAppConfig.earliestTaxYear
      )
    )

    behave.like(pageWithBackLink(createView(form)))
  }

  application.stop()
}
