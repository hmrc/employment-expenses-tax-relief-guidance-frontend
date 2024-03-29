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

import models.ClaimingFor._
import org.jsoup.nodes.Element
import play.twirl.api.Html
import viewmodels.OnwardJourney
import views.behaviours.NewViewBehaviours
import views.html.ClaimOnlineView

class ClaimOnlineViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "claimOnline"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[ClaimOnlineView]

  def createView: Html = view.apply(OnwardJourney.IForm, List(HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses))(fakeRequest, messages)

  "ClaimOnline view" must {
    "When the onward journey is IForm Include a call to action button with the correct link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.IForm, List(HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be("/digital-forms/form/tax-relief-for-expenses-of-employment/draft/guide")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.homeWorking")
      assertContainsMessages(doc,"claimingFor.uniformsClothingTools")
      assertContainsMessages(doc,"claimingFor.mileageFuel")
      assertContainsMessages(doc,"claimingFor.travelExpenses")
    }

    "When the onward journey is Employee Expenses Include a call to action button with the correct link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.FixedRateExpenses, List(UniformsClothingTools))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(frontendAppConfig.employeeExpensesUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.uniformsClothingTools")
    }

    "When the onward journey is Professional Subscriptions Include a call to action button with a link to the IForm which can be replaced by Optimizely" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.ProfessionalSubscriptions, List(FeesSubscriptions))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(frontendAppConfig.professionalSubscriptionsUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.feesSubscriptions")
    }

    "When the onward journey is Employee Working From Home Expenses Include a call to action button with the correct link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.WorkingFromHomeExpensesOnly, List(HomeWorking))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(frontendAppConfig.workingFromHomeExpensesUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.homeWorking")
    }

    "When the onward journey is Employee Working From Home Expenses Include a call to action button with the correct link & including wfh link" in {
      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.WorkingFromHomeExpensesOnly, List(HomeWorking))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(s"${frontendAppConfig.workingFromHomeExpensesUrl}")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.homeWorking")
    }

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  application.stop()
}
