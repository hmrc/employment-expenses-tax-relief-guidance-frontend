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

import org.jsoup.nodes.Element
import play.twirl.api.Html
import viewmodels.OnwardJourney
import views.behaviours.ViewBehaviours
import views.html.ClaimOnlineView

class ClaimOnlineViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "claimOnline"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[ClaimOnlineView]

  def createView: Html = view.apply(OnwardJourney.IForm)(fakeRequest, messages)

  "ClaimOnline view" must {
    "When the onward journey is IForm Include a call to action button with the correct link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.IForm)(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be("https://www.gov.uk/guidance/claim-income-tax-relief-for-your-employment-expenses-p87#claim-online")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
    }

    "When the onward journey is Employee Expenses Include a call to action button with the correct link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.FixedRateExpenses)(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be("https://www.tax.service.gov.uk/employee-expenses")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
    }

    "When the onward journey is Professional Subscriptions Include a call to action button with a link to the IForm which can be replaced by Optimizely" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.ProfessionalSubscriptions)(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be("https://www.tax.service.gov.uk/professional-subscriptions")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
    }

    "When the onward journey is Employee Working From Home Expenses Include a call to action button with the correct link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.WorkingFromHomeExpensesOnly)(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be("https://www.tax.service.gov.uk/employee-working-from-home-expenses")
      assertPageTitleEqualsMessage(doc, "claimOnline.wfh.heading")
    }

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  application.stop
}
