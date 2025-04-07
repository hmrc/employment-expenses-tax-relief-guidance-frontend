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
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import views.behaviours.NewViewBehaviours
import views.html.UsePrintAndPostFreOnlyView
import models.ClaimingFor
import org.jsoup.nodes.Element
import play.api.mvc.Call
import play.twirl.api.Html

class UsePrintAndPostFreOnlyViewSpec extends NewViewBehaviours with MockitoSugar {

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

  val application = applicationBuilder()
    .build()

  def createView(): Html = view.apply(claimingListFor)(fakeRequest, messages)

  val view: UsePrintAndPostFreOnlyView = application.injector.instanceOf[UsePrintAndPostFreOnlyView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  "when freJourneyEnabled is enabled- all new content is displayed for title and heading" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_freOnly")
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading_freOnly"))

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

}
