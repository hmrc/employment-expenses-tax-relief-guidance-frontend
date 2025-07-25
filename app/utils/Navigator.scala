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

import config.FrontendAppConfig
import controllers.routes
import identifiers._
import models.ClaimingFor._
import models.ClaimingForMoreThanOneJob.{MoreThanOneJob, OneJob}
import models.EmployerPaid.{AllExpenses, NoExpenses, SomeExpenses}
import models.{Claimant, ClaimingFor}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject() (navigatorHelper: NavigatorHelper)(implicit appConfig: FrontendAppConfig) {

  private def claimingForRouting(userAnswers: UserAnswers) = userAnswers.claimingFor match {
    case Some(_) if appConfig.onlineJourneyShutterEnabled => routes.ClaimantController.onPageLoad()
    case Some(List(ClaimingFor.HomeWorking))              => routes.ClaimAnyOtherExpenseController.onPageLoad()
    case Some(_)                                          => routes.ClaimantController.onPageLoad()
    case _                                                => routes.SessionExpiredController.onPageLoad
  }

  private def claimantRouting(userAnswers: UserAnswers) = (userAnswers.claimant, userAnswers.claimingFor) match {
    case (Some(Claimant.You), _) if navigatorHelper.isClaimingWfh(userAnswers) =>
      routes.DisclaimerController.onPageLoad()
    case (Some(Claimant.You), Some(_)) =>
      routes.PaidTaxInRelevantYearController.onPageLoad()
    case (Some(Claimant.SomeoneElse), _) =>
      routes.UsePrintAndPostController.printAndPostGuidance()
    case _ =>
      routes.SessionExpiredController.onPageLoad
  }

  private def claimAnyOtherExpenseRouting(userAnswers: UserAnswers) = userAnswers.claimAnyOtherExpense match {
    case Some(true)  => routes.ClaimantController.onPageLoad()
    case Some(false) => routes.ClaimingForController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def paidTaxInRelevantYearRouting(userAnswers: UserAnswers) = userAnswers.paidTaxInRelevantYear match {
    case Some(true)  => routes.WillPayTaxController.onPageLoad()
    case Some(false) => routes.CannotClaimReliefTooLongAgoController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def registeredForSelfAssessmentRouting(userAnswers: UserAnswers) =
    (userAnswers.claimingFor, userAnswers.registeredForSelfAssessment) match {
      case (_, Some(true)) => routes.UseSelfAssessmentController.onPageLoad()
      case (_, Some(false)) if navigatorHelper.isClaimingWfh(userAnswers) =>
        routes.WhichYearsAreYouClaimingForController.onPageLoad()
      case (Some(_), Some(false)) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
      case _                      => routes.SessionExpiredController.onPageLoad
    }

  private def claimingOverPayAsYouEarnThresholdRouting(userAnswers: UserAnswers) =
    userAnswers.claimingOverPayAsYouEarnThreshold match {
      case Some(true)  => routes.RegisterForSelfAssessmentController.onPageLoad()
      case Some(false) => routes.EmployerPaidBackAnyExpensesController.onPageLoad()
      case _           => routes.SessionExpiredController.onPageLoad
    }

  private def moreThanFiveJobsRouting(userAnswers: UserAnswers) = {
    def uniformsClothingTools = userAnswers.claimingFor.contains(List(ClaimingFor.UniformsClothingTools))
    def claimingVehiclesRoute = userAnswers.claimingFor.contains(List(ClaimingFor.MileageFuel))
    val vehiclesRedirect      = navigatorHelper.vehiclesRedirect(userAnswers)

    userAnswers.moreThanFiveJobs match {
      case Some(true) => routes.UsePrintAndPostController.onPageLoad()

      case Some(false) if uniformsClothingTools && appConfig.pegaServiceJourney =>
        routes.ClaimingForMoreThanOneJobController.onPageLoad()

      case Some(false) if uniformsClothingTools && appConfig.freOnlyJourneyEnabled =>
        routes.ClaimOnlineController.onPageLoad()

      case Some(false) if appConfig.freOnlyJourneyEnabled || appConfig.onlineJourneyShutterEnabled =>
        routes.UsePrintAndPostController.onPageLoad()

      case Some(false) if claimingVehiclesRoute =>
        if (vehiclesRedirect) routes.ClaimOnlineController.onPageLoad()
        else routes.UsePrintAndPostController.onPageLoad()

      case Some(false) => routes.ClaimOnlineController.onPageLoad()

      case _ => routes.SessionExpiredController.onPageLoad
    }
  }

  private def claimingForMoreThanOneJobRouting(userAnswers: UserAnswers): Call =
    userAnswers.claimingForMoreThanOneJob match {
      case Some(MoreThanOneJob) => routes.ClaimByAlternativeController.onPageLoad()
      case Some(OneJob)         => routes.ClaimOnlineController.onPageLoad()
      case _                    => routes.SessionExpiredController.onPageLoad
    }

  private def employerPaidBackExpensesRouting(userAnswers: UserAnswers) = {
    def containsMileage: Boolean = userAnswers.claimingFor.exists(_.contains(ClaimingFor.MileageFuel))
    def isClaimingWfh: Boolean   = navigatorHelper.isClaimingWfh(userAnswers)

    userAnswers.employerPaidBackAnyExpenses match {
      case Some(SomeExpenses | NoExpenses) if containsMileage => routes.UseOwnCarController.onPageLoad()
      case Some(SomeExpenses | NoExpenses)                    => routes.MoreThanFiveJobsController.onPageLoad()

      case Some(AllExpenses) if isClaimingWfh => routes.CannotClaimWFHReliefController.onPageLoad()
      case Some(AllExpenses)                  => routes.CannotClaimReliefController.onPageLoad()

      case _ => routes.SessionExpiredController.onPageLoad
    }
  }

  private def useOwnCarRouting(userAnswers: UserAnswers) = userAnswers.useOwnCar match {
    case Some(true)  => routes.ClaimingMileageController.onPageLoad()
    case Some(false) => routes.UseCompanyCarController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def useCompanyCarRouting(userAnswers: UserAnswers) = userAnswers.useCompanyCar match {
    case Some(true) => routes.ClaimingFuelController.onPageLoad()
    case Some(false) =>
      (userAnswers.useOwnCar, userAnswers.claimingMileage, isMergedJourney(userAnswers)) match {
        case (Some(true), Some(true), _) => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(_), _, true)          => routes.MoreThanFiveJobsController.onPageLoad()
        case (Some(_), _, false)         => routes.CannotClaimMileageCostsController.onPageLoad()

        case _ => routes.SessionExpiredController.onPageLoad
      }
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def claimingFuelRouting(userAnswers: UserAnswers) =
    userAnswers.claimingFuel match {
      case Some(true) => routes.MoreThanFiveJobsController.onPageLoad()
      case Some(false) =>
        (userAnswers.useOwnCar, userAnswers.claimingMileage, isMergedJourney(userAnswers)) match {
          case (Some(true), Some(true), _) => routes.MoreThanFiveJobsController.onPageLoad()
          case (Some(_), _, true)          => routes.MoreThanFiveJobsController.onPageLoad()
          case (Some(_), _, false)         => routes.CannotClaimMileageCostsController.onPageLoad()

          case _ => routes.SessionExpiredController.onPageLoad
        }
      case _ => routes.SessionExpiredController.onPageLoad
    }

  private def willPayTaxRouting(userAnswers: UserAnswers) = userAnswers.willPayTax match {
    case Some(true)  => routes.RegisteredForSelfAssessmentController.onPageLoad()
    case Some(false) => routes.WillNotPayTaxController.onPageLoad()
    case _           => routes.SessionExpiredController.onPageLoad
  }

  private def whichYearsAreYouClaimingForRouting(userAnswers: UserAnswers) =
    userAnswers.whichYearsAreYouClaimingFor match {
      case Some(true)  => routes.InformCustomerClaimNowInWeeksController.onPageLoad()
      case Some(false) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
      case _           => routes.SessionExpiredController.onPageLoad
    }

  private def informCustomerClaimNowInWeeksRouting(userAnswers: UserAnswers) =
    userAnswers.whichYearsAreYouClaimingFor match {
      case Some(true)  => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
      case Some(false) => routes.EmployerPaidBackAnyExpensesController.onPageLoad()
      case _           => routes.SessionExpiredController.onPageLoad
    }

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

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ClaimingForId                       -> claimingForRouting,
    ClaimantId                          -> claimantRouting,
    ClaimAnyOtherExpenseId              -> claimAnyOtherExpenseRouting,
    PaidTaxInRelevantYearId             -> paidTaxInRelevantYearRouting,
    NotEntitledSomeYearsId              -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisteredForSelfAssessmentId       -> registeredForSelfAssessmentRouting,
    ClaimingOverPayAsYouEarnThresholdId -> claimingOverPayAsYouEarnThresholdRouting,
    MoreThanFiveJobsId                  -> moreThanFiveJobsRouting,
    ClaimingForMoreThanOneJobId         -> claimingForMoreThanOneJobRouting,
    EmployerPaidBackAnyExpensesId       -> employerPaidBackExpensesRouting,
    ClaimingMileageId                   -> (_ => routes.UseCompanyCarController.onPageLoad()),
    UseOwnCarId                         -> useOwnCarRouting,
    UseCompanyCarId                     -> useCompanyCarRouting,
    ClaimingFuelId                      -> claimingFuelRouting,
    WillPayTaxId                        -> willPayTaxRouting,
    WhichYearsAreYouClaimingForId       -> whichYearsAreYouClaimingForRouting,
    InformCustomerClaimNowInWeeksId     -> informCustomerClaimNowInWeeksRouting,
    WillNotPayTaxId                     -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisterForSelfAssessmentId         -> (_ => routes.EmployerPaidBackAnyExpensesController.onPageLoad()),
    ChangeOtherExpensesId               -> (_ => routes.ClaimantController.onPageLoad()),
    ChangeUniformsWorkClothingToolsId   -> (_ => routes.ClaimantController.onPageLoad()),
    ClaimingForCurrentYearId            -> claimingForCurrentYearControllerRouting,
    SaCheckDisclaimerAllYearsId         -> saCheckDisclaimerAllYearsRouting
  )

  private def isMergedJourney(userAnswers: UserAnswers): Boolean = {
    val claimsForMergedJourney: Set[ClaimingFor] =
      Set(UniformsClothingTools, FeesSubscriptions, HomeWorking, TravelExpenses, BuyingEquipment, Other)

    userAnswers.claimingFor.exists(_.toSet.intersect(claimsForMergedJourney).nonEmpty)
  }

  def nextPage(id: Identifier): UserAnswers => Call =
    routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad)

  lazy val firstPage: Call = routes.ClaimingForController.onPageLoad()

}
