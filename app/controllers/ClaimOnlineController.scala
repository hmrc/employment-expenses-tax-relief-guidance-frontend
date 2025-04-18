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

package controllers

import config.FrontendAppConfig
import controllers.actions._

import javax.inject.Inject
import models.ClaimingFor._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.OnwardJourney
import views.html.ClaimOnlineView

class ClaimOnlineController @Inject() (
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    view: ClaimOnlineView,
    appConfig: FrontendAppConfig
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    val claimingFor = request.userAnswers.claimingFor.getOrElse(List())

    val isMergedJourney = claimingFor
      .filterNot(claim =>
        claim.equals(HomeWorking) || claim.equals(UniformsClothingTools) || claim.equals(FeesSubscriptions)
      )
      .size == 0 &&
      claimingFor
        .filter(claim =>
          claim.equals(HomeWorking) || claim.equals(UniformsClothingTools) || claim.equals(FeesSubscriptions)
        )
        .size > 1

    request.userAnswers.claimingFor match {
      case _ if isMergedJourney && appConfig.mergedJourneyEnabled =>
        Ok(
          view(
            OnwardJourney.MergedJourney(
              claimingFor.contains(HomeWorking),
              claimingFor.contains(FeesSubscriptions),
              claimingFor.contains(UniformsClothingTools)
            ),
            claimingFor
          )
        )
      case Some(List(UniformsClothingTools)) => Ok(view(OnwardJourney.FixedRateExpenses, claimingFor))
      case Some(List(FeesSubscriptions))     => Ok(view(OnwardJourney.ProfessionalSubscriptions, claimingFor))
      case Some(List(HomeWorking))           => Ok(view(OnwardJourney.WorkingFromHomeExpensesOnly, claimingFor))
      case _ if request.userAnswers.claimAnyOtherExpense.contains(true) =>
        Ok(view(OnwardJourney.WorkingFromHomeExpensesOnly, List(HomeWorking)))
      case _ => Ok(view(OnwardJourney.IForm, claimingFor))
    }
  }

}
