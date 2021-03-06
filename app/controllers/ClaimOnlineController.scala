/*
 * Copyright 2021 HM Revenue & Customs
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
import models.ClaimingFor.{FeesSubscriptions, UniformsClothingTools}
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

      request.userAnswers.covidHomeWorking match {

        case Some(true) => Ok(view(OnwardJourney.WorkingFromHomeExpensesOnly))
        case Some(false) => Ok(view(OnwardJourney.IForm))
        case _ =>

          request.userAnswers.claimingFor match {
            case Some(claiming) =>
              val onwardJourney =
                if (claiming.forall(_ == UniformsClothingTools)) OnwardJourney.FixedRateExpenses
                else if (claiming.forall(_ == FeesSubscriptions)) OnwardJourney.ProfessionalSubscriptions
                else OnwardJourney.IForm

              Ok(view(onwardJourney))

            case _ =>
              request.userAnswers.claimAnyOtherExpense match {
                case Some(_) =>
                  val onwardJourney = OnwardJourney.IForm
                  Ok(view(onwardJourney))
                case _ => Redirect(routes.SessionExpiredController.onPageLoad())
              }
          }
      }
  }
}