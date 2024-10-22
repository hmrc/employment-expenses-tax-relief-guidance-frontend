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
  import org.mockito.Mockito.when
  import org.scalatestplus.mockito.MockitoSugar
  import play.api.inject.bind
  import views.behaviours.NewViewBehaviours
  import views.html.UsePrintAndPostDetailedView
  import models.ClaimingFor

  class  UsePrintAndPostFreOnlyViewSpec extends NewViewBehaviours with MockitoSugar{

    val messageKeyPrefix = "usePrintAndPostFreOnlyViewSpec"

    val claimingListFor =  List(
      HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses, FeesSubscriptions, BuyingEquipment, Other
    )

    val uniformsClothingTools = List(UniformsClothingTools)
    val claimHomeWorking = List(HomeWorking)

    def createView(claimingFor: List[ClaimingFor]) = {
      val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
      /*when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(freJourneyEnabled)
      when(mockAppConfig.employeeExpensesClaimByPostUrl).thenReturn("urls.employeeExpensesClaimByPostUrl")

      val freJourneyApplication = applicationBuilder()
        .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
        .build()

      val freJourneyView = freJourneyApplication.injector.instanceOf[UsePrintAndPostDetailedView]

      val result = freJourneyView.apply(claimingFor)(fakeRequest, messages)

      freJourneyApplication.stop() // Ensure the application is stopped after the view is created
      result
    }


    "when freJourneyEnabled is disabled- all old content is displayed for only uniformsClothingToolsView" in {
      val doc = asDocument(createView(freJourneyEnabled = false )
      assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title_old")
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.uniformsClothingTools.1_old"))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.uniformsClothingTools.2_old"))
    }

    "when freJourneyEnabled is disabled- all old content is displayed for only WorkingHome" in {
      val doc = asDocument(createView(freJourneyEnabled = false ))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.1_old"))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.2_old"))
    }
    "when freJourneyEnabled is enabled- all new content is displayed for only WorkingHome" in {
      val doc = asDocument(createView(freJourneyEnabled = true))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.1"))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.2"))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.homeWorking.3"))
    }

    "when freJourneyEnabled is enabled- all new content is displayed for only uniformsClothingToolsView" in {

      val doc = asDocument(createView(freJourneyEnabled = true, ))
      assertPageTitleEqualsMessage(doc, "usePrintAndPostDetailed.title")
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.uniformsClothingTools.1"))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.uniformsClothingTools.2"))
      assertContainsMessages(doc, messages(s"${messageKeyPrefix}.uniformsClothingTools.3"))

      val button = doc.getElementById("submit")
      button.attr("href") must be("urls.employeeExpensesClaimByPostUrl")*/

    }
  }
