/*
 * Copyright 2018 HM Revenue & Customs
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
import models.{ClaimingFor, HowManyYearsWasTaxPaid}
import models.ClaimYears.AnotherYear

@Singleton
class Navigator @Inject()() {

  private def registeredForSelfAssessmentControllerRouting(userAnswers: UserAnswers) = userAnswers.registeredForSelfAssessment match {
    case Some(true)  => routes.UseSelfAssessmentController.onPageLoad()
    case Some(false) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def claimingOverPayAsYouEarnThresholdRouting(userAnswers: UserAnswers) =
    userAnswers.claimingOverPayAsYouEarnThreshold match {
      case Some(true)  => routes.RegisterForSelfAssessmentController.onPageLoad()
      case Some(false) => routes.EmployerPaidBackExpensesController.onPageLoad()
      case None        => routes.SessionExpiredController.onPageLoad()
    }

  private def moreThanFiveJobsRouting(userAnswers: UserAnswers) = userAnswers.moreThanFiveJobs match {
    case Some(true)  => routes.UsePrintAndPostController.onPageLoad()
    case Some(false) => routes.ClaimOnlineController.onPageLoad()
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def employerPaidBackExpensesRouting(userAnswers: UserAnswers) = userAnswers.employerPaidBackExpenses match {
    case Some(true)  => routes.CannotClaimReliefController.onPageLoad()
    case Some(false) => routes.ClaimingForController.onPageLoad()
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def taxYearsRouting(userAnswers: UserAnswers) = userAnswers.taxYears match {
    case Some(List(AnotherYear))                    => routes.CannotClaimReliefTooLongAgoController.onPageLoad()
    case Some(years) if years.size == 1             => routes.PaidTaxInRelevantYearController.onPageLoad()
    case Some(years) if years.contains(AnotherYear) => routes.CannotClaimReliefSomeYearsController.onPageLoad()
    case Some(_)                                    => routes.HowManyYearsWasTaxPaidController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def paidTaxInRelevantYearRouting(userAnswers: UserAnswers) = userAnswers.paidTaxInRelevantYear match {
    case Some(true)  => routes.RegisteredForSelfAssessmentController.onPageLoad()
    case Some(false) => routes.NotEntitledController.onPageLoad()
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def howManyYearsWasTaxPaidRouting(userAnswers: UserAnswers) = userAnswers.howManyYearsWasTaxPaid match {
    case Some(HowManyYearsWasTaxPaid.All)  => routes.RegisteredForSelfAssessmentController.onPageLoad()
    case Some(HowManyYearsWasTaxPaid.Some) => routes.NotEntitledSomeYearsController.onPageLoad()
    case Some(HowManyYearsWasTaxPaid.None) => routes.NotEntitledController.onPageLoad()
    case None                              => routes.SessionExpiredController.onPageLoad()
  }

  private def claimingForRouting(userAnswers: UserAnswers) =
    (userAnswers.claimingFor, userAnswers.claimant) match {
      case (Some(List(ClaimingFor.MileageFuel)), Some(_))     => routes.UseOwnCarController.onPageLoad()
      case (Some(List(ClaimingFor.BuyingEquipment)), Some(_)) => routes.CannotClaimBuyingEquipmentController.onPageLoad()
      case (Some(_), Some(You))                               => routes.MoreThanFiveJobsController.onPageLoad()
      case (Some(_), Some(SomeoneElse))                       => routes.UsePrintAndPostController.onPageLoad()
      case _                                                  => routes.SessionExpiredController.onPageLoad()
    }

  private def useOwnCarRouting(userAnswers: UserAnswers) = userAnswers.useOwnCar match {
    case Some(true)  => routes.ClaimingMileageController.onPageLoad()
    case Some(false) => routes.UseCompanyCarController.onPageLoad()
    case None        => routes.SessionExpiredController.onPageLoad()
  }

  private def useCompanyCarRouting(userAnswers: UserAnswers) = userAnswers.useCompanyCar match {
    case Some(true)  =>
      routes.ClaimingFuelController.onPageLoad()

    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage, userAnswers.claimant) match {
        case (Some(true), Some(true), Some(You))          => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(true), Some(true), Some(SomeoneElse))  => routes.UsePrintAndPostController.onPageLoad()
        case (Some(true), Some(false), _)                 => routes.CannotClaimMileageCostsController.onPageLoad()
        case (Some(false), _, _)                          => routes.CannotClaimMileageCostsController.onPageLoad()
        case _                                            => routes.SessionExpiredController.onPageLoad()
      }

    case None
      => routes.SessionExpiredController.onPageLoad()
  }

  private def claimingFuelRouting(userAnswers: UserAnswers) = userAnswers.claimingFuel match {
    case Some(true) =>
      userAnswers.claimant match {
        case Some(You)         => routes.MoreThanFiveJobsController.onPageLoad()
        case Some(SomeoneElse) => routes.UsePrintAndPostController.onPageLoad()
        case None              => routes.SessionExpiredController.onPageLoad()
      }

    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage, userAnswers.claimant) match {
        case (Some(false), _, _)                         => routes.CannotClaimMileageFuelCostsController.onPageLoad()
        case (Some(true), Some(false), _)                => routes.CannotClaimMileageFuelCostsController.onPageLoad()
        case (Some(true), Some(true), Some(You))         => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(true), Some(true), Some(SomeoneElse)) => routes.UsePrintAndPostController.onPageLoad()
        case _                                           => routes.SessionExpiredController.onPageLoad()
      }

    case None =>
      routes.SessionExpiredController.onPageLoad()
  }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ClaimantId                          -> (_ => routes.TaxYearsController.onPageLoad()),
    TaxYearsId                          -> taxYearsRouting,
    PaidTaxInRelevantYearId             -> paidTaxInRelevantYearRouting,
    CannotClaimReliefSomeYearsId        -> (_ => routes.HowManyYearsWasTaxPaidController.onPageLoad()),
    HowManyYearsWasTaxPaidId            -> howManyYearsWasTaxPaidRouting,
    NotEntitledSomeYearsId              -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisteredForSelfAssessmentId       -> registeredForSelfAssessmentControllerRouting,
    ClaimingOverPayAsYouEarnThresholdId -> claimingOverPayAsYouEarnThresholdRouting,
    MoreThanFiveJobsId                  -> moreThanFiveJobsRouting,
    EmployerPaidBackExpensesId          -> employerPaidBackExpensesRouting,
    ClaimingForId                       -> claimingForRouting,
    ClaimingMileageId                   -> (_ => routes.UseCompanyCarController.onPageLoad()),
    UseOwnCarId                         -> useOwnCarRouting,
    UseCompanyCarId                     -> useCompanyCarRouting,
    ClaimingFuelId                      -> claimingFuelRouting
  )

  def nextPage(id: Identifier): UserAnswers => Call =
    routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad())

  lazy val firstPage: Call = routes.ClaimantController.onPageLoad()
}
