/*
 * Copyright 2024 HM Revenue & Customs
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
import config.FrontendAppConfig
import models.ClaimingFor.{BuyingEquipment, FeesSubscriptions, HomeWorking, MileageFuel, Other, TravelExpenses, UniformsClothingTools}
import org.jsoup.nodes.Element
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Call
import play.twirl.api.Html
import views.behaviours.NewViewBehaviours
import views.html.UseIformFreOnlyView
import controllers.routes
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
class UseIformFreOnlyViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix = "usePrintAndPostDetailed"
  val mockAppConfig    = mock[FrontendAppConfig]

  val claimingListFor = List(
    HomeWorking,
    UniformsClothingTools,
    MileageFuel,
    TravelExpenses,
    FeesSubscriptions,
    BuyingEquipment,
    Other
  )

  val claimingListForHomeWorking = List(
    HomeWorking
  )

  val application = applicationBuilder()
    .build()

  def createView(): Html = view.apply(claimingListFor)(fakeRequest, messages)

  def createViewHomeworking(): Html = view.apply(claimingListForHomeWorking)(fakeRequest, messages)

  val view: UseIformFreOnlyView = application.injector.instanceOf[UseIformFreOnlyView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  "when freJourneyEnabled is enabled- all new content is displayed for title and heading" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly_iform")
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading_freOnly_iform"))

  }

  "when pegaJourneyEnabled is enabled - new content is displayed for only WorkingHome" in {
    when(mockAppConfig.pegaServiceJourney).thenReturn(true)
    when(mockAppConfig.employeeExpensesClaimByPegaServicesUrl).thenReturn(
      "https://account-np.hmrc.gov.uk/pay-as-you-earn/claim-tax-relief-for-job-expenses/dev"
    )
    val application = new GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .overrides(inject.bind[FrontendAppConfig].toInstance(mockAppConfig))
      .build()
    val view: UseIformFreOnlyView = application.injector.instanceOf[UseIformFreOnlyView]

    val doc = asDocument(view.apply(claimingListForHomeWorking)(fakeRequest, messages))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.para1_freOnly_pegaService"))
  }

  "when pegaJourneyEnabled is enabled - old content is displayed for  merged Journey" in {
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.para1_freOnly_iform"))
  }

  "when freJourneyEnabled is enabled- all new content is displayed for only WorkingHome" in {
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.1_freOnly"))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.2_freOnly"))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.3_freOnly"))
  }

  "when freJourneyEnabled is enabled- all new content is displayed for only uniformsClothingToolsView" in {

    val doc = asDocument(createView())

    assertContainsMessages(doc, messages(s"$messageKeyPrefix.uniformsClothingTools.1_freOnly_iform"))

  }

  "when freJourneyEnabled is enabled- Include a call to action button with the correct link to iForm" in {
    when(mockAppConfig.employeeExpensesClaimByIformUrl).thenReturn(
      "https://tax.service.gov.uk/digital-forms/form/tax-relief-for-expenses-of-employment/draft/guide"
    )

    val doc             = asDocument(createView())
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(mockAppConfig.employeeExpensesClaimByIformUrl)
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly_iform")

  }

  "when pegaJourneyEnabled is enabled- Include a call to action button with the correct link to Pega service" in {
    when(mockAppConfig.pegaServiceJourney).thenReturn(true)
    when(mockAppConfig.employeeExpensesClaimByPegaServicesUrl).thenReturn(
      "https://account-np.hmrc.gov.uk/pay-as-you-earn/claim-tax-relief-for-job-expenses/dev"
    )
    val application = new GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .overrides(inject.bind[FrontendAppConfig].toInstance(mockAppConfig))
      .build()
    val view: UseIformFreOnlyView = application.injector.instanceOf[UseIformFreOnlyView]
    val doc = asDocument(view.apply(claimingListForHomeWorking)(fakeRequest, messages))
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(mockAppConfig.employeeExpensesClaimByPegaServicesUrl)
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly_iform")

  }

}
