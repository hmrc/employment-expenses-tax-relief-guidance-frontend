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
import controllers.helpers.ClaimingForListBuilder
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{UseIformFreOnlyView, UsePrintAndPostDetailedView, UsePrintAndPostFreOnlyView, UsePrintAndPostView}

import javax.inject.Inject

class UsePrintAndPostController @Inject() (
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    claimingForListBuilder: ClaimingForListBuilder,
    view: UsePrintAndPostView,
    appConfig: FrontendAppConfig,
    detailedView: UsePrintAndPostDetailedView,
    freOnlyPrintAndPostView: UsePrintAndPostFreOnlyView,
    freOnlyIformView: UseIformFreOnlyView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    (appConfig.freOnlyJourneyEnabled, appConfig.onlineJourneyShutterEnabled) match {

      case (true, _) =>
        val sortedList = claimingForListBuilder.buildClaimingForList(request.userAnswers)
        request.userAnswers.moreThanFiveJobs match {
          case Some(true) => Ok(freOnlyPrintAndPostView(sortedList))
          case _          => Ok(freOnlyIformView(sortedList))
        }

      case (false, true) =>
        val sortedList = claimingForListBuilder.buildClaimingForList(request.userAnswers)
        Ok(detailedView(sortedList))

      case _ =>
        val fuelCosts    = request.userAnswers.claimingFuel.getOrElse(false)
        val mileageCosts = request.userAnswers.claimingMileage.getOrElse(false)
        Ok(view(fuelCosts, mileageCosts))
    }
  }

  def printAndPostGuidance: Action[AnyContent] = getData.andThen(requireData) {
    Redirect(appConfig.employeeExpensesClaimByPostUrl)
  }

}
