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
import models.ClaimingFor.{BuyingEquipment, FeesSubscriptions, HomeWorking, MileageFuel, Other, TravelExpenses, UniformsClothingTools}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import views.behaviours.NewViewBehaviours
import views.html.UsePrintAndPostDetailedView

class UsePrintAndPostDetailedViewSpec extends NewViewBehaviours with MockitoSugar{

  val messageKeyPrefix = "usePrintAndPostDetailed"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[UsePrintAndPostDetailedView]

  val claimingListFor =  List(
    HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses, FeesSubscriptions, BuyingEquipment, Other
  )

  def createView = view.apply(claimingListFor)(fakeRequest, messages)

  val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  val freJourneyApplication = applicationBuilder()
    .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
    .build()

  val freJourneyview = freJourneyApplication.injector.instanceOf[UsePrintAndPostDetailedView]

  val claimHomeWorking = List(HomeWorking)

  def workingHomeView = freJourneyview.apply(claimHomeWorking)(fakeRequest, messages)

  "UsePrintAndPost view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  "when freJourneyEnabled is disabled- all old content is displayed for only WorkingHome" in {
    when(mockAppConfig.employeeExpensesClaimByPostUrl).thenReturn("urls.employeeExpensesClaimByPostUrl")
    when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
    val doc = asDocument(workingHomeView)
    assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.1_old"))
    assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.2_old"))
  }
  "when freJourneyEnabled is enabled- all new content is displayed for only WorkingHome" in {
    when(mockAppConfig.employeeExpensesClaimByPostUrl).thenReturn("urls.employeeExpensesClaimByPostUrl")
    when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
    val doc = asDocument(workingHomeView)
    assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.1"))
    assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.2"))
    assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.3"))
  }
  application.stop()
}
