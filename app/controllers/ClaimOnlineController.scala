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

import controllers.actions._

import javax.inject.Inject
import models.ClaimingFor._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.OnwardJourney
import views.html.ClaimOnlineView

class ClaimOnlineController @Inject()(
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ClaimOnlineView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      def wfhRouting = if (request.userAnswers.covidHomeWorking.getOrElse(false)) {
        Ok(view(OnwardJourney.WorkingFromHomeExpensesOnly))
      } else {
        Ok(view(OnwardJourney.IForm))
      }
      val claimingFor = request.userAnswers.claimingFor.getOrElse(List())

      val isMergedJourney = claimingFor.filterNot(claim => claim.equals(HomeWorking) || claim.equals(UniformsClothingTools) || claim.equals(FeesSubscriptions)).size == 0 &&
        claimingFor.filter(claim => claim.equals(HomeWorking) || claim.equals(UniformsClothingTools) || claim.equals(FeesSubscriptions)).size > 1

      request.userAnswers.claimingFor match {
        case _ if isMergedJourney => Ok(view(OnwardJourney.MergedJourney(claimingFor.contains(HomeWorking), claimingFor.contains(FeesSubscriptions), claimingFor.contains(UniformsClothingTools))))
        case Some(List(UniformsClothingTools)) => Ok(view(OnwardJourney.FixedRateExpenses))
        case Some(List(FeesSubscriptions)) => Ok(view(OnwardJourney.ProfessionalSubscriptions))
        case Some(List(HomeWorking)) => wfhRouting
        case _ if request.userAnswers.claimAnyOtherExpense.contains(true) => wfhRouting
        case _ => Ok(view(OnwardJourney.IForm))
      }
  }
}