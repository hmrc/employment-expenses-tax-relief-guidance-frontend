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
import models.ClaimingFor.{
  BuyingEquipment,
  FeesSubscriptions,
  HomeWorking,
  MileageFuel,
  Other,
  TravelExpenses,
  UniformsClothingTools
}
import org.jsoup.nodes.Element
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Call
import play.twirl.api.Html
import views.behaviours.NewViewBehaviours
import views.html.UseIformFreOnlyView
import controllers.routes
import org.scalatest.BeforeAndAfterEach
import play.api.{Application, inject}
import uk.gov.hmrc.time.TaxYear

class UseIformFreOnlyViewSpec extends NewViewBehaviours with MockitoSugar with BeforeAndAfterEach {

  val messageKeyPrefix                     = "usePrintAndPostDetailed"
  val wfhPolicyChangeFormPrefix            = "usePrintAndPostWfhPolicyChange.form"
  val workFromHomePolicyChangeSharedPrefix = "usePrintAndPostWfhPolicyChange.postAndForm"
  val appConfig                            = mock[FrontendAppConfig]

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

  val claimingListForFeesSubscriptions = List(
    FeesSubscriptions
  )

  val claimStartYear: String = TaxYear.current.back(4).startYear.toString

  val application: Application = applicationBuilder()
    .configure("metrics.enabled" -> false)
    .overrides(inject.bind[FrontendAppConfig].toInstance(appConfig))
    .build()

  def createView(): Html = view.apply(claimingListFor, appConfig.employeeExpensesClaimByIformUrl)(fakeRequest, messages)

  val view: UseIformFreOnlyView = application.injector.instanceOf[UseIformFreOnlyView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  override def beforeEach(): Unit = {
    super.beforeEach()

    when(appConfig.employeeExpensesClaimByPostUrl).thenReturn(
      "https://www.gov.uk/guidance/send-an-income-tax-relief-claim-for-job-expenses-by-post-or-phone"
    )
    when(appConfig.employeeExpensesClaimByPegaServicesUrl).thenReturn(
      "https://account-np.hmrc.gov.uk/pay-as-you-earn/claim-tax-relief-for-job-expenses/dev"
    )
    when(appConfig.employeeExpensesClaimByIformUrl).thenReturn(
      "https://tax.service.gov.uk/digital-forms/form/tax-relief-for-expenses-of-employment/draft/guide"
    )
    when(appConfig.pegaServiceJourney).thenReturn(false)
  }

  "the correct title and header are displayed" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly_iform")
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading_freOnly_iform"))
  }

  "the correct content is displayed when the claim list contains multiple expenses including work from home" in {
    when(appConfig.earliestTaxYear).thenReturn(claimStartYear)
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$wfhPolicyChangeFormPrefix.p1"))
    assertContainsMessages(doc, messages(s"$wfhPolicyChangeFormPrefix.p2"))
    assertContainsMessages(doc, messages(s"$wfhPolicyChangeFormPrefix.p3"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.p1", claimStartYear))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.p2"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.bullet1"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.bullet2"))
    doc.getElementById("startyourclaim").text mustBe messages("usePrintAndPostDetailed.link.label")
  }

  "The correct content is displayed for uniformsClothingToolsView" in {
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.uniformsClothingTools.1_freOnly_iform"))
  }

  "Include a call to action button with the correct link to iForm" in {
    val doc             = asDocument(createView())
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(appConfig.employeeExpensesClaimByIformUrl)
    assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title_freOnly_iform")
  }

  "when pegaJourneyEnabled is enabled - Include a call to action button with the correct link to Pega service" in {
    when(appConfig.pegaServiceJourney).thenReturn(true)
    val doc = asDocument(
      view.apply(claimingListForHomeWorking, appConfig.employeeExpensesClaimByPegaServicesUrl)(fakeRequest, messages)
    )
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(appConfig.employeeExpensesClaimByPegaServicesUrl)
    assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title_freOnly_iform")
  }

  "when pegaJourneyEnabled is disabled - Include a call to action button with the correct link to IForm service" in {
    val doc             = asDocument(createView())
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(appConfig.employeeExpensesClaimByIformUrl)
    assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title_freOnly_iform")
  }

  "Include a call to action button with the correct link to iForm when claiming for FeesSubscriptions" in {
    val redirectionLink = s"${appConfig.employeeExpensesClaimByIformUrl}?claiming-for=professional-fees"
    val doc = asDocument(view.apply(claimingListForFeesSubscriptions, redirectionLink)(fakeRequest, messages))
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(s"${appConfig.employeeExpensesClaimByIformUrl}?claiming-for=professional-fees")
    assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title_freOnly_iform")
  }

}
