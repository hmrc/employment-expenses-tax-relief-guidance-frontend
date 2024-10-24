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

import config.FrontendAppConfig
import models.ClaimingFor
import models.ClaimingFor._
import org.jsoup.nodes.Element
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.twirl.api.Html
import viewmodels.OnwardJourney
import views.behaviours.NewViewBehaviours
import views.html.ClaimOnlineView

class ClaimOnlineViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix = "claimOnline"

  val mockAppConfig = mock[FrontendAppConfig]

  val application: Application = GuiceApplicationBuilder()
    .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig)) // Bind the mock AppConfig
    .configure("play.http.router" -> "testOnlyDoNotUseInAppConf.Routes") // Ensure routing is set up correctly
    .build()

  val view = application.injector.instanceOf[ClaimOnlineView]

  def createView(journey: OnwardJourney, selectedList: List[ClaimingFor])(implicit request: play.api.mvc.Request[_], messages: play.api.i18n.Messages) = {
    view.apply(journey, selectedList)
  }

  "ClaimOnline view when freOnlyJourneyEnabled is disabled" must {
    "When the onward journey is IForm Include a call to action button with the correct link" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAppConfig.employeeExpensesUrl).thenReturn("http://example.com/employee-expenses")
      when(mockAppConfig.professionalSubscriptionsUrl).thenReturn("http://example.com/professional-subscriptions")
      when(mockAppConfig.workingFromHomeExpensesUrl).thenReturn("http://example.com/working-from-home")
      when(mockAppConfig.employeeExpensesClaimOnlineUrl).thenReturn("/digital-forms/form/tax-relief-for-expenses-of-employment/draft/guide")


      val doc = asDocument(createView(OnwardJourney.IForm, List(HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be("/digital-forms/form/tax-relief-for-expenses-of-employment/draft/guide")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.homeWorking")
      assertContainsMessages(doc,"claimingFor.uniformsClothingTools")
      assertContainsMessages(doc,"claimingFor.mileageFuel")
      assertContainsMessages(doc,"claimingFor.travelExpenses")
      assertRenderedByAttribute(doc, "data-module", Some("hmrc-back-link"))
    }

    "When the onward journey is Employee Expenses Include a call to action button with the correct link" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAppConfig.employeeExpensesUrl).thenReturn("http://example.com/employee-expenses")
      when(mockAppConfig.professionalSubscriptionsUrl).thenReturn("http://example.com/professional-subscriptions")
      when(mockAppConfig.workingFromHomeExpensesUrl).thenReturn("http://example.com/working-from-home")

      val doc = asDocument(createView(OnwardJourney.FixedRateExpenses, List(UniformsClothingTools))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(mockAppConfig.employeeExpensesUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.uniformsClothingTools")
    }

    "When the onward journey is Professional Subscriptions Include a call to action button with a link to the IForm which can be replaced by Optimizely" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAppConfig.employeeExpensesUrl).thenReturn("http://example.com/employee-expenses")
      when(mockAppConfig.professionalSubscriptionsUrl).thenReturn("http://example.com/professional-subscriptions")
      when(mockAppConfig.workingFromHomeExpensesUrl).thenReturn("http://example.com/working-from-home")

      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.ProfessionalSubscriptions, List(FeesSubscriptions))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(mockAppConfig.professionalSubscriptionsUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.feesSubscriptions")
    }

    "When the onward journey is Employee Working From Home Expenses Include a call to action button with the correct link" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAppConfig.employeeExpensesUrl).thenReturn("http://example.com/employee-expenses")
      when(mockAppConfig.professionalSubscriptionsUrl).thenReturn("http://example.com/professional-subscriptions")
      when(mockAppConfig.workingFromHomeExpensesUrl).thenReturn("http://example.com/working-from-home")

      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.WorkingFromHomeExpensesOnly, List(HomeWorking))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(mockAppConfig.workingFromHomeExpensesUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.homeWorking")
    }

    "When the onward journey is Employee Working From Home Expenses Include a call to action button with the correct link & including wfh link" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAppConfig.employeeExpensesUrl).thenReturn("http://example.com/employee-expenses")
      when(mockAppConfig.professionalSubscriptionsUrl).thenReturn("http://example.com/professional-subscriptions")
      when(mockAppConfig.workingFromHomeExpensesUrl).thenReturn("http://example.com/working-from-home")

      val view = application.injector.instanceOf[ClaimOnlineView]
      val doc = asDocument(view(OnwardJourney.WorkingFromHomeExpensesOnly, List(HomeWorking))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(s"${mockAppConfig.workingFromHomeExpensesUrl}")
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimingFor.homeWorking")
    }

  }

  "ClaimOnline view when freOnlyJourneyEnabled is enabled" must {
    "When the onward journey is Employee Expenses Include a call to action button with the correct link" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
      when(mockAppConfig.employeeExpensesUrl).thenReturn("http://example.com/employee-expenses")
      when(mockAppConfig.professionalSubscriptionsUrl).thenReturn("http://example.com/professional-subscriptions")
      when(mockAppConfig.workingFromHomeExpensesUrl).thenReturn("http://example.com/working-from-home")

      val doc = asDocument(createView(OnwardJourney.FixedRateExpenses, List(UniformsClothingTools))(fakeRequest, messages))
      val button: Element = doc.getElementById("continue")
      button.attr("href") must be(mockAppConfig.employeeExpensesUrl)
      assertPageTitleEqualsMessage(doc, "claimOnline.heading")
      assertContainsMessages(doc,"claimOnline.fre.para1")
      assertContainsMessages(doc,"claimOnline.fre.para2")
      assertContainsMessages(doc,"claimOnline.fre.para3")
      assertContainsMessages(doc,"claimOnline.fre.button")
    }
  }
  application.stop()
}
