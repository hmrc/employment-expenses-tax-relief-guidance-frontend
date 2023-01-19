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

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import identifiers._
import models.Claimant.{SomeoneElse, You}
import models.ClaimingFor
import models.EmployerPaid.{NoExpenses, SomeExpenses, AllExpenses}

@Singleton
class Navigator @Inject()() {

  private def claimingForCurrentYearControllerRouting(userAnswers: UserAnswers) =
    userAnswers.claimingForCurrentYear match {
      case Some(true) => routes.SaCheckDisclaimerCurrentYearController.onPageLoad()
      case Some(false) => routes.UseSelfAssessmentController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad
    }

  private def saCheckDisclaimerAllYearsRouting(userAnswers: UserAnswers) =
    userAnswers.claimingForCurrentYear match {
      case Some(true) => routes.SaCheckDisclaimerAllYearsController.onPageLoad()
      case Some(false) => routes.UseSelfAssessmentController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad
    }

  private def registeredForSelfAssessmentControllerRouting(userAnswers: UserAnswers) =

    userAnswers.claimAnyOtherExpense match {
      case x if x.isEmpty || x == Some(false) =>
        userAnswers.registeredForSelfAssessment match {
          case Some(true) => routes.UseSelfAssessmentController.onPageLoad()
          case Some(false) => userAnswers.claimAnyOtherExpense match {
            case Some(true) => routes.EmployerPaidBackWfhExpensesController.onPageLoad()
            case _ => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
          }
          case _ => routes.SessionExpiredController.onPageLoad
        }
      case _ =>
        userAnswers.registeredForSelfAssessment match {
          case Some(sa) => routes.WhichYearsAreYouClaimingForController.onPageLoad()
          case _ => routes.SessionExpiredController.onPageLoad
        }
    }

  private def claimingOverPayAsYouEarnThresholdRouting(userAnswers: UserAnswers) =
    userAnswers.claimingOverPayAsYouEarnThreshold match {
      case Some(true) => routes.RegisterForSelfAssessmentController.onPageLoad()
      case Some(false) => routes.EmployerPaidBackExpensesController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad
    }

  private def moreThanFiveJobsRouting(userAnswers: UserAnswers) = userAnswers.moreThanFiveJobs match {
    case Some(true) => routes.UsePrintAndPostController.onPageLoad()
    case Some(false) => routes.ClaimOnlineController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def employerPaidBackExpensesRouting(userAnswers: UserAnswers) = userAnswers.employerPaidBackExpenses match {
    case Some(true) => routes.CannotClaimReliefController.onPageLoad()
    case Some(false) => (userAnswers.claimingFor, userAnswers.claimant) match {
      case (Some(List(ClaimingFor.MileageFuel)), Some(_)) => routes.UseOwnCarController.onPageLoad()
      case (Some(List(ClaimingFor.BuyingEquipment)), Some(_)) => routes.CannotClaimBuyingEquipmentController.onPageLoad()
      case (Some(_), Some(You)) => routes.MoreThanFiveJobsController.onPageLoad()
      case (Some(_), Some(SomeoneElse)) => routes.UsePrintAndPostController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad
    }
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def employerPaidBackWFHExpensesRouting(userAnswers: UserAnswers) = userAnswers.employerPaidBackWFHExpenses match {
    case Some(NoExpenses) => routes.WfhDueToCovidController.onPageLoad()
    case Some(SomeExpenses) => routes.MoreThanFiveJobsController.onPageLoad()
    case Some(AllExpenses) => routes.CannotClaimWFHReliefController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def paidTaxInRelevantYearRouting(userAnswers: UserAnswers) = userAnswers.paidTaxInRelevantYear match {
    case Some(true) => routes.WillPayTaxController.onPageLoad()
    case Some(false) => routes.CannotClaimReliefTooLongAgoController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def claimingForRouting(userAnswers: UserAnswers) =
    userAnswers.claimingFor match {
      case Some(List(ClaimingFor.HomeWorking)) => routes.ClaimAnyOtherExpenseController.onPageLoad()
      case Some(_)  => routes.ClaimantController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad
    }

  private def useOwnCarRouting(userAnswers: UserAnswers) = userAnswers.useOwnCar match {
    case Some(true) => routes.ClaimingMileageController.onPageLoad()
    case Some(false) => routes.UseCompanyCarController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def useCompanyCarRouting(userAnswers: UserAnswers) = userAnswers.useCompanyCar match {
    case Some(true) =>
      routes.ClaimingFuelController.onPageLoad()

    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage, userAnswers.claimant) match {
        case (Some(true), Some(true), Some(You)) => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(true), Some(true), Some(SomeoneElse)) => routes.UsePrintAndPostController.onPageLoad()
        case (Some(true), Some(false), _) => routes.CannotClaimMileageCostsController.onPageLoad()
        case (Some(false), _, _) => routes.CannotClaimMileageCostsController.onPageLoad()
        case _ => routes.SessionExpiredController.onPageLoad
      }

    case _
    => routes.SessionExpiredController.onPageLoad
  }

  private def claimingFuelRouting(userAnswers: UserAnswers) = userAnswers.claimingFuel match {
    case Some(true) =>
      userAnswers.claimant match {
        case Some(You) => routes.MoreThanFiveJobsController.onPageLoad()
        case Some(SomeoneElse) => routes.UsePrintAndPostController.onPageLoad()
        case _ => routes.SessionExpiredController.onPageLoad
      }

    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage, userAnswers.claimant) match {
        case (Some(false), _, _) => routes.CannotClaimMileageFuelCostsController.onPageLoad()
        case (Some(true), Some(false), _) => routes.CannotClaimMileageFuelCostsController.onPageLoad()
        case (Some(true), Some(true), Some(You)) => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(true), Some(true), Some(SomeoneElse)) => routes.UsePrintAndPostController.onPageLoad()
        case _ => routes.SessionExpiredController.onPageLoad
      }

    case _ =>
      routes.SessionExpiredController.onPageLoad
  }

  private def willPayTaxRouting(userAnswers: UserAnswers) = userAnswers.willPayTax match {
    case Some(true) => routes.RegisteredForSelfAssessmentController.onPageLoad()
  case Some(false) => routes.WillNotPayTaxController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def whichYearsAreYouClaimingForRouting(userAnswers: UserAnswers) = userAnswers.whichYearsAreYouClaimingFor match {
    case Some(1) =>
      if(userAnswers.registeredForSelfAssessment.getOrElse(false)) {
        routes.SaCheckDisclaimerCurrentYearController.onPageLoad()
      } else {
        routes.InformCustomerClaimNowInWeeksController.onPageLoad()
      }
    case Some(2) =>
      if(userAnswers.registeredForSelfAssessment.getOrElse(false)) {
        routes.UseSelfAssessmentController.onPageLoad()
      } else {
        routes.EmployerPaidBackWfhExpensesController.onPageLoad()
      }
    case Some(3) =>
      if(userAnswers.registeredForSelfAssessment.getOrElse(false)) {
        routes.SaCheckDisclaimerAllYearsController.onPageLoad()
      } else {
        routes.InformCustomerClaimNowInWeeksController.onPageLoad()
      }
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def changeOtherExpensesRouting(userAnswers: UserAnswers) = userAnswers.claimant match {
    case Some(You) => routes.MoreThanFiveJobsController.onPageLoad()
    case Some(SomeoneElse) => routes.UsePrintAndPostController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def changeUniformsWorkClothingToolsRouting(userAnswers: UserAnswers) = userAnswers.claimant match {
    case Some(You) => routes.MoreThanFiveJobsController.onPageLoad()
    case Some(SomeoneElse) => routes.UsePrintAndPostController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def claimAnyOtherExpenseRouting(userAnswers: UserAnswers) = userAnswers.claimAnyOtherExpense match {
    case Some(true) => routes.DisclaimerController.onPageLoad()
    case Some(false) => routes.ClaimingForController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def covidHomeWorkingRouting(userAnswers: UserAnswers) = userAnswers.covidHomeWorking match {
    case Some(true) => routes.ClaimOnlineController.onPageLoad()
    case Some(false) => routes.MoreThanFiveJobsController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ClaimingForId -> claimingForRouting,
    ClaimantId -> (_ => routes.PaidTaxInRelevantYearController.onPageLoad()),
    ClaimAnyOtherExpenseId -> claimAnyOtherExpenseRouting,
    CovidHomeWorkingId -> covidHomeWorkingRouting,
    PaidTaxInRelevantYearId -> paidTaxInRelevantYearRouting,
    NotEntitledSomeYearsId -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisteredForSelfAssessmentId -> registeredForSelfAssessmentControllerRouting,
    ClaimingOverPayAsYouEarnThresholdId -> claimingOverPayAsYouEarnThresholdRouting,
    MoreThanFiveJobsId -> moreThanFiveJobsRouting,
    EmployerPaidBackExpensesId -> employerPaidBackExpensesRouting,
    EmployerPaidBackWfhExpensesId -> employerPaidBackWFHExpensesRouting,
    ClaimingMileageId -> (_ => routes.UseCompanyCarController.onPageLoad()),
    UseOwnCarId -> useOwnCarRouting,
    UseCompanyCarId -> useCompanyCarRouting,
    ClaimingFuelId -> claimingFuelRouting,
    WillPayTaxId -> willPayTaxRouting,
    WhichYearsAreYouClaimingForId -> whichYearsAreYouClaimingForRouting,
    WillNotPayTaxId -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisterForSelfAssessmentId -> (_ => routes.EmployerPaidBackExpensesController.onPageLoad()),
    ChangeOtherExpensesId -> changeOtherExpensesRouting,
    ChangeUniformsWorkClothingToolsId -> changeUniformsWorkClothingToolsRouting,
    ClaimingForCurrentYearId -> claimingForCurrentYearControllerRouting,
    SaCheckDisclaimerAllYearsId -> saCheckDisclaimerAllYearsRouting
  )

  def nextPage(id: Identifier): UserAnswers => Call =
    routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad)

  lazy val firstPage: Call = routes.ClaimingForController.onPageLoad()

  lazy val changeOtherExpensesPage: Call = routes.ChangeOtherExpensesController.onPageLoad()

  lazy val changeUniformsWorkClothingToolsPage: Call = routes.ChangeUniformsWorkClothingToolsController.onPageLoad()
}
