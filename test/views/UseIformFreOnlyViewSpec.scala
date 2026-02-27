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
import play.api.inject
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

  val claimStartYear: String = TaxYear.current.back(4).startYear.toString

  val application = applicationBuilder()
    .configure("metrics.enabled" -> false)
    .overrides(inject.bind[FrontendAppConfig].toInstance(appConfig))
    .build()

  def createView(): Html = view.apply(claimingListFor)(fakeRequest, messages)

  def createViewHomeworking(): Html = view.apply(claimingListForHomeWorking)(fakeRequest, messages)

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
    when(appConfig.workingFromHomePolicyChangeEnabled).thenReturn(false)
  }

  "The correct title and header are displayed" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly_iform")
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading_freOnly_iform"))
  }

  "when pegaJourneyEnabled is enabled - new content is displayed for only WorkingHome" in {
    when(appConfig.pegaServiceJourney).thenReturn(true)

    val doc = asDocument(view.apply(claimingListForHomeWorking)(fakeRequest, messages))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.para1_freOnly_pegaService"))
  }

  "when workFromHomePolicyChange is enabled and the claim list contains multiple expenses including work from home the correct content is displayed" in {
    when(appConfig.workingFromHomePolicyChangeEnabled).thenReturn(true)
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

  "when pegaJourneyEnabled is disabled - new content is displayed for only WorkingHome" in {
    val doc = asDocument(view.apply(claimingListForHomeWorking)(fakeRequest, messages))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.para1_freOnly_iform"))
  }

  "when pegaJourneyEnabled is enabled - old content is displayed for  merged Journey" in {
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.para1_freOnly_iform"))
  }

  "The correct content is displayed" in {
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.1_freOnly"))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.2_freOnly"))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.3_freOnly"))
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
    val doc             = asDocument(view.apply(claimingListForHomeWorking)(fakeRequest, messages))
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(appConfig.employeeExpensesClaimByPegaServicesUrl)
    assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title_freOnly_iform")
  }

  "when pegaJourneyEnabled is dissabled - Include a call to action button with the correct link to IForm service" in {
    val doc             = asDocument(view.apply(claimingListForHomeWorking)(fakeRequest, messages))
    val button: Element = doc.getElementById("startyourclaim")
    button.attr("href") must be(appConfig.employeeExpensesClaimByIformUrl)
    assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title_freOnly_iform")
  }

}
