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

package utils

import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import identifiers._
import models.Claimant.You
import models.ClaimingFor.{HomeWorking, UniformsClothingTools}
import models.EmployerPaid.{NoExpenses, SomeExpenses}
import models.{Claimant, ClaimingFor, EmployerPaid}
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator()(frontendAppConfig)

  ".firstPage" must {
    "go to the ClaimingFor page" in {
      navigator.firstPage mustBe routes.ClaimingForController.onPageLoad()
    }
  }

  ".changeOtherExpensesPage" must {
    "go to ChangeOtherExpenses" in {
      navigator.changeOtherExpensesPage mustBe routes.ChangeOtherExpensesController.onPageLoad()
    }
  }

  ".changeUniformsWorkClothingToolsPage" must {
    "go to ChangeUniformsWorkClothingToolsController" in {
      navigator.changeUniformsWorkClothingToolsPage mustBe routes.ChangeUniformsWorkClothingToolsController.onPageLoad()
    }
  }

  "Navigator" must {
    "go to the Index view" when {
      "an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier)(mock[UserAnswers]) mustBe
          routes.IndexController.onPageLoad
      }
    }

    "go to the PaidTaxInRelevantYear view" when {
      "navigating from the claimant view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant)
          .thenReturn(Some(Claimant.You))
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel)))

        navigator.nextPage(ClaimantId)(mockAnswers) mustBe
          routes.PaidTaxInRelevantYearController.onPageLoad()
      }
    }

    "go to the ClaimingOverPayAsYouEarnThreshold view" when {
      "answering No from the RegisterForSelfAssessment view" when {
        "and not claiming for working from home only expenses" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.claimAnyOtherExpense).thenReturn(Some(false))
          when(mockAnswers.claimingFor).thenReturn(Some(List()))
          when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(false))

          navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
            routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
        }
      }
    }

    "go to the UseSelfAssessment view" when {
      "answering Yes from the RegisterForSelfAssessment view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(HomeWorking, UniformsClothingTools)))
        when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(true))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.UseSelfAssessmentController.onPageLoad()
      }
    }

    "go to the ClaimingOverPayAsYouEarnThreshold view" when {
      "answering No from the RegisterForSelfAssessment when HomeWorking is not selected" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(UniformsClothingTools)))
        when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(false))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
      }
    }

    "go to the WhichYearsAreYouClaimingFor view" when {
      "answering No from the RegisterForSelfAssessment when HomeWorking is selected" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(HomeWorking, UniformsClothingTools)))
        when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(false))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.WhichYearsAreYouClaimingForController.onPageLoad()
      }

      "answering No from the RegisterForSelfAssessment when no claims are selected (started from wfh entry)" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(None)
        when(mockAnswers.claimAnyOtherExpense).thenReturn(Some(true))
        when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(false))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.WhichYearsAreYouClaimingForController.onPageLoad()
      }
    }

    "go to the InformCustomerClaimNowInWeeks view" when {
      "answering 'yes' to which years" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimAnyOtherExpense).thenReturn(None)
        when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(true))
        when(mockAnswers.whichYearsAreYouClaimingFor).thenReturn(Some(true))

        navigator.nextPage(WhichYearsAreYouClaimingForId)(mockAnswers) mustBe
          routes.InformCustomerClaimNowInWeeksController.onPageLoad()
      }
    }

    "go to the EmployerPaidBackWfhExpenses view" when {
      "answering 'no' to which years" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimAnyOtherExpense).thenReturn(None)
        when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(false))
        when(mockAnswers.whichYearsAreYouClaimingFor).thenReturn(Some(false))

        navigator.nextPage(WhichYearsAreYouClaimingForId)(mockAnswers) mustBe
          routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
      }
    }

    "go to the Disclaimer view" when {
      "claiming for only WFH expenses" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))
        when(mockAnswers.claimAnyOtherExpense).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(Claimant.You))

        navigator.nextPage(ClaimantId)(mockAnswers) mustBe
          routes.DisclaimerController.onPageLoad()
      }
    }

    "go to the RegisteredForSelfAssessment view" when {
      "navigating from NotEntitledSomeYears" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(NotEntitledSomeYearsId)(mockAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }

      "answering Yes from the WillPayTax view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.willPayTax).thenReturn(Some(true))

        navigator.nextPage(WillPayTaxId)(mockAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }

      "navigating from the WillNotPayTax view" in {
        val mockAnswers = mock[UserAnswers]

        navigator.nextPage(WillNotPayTaxId)(mockAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }
    }

    "go to the RegisterForSelfAssessment view" when {
      "answering Yes from the ClaimingOverPayAsYouEarnThreshold view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingOverPayAsYouEarnThreshold).thenReturn(Some(true))

        navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
          routes.RegisterForSelfAssessmentController.onPageLoad()
      }
    }

    "go to the CannotClaimReliefTooLongAgo view" when {
      "answering No from the PaidTaxInRelevantYear view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.paidTaxInRelevantYear).thenReturn(Some(false))

        navigator.nextPage(PaidTaxInRelevantYearId)(mockAnswers) mustBe
          routes.CannotClaimReliefTooLongAgoController.onPageLoad()
      }
    }

    "go to the Empl" +
      "oyerPaidBackExpenses view" when {
        "answering No from the ClaimingOverPayAsYouEarnThreshold view" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.claimingOverPayAsYouEarnThreshold).thenReturn(Some(false))

          navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
            routes.EmployerPaidBackAnyExpensesController.onPageLoad()
        }

        "navigating from the RegisterForSelfAssessment view" in {
          val mockAnswers = mock[UserAnswers]

          navigator.nextPage(RegisterForSelfAssessmentId)(mockAnswers) mustBe
            routes.EmployerPaidBackAnyExpensesController.onPageLoad()
        }
      }

    "go to the UsePrintAndPost view" when {
      "answering Yes from the MoreThanFiveJobs view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.moreThanFiveJobs).thenReturn(Some(true))
        when(mockAnswers.claimingFor).thenReturn(None)
        when(mockAnswers.claimingMileage).thenReturn(None)
        when(mockAnswers.claimingFuel).thenReturn(None)
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering No from the MoreThanFiveJobs view and onlineJourneyShutterEnabled FS is set to true" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)

        val mockAnswers = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.moreThanFiveJobs).thenReturn(Some(false))
        when(mockAnswers.claimingFor).thenReturn(None)
        when(mockAnswers.claimingMileage).thenReturn(None)
        when(mockAnswers.claimingFuel).thenReturn(None)
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering MileageFuel and another option from the ClaimingFor view and the claimant is You is and onlineJourneyShutterEnabled FS is set to true" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.UseOwnCarController.onPageLoad()
      }

      "go to the UseOwnCar view" when {
        "answering MileageFuel form the ClaimingFor view and onlineJourneyShutterEnabled FS is set to true" in {
          val mockAppConfig = mock[FrontendAppConfig]
          val navigator     = new Navigator()(mockAppConfig)
          val mockAnswers   = mock[UserAnswers]
          when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
          when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))
          when(mockAnswers.claimant).thenReturn(Some(You))
          when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

          navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
            routes.UseOwnCarController.onPageLoad()
        }
      }

      "answering Yes from the MoreThanFiveJobs view and claiming for uniforms clothing tools only and " +
        "freOnlyJourneyEnabled FS is set to true" in {
          val mockAppConfig = mock[FrontendAppConfig]
          val navigator     = new Navigator()(mockAppConfig)

          val mockAnswers = mock[UserAnswers]
          when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(mockAnswers.moreThanFiveJobs).thenReturn(Some(true))
          when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.UniformsClothingTools)))
          when(mockAnswers.claimingMileage).thenReturn(None)
          when(mockAnswers.claimingFuel).thenReturn(None)
          when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)

          navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
            routes.UsePrintAndPostController.onPageLoad()
        }

      "answering anything other than unifroms clothing tools from the ClaimingFor view and freOnlyJourneyEnabled FS is set to true" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.FeesSubscriptions, ClaimingFor.TravelExpenses)))
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering Yes some of my expenses from the EmployerPaidBackAnyExpenses view and freOnlyJourneyEnabled FS is set to true" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.FeesSubscriptions)))
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(SomeExpenses))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering No from the MoreThanFiveJobs view and " +
        "freOnlyJourneyEnabled is set to true" in {
          val mockAppConfig = mock[FrontendAppConfig]
          val navigator     = new Navigator()(mockAppConfig)
          val mockAnswers   = mock[UserAnswers]
          when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(mockAnswers.moreThanFiveJobs).thenReturn(Some(false))
          when(mockAnswers.claimingFor).thenReturn(None)
          when(mockAnswers.claimingMileage).thenReturn(None)
          when(mockAnswers.claimingFuel).thenReturn(None)
          when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)
          navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
            routes.UsePrintAndPostController.onPageLoad()
        }
    }

    "go to the claim by post gov.uk page" when {
      "ansering Someone Else on the Claimant view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(Claimant.SomeoneElse))
        when(mockAnswers.claimingFor).thenReturn(Some(List()))
        navigator.nextPage(ClaimantId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.printAndPostGuidance()
      }
    }

    "go to ClaimOnline view" when {
      "answering No from the MoreThanFiveJobs view and onlineJourneyShutterEnabled FS is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)

        val mockAnswers = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.moreThanFiveJobs).thenReturn(Some(false))
        when(mockAnswers.claimingFor).thenReturn(None)
        when(mockAnswers.claimingMileage).thenReturn(None)
        when(mockAnswers.claimingFuel).thenReturn(None)
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.ClaimOnlineController.onPageLoad()
      }

      "answering No from the MoreThanFiveJobs view and freOnlyJourneyEnabled and onlineJourneyShutterEnabled is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)

        val mockAnswers = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.moreThanFiveJobs).thenReturn(Some(false))
        when(mockAnswers.claimingFor).thenReturn(None)
        when(mockAnswers.claimingMileage).thenReturn(None)
        when(mockAnswers.claimingFuel).thenReturn(None)
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.ClaimOnlineController.onPageLoad()
      }

      "answering No from the MoreThanFiveJobs view and claiming for uniforms clothing tools only and " +
        "freOnlyJourneyEnabled is set to true" in {
          val mockAppConfig = mock[FrontendAppConfig]
          val navigator     = new Navigator()(mockAppConfig)

          val mockAnswers = mock[UserAnswers]
          when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(mockAnswers.moreThanFiveJobs).thenReturn(Some(false))
          when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.UniformsClothingTools)))
          when(mockAnswers.claimingMileage).thenReturn(None)
          when(mockAnswers.claimingFuel).thenReturn(None)
          when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)

          navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
            routes.ClaimOnlineController.onPageLoad()
        }
    }

    "go to CannotClaimRelief view" when {
      "answering Yes all the expenses from the EmployerPaidBackAnyExpenses view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(EmployerPaid.AllExpenses))
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.CannotClaimReliefController.onPageLoad()
      }

      "answering Yes all expenses from the EmployerPaidBackAnyExpenses view" when {
        "claiming for working from home only expenses" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(EmployerPaid.AllExpenses))
          when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))

          navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
            routes.CannotClaimWFHReliefController.onPageLoad()
        }
      }
    }

    "go to the ClaimOnline view" when {
      "answering Working from Home from the ClaimingFor view and the claimant is You and onlineJourneyShutterEnabled FS is set to true" in {
        // val mockAnswers = mock[UserAnswers]
        // when(mockAnswers.claimingFor).thenReturn(Some(ClaimingFor.HomeWorking :: Nil))

        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAnswers.claimingFor).thenReturn(Some(ClaimingFor.HomeWorking :: Nil))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.ClaimantController.onPageLoad()
      }

      "answering Working from Home from the ClaimingFor view and the claimant is You and onlineJourneyShutterEnabled FS is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAnswers.claimingFor).thenReturn(Some(ClaimingFor.HomeWorking :: Nil))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.ClaimAnyOtherExpenseController.onPageLoad()
      }

    }

    "go to the MoreThanFiveJobs view" when {

      "answering anything other than MileageFuel from the ClaimingFor view and the claimant is You view and onlineJourneyShutterEnabled FS is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.FeesSubscriptions)))
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering MileageFuel and another option from the ClaimingFor view and the claimant is You is and onlineJourneyShutterEnabled FS is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.UseOwnCarController.onPageLoad()
      }

      "answering No to useCompanyCar, having answered Yes to ClaimingMileage, when the claimant is You, journey shutter is 'false'" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(mockAnswers.useCompanyCar).thenReturn(Some(false))
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.claimingMileage).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering Yes to ClaimingFuel when the claimant is You, journey shutter is 'false'" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(mockAnswers.claimingFuel).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering No to ClaimingFuel, having previously answered Yes to ClaimingMileage, when the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.claimingMileage).thenReturn(Some(true))
        when(mockAnswers.claimingFuel).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "going via the ChangeOtherExpenses route with the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ChangeOtherExpensesId)(mockAnswers) mustBe
          routes.ClaimantController.onPageLoad()
      }

      "going via the ChangeUniformsWorkClothingTools route with the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ChangeUniformsWorkClothingToolsId)(mockAnswers) mustBe
          routes.ClaimantController.onPageLoad()
      }

      "answering unifroms clothing tools from the ClaimingFor view and freOnlyJourneyEnabled FS is set to true" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.UniformsClothingTools)))
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering any expenses from the ClaimingFor view and freOnlyJourneyEnabled and onlineJourneyShutterEnabled is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.UniformsClothingTools)))
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering Yes some of my expenses from the EmployerPaidBackAnyExpenses view and onlineJourneyShutterEnabled FS is set to true" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(SomeExpenses))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering Yes some of my expenses from the EmployerPaidBackAnyExpenses view and onlineJourneyShutterEnabled FS is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(SomeExpenses))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering No from the EmployerPaidBackAnyExpenses view" when {
        "claiming for working from home only expenses  and onlineJourneyShutterEnabled FS is set to true" in {
          val mockAppConfig = mock[FrontendAppConfig]
          val navigator     = new Navigator()(mockAppConfig)
          val mockAnswers   = mock[UserAnswers]
          when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
          when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))
          when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(NoExpenses))

          navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
            routes.MoreThanFiveJobsController.onPageLoad()
        }
      }

      "claiming for working from home only expenses and onlineJourneyShutterEnabled FS is set to false" in {
        val mockAppConfig = mock[FrontendAppConfig]
        val navigator     = new Navigator()(mockAppConfig)
        val mockAnswers   = mock[UserAnswers]
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(NoExpenses))

        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }
    }
  }

  "go to the UseOwnCar view" when {
    "answering MileageFuel form the ClaimingFor view and onlineJourneyShutterEnabled FS is set to false" in {
      val mockAppConfig = mock[FrontendAppConfig]
      val navigator     = new Navigator()(mockAppConfig)
      val mockAnswers   = mock[UserAnswers]
      when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
      when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))
      when(mockAnswers.claimant).thenReturn(Some(You))
      when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

      navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
        routes.UseOwnCarController.onPageLoad()
    }
  }

  "go to the ClaimingMileage view" when {
    "answering Yes from the UseOwnCar view" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.useOwnCar).thenReturn(Some(true))
      when(mockAnswers.claimant).thenReturn(Some(You))

      navigator.nextPage(UseOwnCarId)(mockAnswers) mustBe
        routes.ClaimingMileageController.onPageLoad()
    }
  }

  "go to the UseCompanyCar view" when {
    "answering No from the UseOwnCar view" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.useOwnCar).thenReturn(Some(false))
      when(mockAnswers.claimant).thenReturn(Some(You))

      navigator.nextPage(UseOwnCarId)(mockAnswers) mustBe
        routes.UseCompanyCarController.onPageLoad()
    }

    "navigating from the ClaimingMileage view" in {
      navigator.nextPage(ClaimingMileageId)(mock[UserAnswers]) mustBe
        routes.UseCompanyCarController.onPageLoad()
    }
  }

  "go to the CannotClaimMileage view" when {
    "answering No to UseCompanyCar and having previously answered No to UseOwnCar" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimingFor)
        .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
      when(mockAnswers.useOwnCar).thenReturn(Some(false))
      when(mockAnswers.useCompanyCar).thenReturn(Some(false))
      when(mockAnswers.claimant).thenReturn(Some(You))

      navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
        routes.MoreThanFiveJobsController.onPageLoad()
    }

    "answering No to UseCompanyCar, having previously answered Yes to UseOwnCar and No to ClaimingMileage" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimingFor)
        .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
      when(mockAnswers.useOwnCar).thenReturn(Some(true))
      when(mockAnswers.claimingMileage).thenReturn(Some(false))
      when(mockAnswers.useCompanyCar).thenReturn(Some(false))
      when(mockAnswers.claimant).thenReturn(Some(You))

      navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
        routes.MoreThanFiveJobsController.onPageLoad()
    }

    "answering anything other than MileageFuel from the ClaimingFor view and the claimant is You view and onlineJourneyShutterEnabled FS is set to true" in {
      val mockAppConfig = mock[FrontendAppConfig]
      val navigator     = new Navigator()(mockAppConfig)
      val mockAnswers   = mock[UserAnswers]
      when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.FeesSubscriptions)))
      when(mockAnswers.claimant).thenReturn(Some(You))
      when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaid))

      navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
        routes.MoreThanFiveJobsController.onPageLoad()
    }
  }

  "go to the ClaimingFuel view" when {
    "answering Yes to UseCompanyCar" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimingFor)
        .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
      when(mockAnswers.useCompanyCar).thenReturn(Some(true))
      when(mockAnswers.claimant).thenReturn(Some(You))

      navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
        routes.ClaimingFuelController.onPageLoad()
    }
  }

  "go to the CannotClaimMileageFuelCosts view" when {
    "answering No to ClaimingFuel, having already answered No to UseOwnCar" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimingFor)
        .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
      when(mockAnswers.claimingFuel).thenReturn(Some(false))
      when(mockAnswers.useOwnCar).thenReturn(Some(false))

      navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
        routes.MoreThanFiveJobsController.onPageLoad()
    }

    "answering No to ClaimingFuel, having already answered Yes to UseOwnCar and No to ClaimingMileage" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimingFor)
        .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
      when(mockAnswers.claimingFuel).thenReturn(Some(false))
      when(mockAnswers.useOwnCar).thenReturn(Some(true))
      when(mockAnswers.claimingMileage).thenReturn(Some(false))

      navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
        routes.MoreThanFiveJobsController.onPageLoad()
    }
  }

  "go to WillNotPayTax view" when {
    "answering No to WillPayTax" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimant).thenReturn(Some(You))
      when(mockAnswers.willPayTax).thenReturn(Some(false))

      navigator.nextPage(WillPayTaxId)(mockAnswers) mustBe
        routes.WillNotPayTaxController.onPageLoad()
    }
  }

  "go to Claimant view" when {
    "answering anything other than WFH from the ClaimingFor view" in {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))

      navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
        routes.ClaimantController.onPageLoad()
    }

    "go to the WillPayTax controller" when {
      "answering Yes to PaidTaxInRelevantYearController" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.paidTaxInRelevantYear).thenReturn(Some(true))

        navigator.nextPage(PaidTaxInRelevantYearId)(mockAnswers) mustBe
          routes.WillPayTaxController.onPageLoad()
      }
    }

    "go to SessionExpired controller" when {
      val mockAnswers = mock[UserAnswers]
      when(mockAnswers.claimAnyOtherExpense).thenReturn(Some(false))

      "no data from RegisteredForSelfAssessment" in {
        when(mockAnswers.claimingFor).thenReturn(None)
        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from ClaimingOverPayAsYouEarnThreshold" in {
        navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from MoreThanFiveJobs" in {
        when(mockAnswers.claimingFor).thenReturn(None)
        when(mockAnswers.claimingMileage).thenReturn(None)
        when(mockAnswers.claimingFuel).thenReturn(None)
        when(mockAnswers.employerPaidBackAnyExpenses).thenReturn(None)
        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from EmployerPaidBackAnyExpenses" in {
        navigator.nextPage(EmployerPaidBackAnyExpensesId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from PaidTaxInRelevantYear" in {
        navigator.nextPage(PaidTaxInRelevantYearId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from ClaimingFor" in {
        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from UseOwnCar" in {
        navigator.nextPage(UseOwnCarId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from UseCompanyCar" in {
        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "Missing data from useCompanyCar" in {
        val someAnswers = mock[UserAnswers]
        when(someAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(someAnswers.useCompanyCar).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(UseCompanyCarId)(someAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from ClaimingFuel" in {
        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "missing data from ClaimingFuel(false)" in {
        val someAnswers = mock[UserAnswers]
        when(someAnswers.claimingFor)
          .thenReturn(Some(List(ClaimingFor.MileageFuel, ClaimingFor.BuyingEquipment)))
        when(someAnswers.claimingFuel).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingFuelId)(someAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }

      "no data from WillPayTax" in {
        navigator.nextPage(WillPayTaxId)(mockAnswers) mustBe
          routes.SessionExpiredController.onPageLoad
      }
    }

  }

}
