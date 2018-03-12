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

@Singleton
class Navigator @Inject()() {

  private def registeredForSelfAssessmentControllerRouting(userAnswers: UserAnswers) = userAnswers.registeredForSelfAssessment match {
    case Some(true) => routes.UseSelfAssessmentController.onPageLoad()
    case Some(false) => routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def claimingOverPayAsYouEarnThresholdRouting(userAnswers: UserAnswers) =
    (userAnswers.claimingOverPayAsYouEarnThreshold, userAnswers.claimant) match {
      case (Some(true), _)                  => routes.RegisterForSelfAssessmentController.onPageLoad()
      case (Some(false), Some(You))         => routes.MoreThanFiveJobsController.onPageLoad()
      case (Some(false), Some(SomeoneElse)) => routes.UsePrintAndPostController.onPageLoad()
      case (_, _)                           => routes.SessionExpiredController.onPageLoad()
    }

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ClaimantId                          -> (_ => routes.RegisteredForSelfAssessmentController.onPageLoad()),
    RegisteredForSelfAssessmentId       -> registeredForSelfAssessmentControllerRouting,
    ClaimingOverPayAsYouEarnThresholdId -> claimingOverPayAsYouEarnThresholdRouting
  )

  def nextPage(id: Identifier): UserAnswers => Call =
    routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad())
}
