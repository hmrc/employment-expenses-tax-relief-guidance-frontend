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
import models.ClaimingFor
import models.ClaimingFor.{HomeWorking, MileageFuel, values}

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.UserAnswers
import views.html.{UseIformFreOnlyView, UsePrintAndPostDetailedView, UsePrintAndPostFreOnlyView, UsePrintAndPostView}

class UsePrintAndPostController @Inject()(
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: UsePrintAndPostView,
                                           appConfig: FrontendAppConfig,
                                           detailedView: UsePrintAndPostDetailedView,
                                           freOnlyPrintAndPostView: UsePrintAndPostFreOnlyView,
                                           freOnlyIformView: UseIformFreOnlyView
                                         ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      if (appConfig.freOnlyJourneyEnabled || appConfig.onlineJourneyShutterEnabled) {
        val sortedList = buildClaimingForList(request.userAnswers)
        if (appConfig.freOnlyJourneyEnabled) {
          if (request.userAnswers.moreThanFiveJobs.isDefined) {
            request.userAnswers.moreThanFiveJobs match {
              case Some(true) => Ok(freOnlyPrintAndPostView(sortedList))
              case Some(false) => Ok(freOnlyIformView(sortedList))
            }
          } else {
            Ok(freOnlyIformView(sortedList))
          }
        } else {
          Ok(detailedView(sortedList))
        }
      }
      else {
        val fuelCosts = request.userAnswers.claimingFuel.getOrElse(false)
        val mileageCosts = request.userAnswers.claimingMileage.getOrElse(false)
        Ok(view(fuelCosts, mileageCosts))
      }
  }

  private def buildClaimingForList(userAnswers: UserAnswers): List[ClaimingFor] = {
    val containsMileageFuel = userAnswers.claimingFor.exists(_.contains(MileageFuel))
    val isClaimingMileage = userAnswers.claimingMileage.getOrElse(false)
    val isClaimingFuel = userAnswers.claimingFuel.getOrElse(false)
    val claimingForList = userAnswers.claimingFor.getOrElse(Nil)
    val onlyWfhClaim = userAnswers.claimAnyOtherExpense.getOrElse(false)
    if(onlyWfhClaim) {
      return List(HomeWorking)
    }
    val filterList =
      if (containsMileageFuel && (isClaimingMileage || isClaimingFuel)) {
        claimingForList
      } else {
        claimingForList.filterNot(_ == MileageFuel)
      }
    values.flatMap(value => filterList.find(_ == value))
  }

  def printAndPostGuidance: Action[AnyContent] = (getData andThen requireData) {
    Redirect(appConfig.employeeExpensesClaimByPostUrl)
  }
}
