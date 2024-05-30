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
import models.ClaimingFor.{FeesSubscriptions, HomeWorking, UniformsClothingTools}
import models.{Claimant, ClaimingFor, EmployerPaid}
import models.EmployerPaid.{AllExpenses, NoExpenses, SomeExpenses}

@Singleton
class Navigator @Inject()() {

  private def claimingForCurrentYearControllerRouting(userAnswers: UserAnswers) =
    userAnswers.claimingForCurrentYear match {
      case Some(true)  => routes.SaCheckDisclaimerCurrentYearController.onPageLoad()
      case Some(false) => routes.UseSelfAssessmentController.onPageLoad()
      case _           => routes.SessionExpiredController.onPageLoad
    }

  private def saCheckDisclaimerAllYearsRouting(userAnswers: UserAnswers) =
    userAnswers.claimingForCurrentYear match {
      case Some(true)  => routes.SaCheckDisclaimerAllYearsController.onPageLoad()
      case Some(false) => routes.UseSelfAssessmentController.onPageLoad()
      case _           => routes.SessionExpiredController.onPageLoad
    }

  private def registeredForSelfAssessmentRouting(userAnswers: UserAnswers) = (userAnswers.claimAnyOtherExpense, userAnswers.registeredForSelfAssessment, isMergedJourney(userAnswers)) match {
    case (_, Some(false), true)                                 => routes.WhichYearsAreYouClaimingForController.onPageLoad()
    case (_, Some(true), _)                                     => routes.UseSelfAssessmentController.onPageLoad()
    case (None, Some(false), _) | (Some(false), Some(false), _) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
    case (Some(true), Some(false), _)                           => routes.WhichYearsAreYouClaimingForController.onPageLoad()
    case _                                                      => routes.SessionExpiredController.onPageLoad
  }

  private def claimingOverPayAsYouEarnThresholdRouting(userAnswers: UserAnswers) =
    userAnswers.claimingOverPayAsYouEarnThreshold match {
      case Some(true)  => routes.RegisterForSelfAssessmentController.onPageLoad()
      case Some(false) => routes.EmployerPaidBackAnyExpensesController.onPageLoad()
      case _           => routes.SessionExpiredController.onPageLoad
    }

  private def moreThanFiveJobsRouting(userAnswers: UserAnswers) = {
    val claimingVehiclesRoute = userAnswers.claimingFor.contains(List(ClaimingFor.MileageFuel))
    val vehiclesRedirect = userAnswers.claimingMileage.contains(true) && userAnswers.claimingFuel.contains(false) && userAnswers.employerPaidBackAnyExpenses.contains(EmployerPaid.SomeExpenses)

    userAnswers.moreThanFiveJobs match {
      case Some(true)                           => routes.UsePrintAndPostController.onPageLoad()
      case Some(false) if claimingVehiclesRoute => if(vehiclesRedirect) routes.ClaimOnlineController.onPageLoad() else routes.UsePrintAndPostController.onPageLoad()
      case Some(false)                          => routes.ClaimOnlineController.onPageLoad()
      case _                                    => routes.SessionExpiredController.onPageLoad
    }
  }

  private def employerPaidBackExpensesRouting(userAnswers: UserAnswers) = {
    userAnswers.claimingFor match {
      case Some(claimingForList) => if(claimingForList.contains(ClaimingFor.HomeWorking)) {
        employerPaidBackWFHExpensesRouting(userAnswers)
      } else {
        employerPaidBackOtherExpensesRouting(userAnswers)
      }
      case _ if userAnswers.claimAnyOtherExpense.contains(true) =>
        employerPaidBackWFHExpensesRouting(userAnswers)
      case _ => routes.SessionExpiredController.onPageLoad
    }
  }

  private def employerPaidBackOtherExpensesRouting(userAnswers: UserAnswers) = userAnswers.employerPaidBackAnyExpenses match {
    case Some(AllExpenses)  => routes.CannotClaimReliefController.onPageLoad()
    case Some(SomeExpenses | NoExpenses) => userAnswers.claimingFor match {
      case Some(List(ClaimingFor.MileageFuel))     => routes.UseOwnCarController.onPageLoad()
      case Some(List(ClaimingFor.BuyingEquipment)) => routes.MoreThanFiveJobsController.onPageLoad()
      case Some(_)                                 => routes.MoreThanFiveJobsController.onPageLoad()
      case _                                       => routes.SessionExpiredController.onPageLoad
    }
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def employerPaidBackWFHExpensesRouting(userAnswers: UserAnswers) = userAnswers.employerPaidBackAnyExpenses match {
    case Some(NoExpenses)   => routes.ClaimOnlineController.onPageLoad()
    case Some(SomeExpenses) => routes.MoreThanFiveJobsController.onPageLoad()
    case Some(AllExpenses)  => routes.CannotClaimWFHReliefController.onPageLoad()
    case _                  => routes.SessionExpiredController.onPageLoad
  }

  private def paidTaxInRelevantYearRouting(userAnswers: UserAnswers) = userAnswers.paidTaxInRelevantYear match {
    case Some(true)  => routes.WillPayTaxController.onPageLoad()
    case Some(false) => routes.CannotClaimReliefTooLongAgoController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def claimingForRouting(userAnswers: UserAnswers) = userAnswers.claimingFor match {
    case Some(List(ClaimingFor.HomeWorking)) => routes.ClaimAnyOtherExpenseController.onPageLoad()
    case Some(_)                             => routes.ClaimantController.onPageLoad()
    case _                                   => routes.SessionExpiredController.onPageLoad
  }

  private def claimantRouting(userAnswers: UserAnswers) = (userAnswers.claimant, userAnswers.claimingFor, isMergedJourney(userAnswers)) match {
    case (Some(Claimant.You), Some(List(ClaimingFor.HomeWorking)), _) | (Some(Claimant.You), None, _) => routes.DisclaimerController.onPageLoad()
    case (Some(Claimant.You), _, true)                                                                => routes.DisclaimerController.onPageLoad()
    case (Some(Claimant.You), Some(_), _)                                                             => routes.PaidTaxInRelevantYearController.onPageLoad()
    case (Some(Claimant.SomeoneElse), _, _)                                                           => routes.UsePrintAndPostController.printAndPostGuidance()
    case _                                                                                            => routes.SessionExpiredController.onPageLoad
  }

  private def useOwnCarRouting(userAnswers: UserAnswers) = userAnswers.useOwnCar match {
    case Some(true)  => routes.ClaimingMileageController.onPageLoad()
    case Some(false) => routes.UseCompanyCarController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def useCompanyCarRouting(userAnswers: UserAnswers) = userAnswers.useCompanyCar match {
    case Some(true)  => routes.ClaimingFuelController.onPageLoad()
    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage) match {
        case (Some(true), Some(true))  => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(true), Some(false)) => routes.CannotClaimMileageCostsController.onPageLoad()
        case (Some(false), _)          => routes.CannotClaimMileageCostsController.onPageLoad()
        case _                         => routes.SessionExpiredController.onPageLoad
      }
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def claimingFuelRouting(userAnswers: UserAnswers) = userAnswers.claimingFuel match {
    case Some(true)  => routes.MoreThanFiveJobsController.onPageLoad()
    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage) match {
        case (Some(false), _)          => routes.CannotClaimMileageFuelCostsController.onPageLoad()
        case (Some(true), Some(false)) => routes.CannotClaimMileageFuelCostsController.onPageLoad()
        case (Some(true), Some(true))  => routes.MoreThanFiveJobsController.onPageLoad()
        case _                         => routes.SessionExpiredController.onPageLoad
      }
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def willPayTaxRouting(userAnswers: UserAnswers) = userAnswers.willPayTax match {
    case Some(true)  => routes.RegisteredForSelfAssessmentController.onPageLoad()
    case Some(false) => routes.WillNotPayTaxController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def whichYearsAreYouClaimingForRouting(userAnswers: UserAnswers) = userAnswers.whichYearsAreYouClaimingFor match {
    case Some(true) => routes.InformCustomerClaimNowInWeeksController.onPageLoad()
    case Some(false) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def informCustomerClaimNowInWeeksRouting(userAnswers: UserAnswers) = userAnswers.whichYearsAreYouClaimingFor match {
    case Some(true) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
    case Some(false) => routes.EmployerPaidBackAnyExpensesController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def claimAnyOtherExpenseRouting(userAnswers: UserAnswers) = userAnswers.claimAnyOtherExpense match {
    case Some(true)  => routes.ClaimantController.onPageLoad()
    case Some(false) => routes.ClaimingForController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ClaimingForId -> claimingForRouting,
    ClaimantId -> claimantRouting,
    ClaimAnyOtherExpenseId -> claimAnyOtherExpenseRouting,
    PaidTaxInRelevantYearId -> paidTaxInRelevantYearRouting,
    NotEntitledSomeYearsId -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisteredForSelfAssessmentId -> registeredForSelfAssessmentRouting,
    ClaimingOverPayAsYouEarnThresholdId -> claimingOverPayAsYouEarnThresholdRouting,
    MoreThanFiveJobsId -> moreThanFiveJobsRouting,
    EmployerPaidBackAnyExpensesId -> employerPaidBackExpensesRouting,
    ClaimingMileageId -> (_ => routes.UseCompanyCarController.onPageLoad()),
    UseOwnCarId -> useOwnCarRouting,
    UseCompanyCarId -> useCompanyCarRouting,
    ClaimingFuelId -> claimingFuelRouting,
    WillPayTaxId -> willPayTaxRouting,
    WhichYearsAreYouClaimingForId -> whichYearsAreYouClaimingForRouting,
    InformCustomerClaimNowInWeeksId -> informCustomerClaimNowInWeeksRouting,
    WillNotPayTaxId -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisterForSelfAssessmentId -> (_ => routes.EmployerPaidBackAnyExpensesController.onPageLoad()),
    ChangeOtherExpensesId -> (_ => routes.ClaimantController.onPageLoad()),
    ChangeUniformsWorkClothingToolsId -> (_ => routes.ClaimantController.onPageLoad()),
    ClaimingForCurrentYearId -> claimingForCurrentYearControllerRouting,
    SaCheckDisclaimerAllYearsId -> saCheckDisclaimerAllYearsRouting
  )

  def isMergedJourney(userAnswers: UserAnswers): Boolean = {
    val claimingFor = userAnswers.claimingFor.getOrElse(List())
    claimingFor.filterNot(claim => claim.equals(HomeWorking) || claim.equals(UniformsClothingTools) || claim.equals(FeesSubscriptions)).size == 0 &&
      claimingFor.filter(claim => claim.equals(HomeWorking) || claim.equals(UniformsClothingTools) || claim.equals(FeesSubscriptions)).size > 1 &&
      claimingFor.contains(HomeWorking)
  }

  def nextPage(id: Identifier): UserAnswers => Call =
    routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad)

  lazy val firstPage: Call = routes.ClaimingForController.onPageLoad()

  lazy val changeOtherExpensesPage: Call = routes.ChangeOtherExpensesController.onPageLoad()

  lazy val changeUniformsWorkClothingToolsPage: Call = routes.ChangeUniformsWorkClothingToolsController.onPageLoad()
}
