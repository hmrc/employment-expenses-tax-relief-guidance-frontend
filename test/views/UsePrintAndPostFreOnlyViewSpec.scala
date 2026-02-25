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
import controllers.routes
import models.ClaimingFor.{
  BuyingEquipment,
  FeesSubscriptions,
  HomeWorking,
  MileageFuel,
  Other,
  TravelExpenses,
  UniformsClothingTools
}
import org.scalatestplus.mockito.MockitoSugar
import views.behaviours.NewViewBehaviours
import views.html.UsePrintAndPostFreOnlyView
import play.api.mvc.Call
import play.twirl.api.Html
import org.mockito.Mockito.when
import play.api.inject.bind
import uk.gov.hmrc.time.TaxYear

class UsePrintAndPostFreOnlyViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix                     = "usePrintAndPostDetailed"
  val workFromHomePolicyChangeSharedPrefix = "usePrintAndPostWfhPolicyChange.postAndForm"
  val workFromHomePolicyChangePostPrefix   = "usePrintAndPostWfhPolicyChange.post"
  val mockAppConfig: FrontendAppConfig     = mock[FrontendAppConfig]
  val claimStartYear: String               = TaxYear.current.back(4).startYear.toString

  val claimingListFor = List(
    HomeWorking,
    UniformsClothingTools,
    MileageFuel,
    TravelExpenses,
    FeesSubscriptions,
    BuyingEquipment,
    Other
  )

  private val application =
    applicationBuilder()
      .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
      .build()

  def createView(): Html = view.apply(claimingListFor)(fakeRequest, messages)

  val view: UsePrintAndPostFreOnlyView = application.injector.instanceOf[UsePrintAndPostFreOnlyView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  "The correct content is displayed for title and heading" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly")
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading_freOnly"))

  }

  "The correct content is displayed when working from home policy change is enabled" in {
    when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(true)
    when(mockAppConfig.earliestTaxYear).thenReturn(claimStartYear)
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangePostPrefix.p1"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.p1", claimStartYear))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.p2"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.bullet1"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangeSharedPrefix.bullet2"))
    assertContainsMessages(doc, messages(s"$workFromHomePolicyChangePostPrefix.submit.link"))
  }

  "The correct content is displayed for working from home when working from home policy change is disabled" in {
    when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(false)
    when(mockAppConfig.employeeExpensesClaimByPostUrl).thenReturn(
      "https://www.gov.uk/guidance/send-an-income-tax-relief-claim-for-job-expenses-by-post-or-phone"
    )
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.1_freOnly"))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.2_freOnly"))
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.homeWorking.3_freOnly"))
    assertContainsMessages(doc, messages("usePrintAndPostDetailed.link.label_freOnly"))
  }

  "The correct content is displayed for only uniformsClothingToolsView" in {
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.uniformsClothingTools.1_freOnly_iform"))

  }

}
