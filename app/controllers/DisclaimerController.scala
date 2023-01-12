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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, GetClaimantAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DisclaimerView

import javax.inject.Inject

class DisclaimerController  @Inject()(
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       getClaimant: GetClaimantAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DisclaimerView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData andThen getClaimant) {
    implicit request =>
      val whichYears = request.userAnswers.whichYearsAreYouClaimingFor.getOrElse(3)

      val claimingForCurrent: Boolean = whichYears == 1 || whichYears == 3
      val claimingForPrev: Boolean = whichYears == 2 || whichYears == 3

      Ok(view(request.claimant, claimingForCurrent, claimingForPrev))
  }

}
