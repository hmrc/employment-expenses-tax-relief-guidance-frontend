/*
 * Copyright 2020 HM Revenue & Customs
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
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import controllers.routes
import identifiers._
import models.{Claimant, ClaimingFor}
import models.Claimant.{SomeoneElse, You}
import models.ClaimingFor.BuyingEquipment

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  ".firstPage" must {
    "go to the Claimant page" in {
      navigator.firstPage mustBe routes.ClaimantController.onPageLoad()
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
          routes.IndexController.onPageLoad()
      }
    }

    "go to the PaidTaxInRelevantYear view" when {
      "navigating from the claimant view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant)
          .thenReturn(Some(Claimant.You))

        navigator.nextPage(ClaimantId)(mockAnswers) mustBe
          routes.PaidTaxInRelevantYearController.onPageLoad()
      }
    }

    "go to the ClaimingOverPayAsYouEarnThreshold view" when {
      "answering No from the RegisterForSelfAssessment view" when {
        "and not claiming for working from home only expenses" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.onlyWorkingFromHomeExpenses).thenReturn(Some(false))
          when(mockAnswers.registeredForSelfAssessment).thenReturn(Some(false))

          navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
            routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
        }
      }
    }

    "go to the UseSelfAssessment view" when {
      "answering Yes from the RegisterForSelfAssessment view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.registeredForSelfAssessment)
          .thenReturn(Some(true))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.UseSelfAssessmentController.onPageLoad()
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

      "answering Yes to the OnlyWorkingFromHomeView" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.onlyWorkingFromHomeExpenses).thenReturn(Some(true))

        navigator.nextPage(OnlyWorkingFromHomeExpensesId)(mockAnswers) mustBe
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

    "go to the EmployerPaidBackExpenses view" when {
      "answering No from the ClaimingOverPayAsYouEarnThreshold view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingOverPayAsYouEarnThreshold).thenReturn(Some(false))

        navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
          routes.EmployerPaidBackExpensesController.onPageLoad()
      }

      "navigating from the RegisterForSelfAssessment view" in {
        val mockAnswers = mock[UserAnswers]

        navigator.nextPage(RegisterForSelfAssessmentId)(mockAnswers) mustBe
          routes.EmployerPaidBackExpensesController.onPageLoad()
      }
    }

    "go to the UsePrintAndPost view" when {
      "answering Yes from the MoreThanFiveJobs view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.moreThanFiveJobs).thenReturn(Some(true))

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering anything other than MileageFuel from the ClaimingFor view and the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.FeesSubscriptions)))
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering MileageFuel and another option from the ClaimingFor view and the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(
            ClaimingFor.MileageFuel,
            ClaimingFor.BuyingEquipment))
          )
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering No to UseCompanyCar, having answered Yes to ClaimingMileage, when the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.useCompanyCar).thenReturn(Some(false))
        when(mockAnswers.claimingMileage).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering Yes to ClaimingFuel when the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFuel).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering No to ClaimingFuel, having previously answered Yes to ClaimingMileage when the claiming is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.claimingFuel).thenReturn(Some(false))
        when(mockAnswers.claimingMileage).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "using the ChangeOtherExpenses route when the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(ChangeOtherExpensesId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "using the ChangeUniformsWorkClothingTools route when the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(SomeoneElse))

        navigator.nextPage(ChangeUniformsWorkClothingToolsId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }
    }

    "go to ClaimOnline view" when {
      "answering No from the MoreThanFiveJobs view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.moreThanFiveJobs).thenReturn(Some(false))

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.ClaimOnlineController.onPageLoad()
      }
    }

    "go to CannotClaimRelief view" when {
      "answering Yes from the EmployerPaidBackExpenses view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.employerPaidBackExpenses).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
          routes.CannotClaimReliefController.onPageLoad()
      }

      "answering Yes from the EmployerPaidBackExpenses view" when {
        "claiming for working from home only expenses" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.employerPaidBackExpenses).thenReturn(Some(true))
          when(mockAnswers.claimant).thenReturn(Some(You))
          when(mockAnswers.onlyWorkingFromHomeExpenses).thenReturn(Some(true))

          navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
            routes.CannotClaimReliefController.onPageLoad()
        }
      }
    }

    "go to the ClaimingFor view" when {
      "answering No from the EmployerPaidBackExpenses view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.employerPaidBackExpenses).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
          routes.ClaimingForController.onPageLoad()
      }
    }

    "go to the CovidHomeWorkingController view" when {
      "answering No from the EmployerPaidBackExpenses view" when {
        "claiming for working from home only expenses" in {
          val mockAnswers = mock[UserAnswers]
          when(mockAnswers.employerPaidBackExpenses).thenReturn(Some(false))
          when(mockAnswers.claimant).thenReturn(Some(You))
          when(mockAnswers.onlyWorkingFromHomeExpenses).thenReturn(Some(true))

          navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
            routes.WfhDueToCovidController.onPageLoad()
        }
      }
      "answering Working from Home from the ClaimingFor view and the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(ClaimingFor.HomeWorking :: Nil))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.WfhDueToCovidController.onPageLoad()
      }

    }

    "go to the MoreThanFiveJobs view" when {

      "answering anything other than MileageFuel from the ClaimingFor view and the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.FeesSubscriptions)))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering MileageFuel and another option from the ClaimingFor view and the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor)
          .thenReturn(Some(List(
            ClaimingFor.MileageFuel,
            ClaimingFor.BuyingEquipment))
          )
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering No to useCompanyCar, having answered Yes to ClaimingMileage, when the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.useCompanyCar).thenReturn(Some(false))
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.claimingMileage).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering Yes to ClaimingFuel when the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFuel).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering No to WfhDueToCovid when the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.covidHomeWorking).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(CovidHomeWorkingId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "answering No to ClaimingFuel, having previously answered Yes to ClaimingMileage, when the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
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
          routes.MoreThanFiveJobsController.onPageLoad()
      }

      "going via the ChangeUniformsWorkClothingTools route with the claimant is You" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ChangeUniformsWorkClothingToolsId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }
    }

    "go to the UseOwnCar view" when {
      "answering MileageFuel form the ClaimingFor view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
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
        when(mockAnswers.useOwnCar).thenReturn(Some(false))
        when(mockAnswers.useCompanyCar).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.CannotClaimMileageCostsController.onPageLoad()
      }

      "answering No to UseCompanyCar, having previously answered Yes to UseOwnCar and No to ClaimingMileage" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.claimingMileage).thenReturn(Some(false))
        when(mockAnswers.useCompanyCar).thenReturn(Some(false))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.CannotClaimMileageCostsController.onPageLoad()
      }
    }

    "go to the ClaimingFuel view" when {
      "answering Yes to UseCompanyCar" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.useCompanyCar).thenReturn(Some(true))
        when(mockAnswers.claimant).thenReturn(Some(You))

        navigator.nextPage(UseCompanyCarId)(mockAnswers) mustBe
          routes.ClaimingFuelController.onPageLoad()
      }
    }

    "go to the CannotClaimMileageFuelCosts view" when {
      "answering No to ClaimingFuel, having already answered No to UseOwnCar" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFuel).thenReturn(Some(false))
        when(mockAnswers.useOwnCar).thenReturn(Some(false))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.CannotClaimMileageFuelCostsController.onPageLoad()
      }

      "answering No to ClaimingFuel, having already answered Yes to UseOwnCar and No to ClaimingMileage" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingFuel).thenReturn(Some(false))
        when(mockAnswers.useOwnCar).thenReturn(Some(true))
        when(mockAnswers.claimingMileage).thenReturn(Some(false))

        navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
          routes.CannotClaimMileageFuelCostsController.onPageLoad()
      }
    }

    "go to the CannotClaimBuyingEquipment view" when {
      "choosing only BuyingEquipment from the ClaimingFor view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant).thenReturn(Some(You))
        when(mockAnswers.claimingFor).thenReturn(Some(List(BuyingEquipment)))

        navigator.nextPage(ClaimingForId)(mockAnswers) mustBe
          routes.CannotClaimBuyingEquipmentController.onPageLoad()
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
      "answering No from the OnlyWorkingFromHomeExpenses view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.onlyWorkingFromHomeExpenses).thenReturn(Some(false))

        navigator.nextPage(OnlyWorkingFromHomeExpensesId)(mockAnswers) mustBe
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
        when(mockAnswers.claimant).thenReturn(Some(You))

        "no data from RegisteredForSelfAssessment" in {
          navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "no data from ClaimingOverPayAsYouEarnThreshold" in {
          navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "no data from MoreThanFiveJobs" in {
          navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "no data from EmployerPaidBackExpenses" in {
          navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
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
          when(someAnswers.useCompanyCar) thenReturn Some(false)
          when(mockAnswers.claimant).thenReturn(Some(You))

          navigator.nextPage(UseCompanyCarId)(someAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "no data from ClaimingFuel" in {
          navigator.nextPage(ClaimingFuelId)(mockAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "missing data from ClaimingFuel(true)" in {
          val someAnswers = mock[UserAnswers]
          when(someAnswers.claimingFuel) thenReturn Some(true)

          navigator.nextPage(ClaimingFuelId)(someAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "missing data from ClaimingFuel(false)" in {
          val someAnswers = mock[UserAnswers]
          when(someAnswers.claimingFuel) thenReturn Some(false)
          when(mockAnswers.claimant).thenReturn(Some(You))

          navigator.nextPage(ClaimingFuelId)(someAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }


        "no data from WillPayTax" in {
          navigator.nextPage(WillPayTaxId)(mockAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "no data from ChangeOtherExpenses" in {
          val someAnswers = mock[UserAnswers]

          navigator.nextPage(ChangeOtherExpensesId)(someAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }

        "no data from ChangeUniformsWorkClothingTools" in {
          val someAnswers = mock[UserAnswers]

          navigator.nextPage(ChangeUniformsWorkClothingToolsId)(someAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }

    }
  }
}
