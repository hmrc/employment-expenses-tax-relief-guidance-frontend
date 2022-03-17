/*
 * Copyright 2022 HM Revenue & Customs
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
import connectors.DataCacheConnector
import controllers.actions._
import forms.EmployerPaidBackWfhExpensesFormProvider
import identifiers.EmployerPaidBackWfhExpensesId

import javax.inject.Inject
import models.EmployerPaid
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.EmployerPaidBackWfhExpensesView

import scala.concurrent.{ExecutionContext, Future}

class EmployerPaidBackWfhExpensesController @Inject()(
                                                       dataCacheConnector: DataCacheConnector,
                                                       navigator: Navigator,
                                                       getData: DataRetrievalAction,
                                                       requireData: DataRequiredAction,
                                                       getClaimant: GetClaimantAction,
                                                       formProvider: EmployerPaidBackWfhExpensesFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: EmployerPaidBackWfhExpensesView,
                                                       appConfig: FrontendAppConfig
                                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form: Form[EmployerPaid] = formProvider()

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.employerPaidBackWFHExpenses match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val backButtonOverride = if(!request.userAnswers.whichYearsAreYouClaimingFor.isDefined) {
        request.userAnswers.registeredForSelfAssessment match {
          case Some(true) => Some(appConfig.claimingForCurrentYearBackButtonOverride)
          case _ => None
        }
      }else{
        None
      }

      Ok(view(preparedForm, backButtonOverride))
  }

  def onSubmit: Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, None))),
        value => {

          dataCacheConnector.save[EmployerPaid](request.sessionId, EmployerPaidBackWfhExpensesId, value).map(cacheMap =>
            Redirect(navigator.nextPage(EmployerPaidBackWfhExpensesId)(new UserAnswers(cacheMap)))
          )

        }

      )
  }
}
