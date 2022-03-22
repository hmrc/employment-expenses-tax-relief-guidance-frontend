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
import forms.PaidTaxInRelevantYearFormProvider
import identifiers.PaidTaxInRelevantYearId
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Navigator, UserAnswers}
import views.html.PaidTaxInRelevantYearView

import scala.concurrent.{ExecutionContext, Future}

class PaidTaxInRelevantYearController @Inject()(
                                                appConfig: FrontendAppConfig,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                getClaimant: GetClaimantAction,
                                                formProvider: PaidTaxInRelevantYearFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: PaidTaxInRelevantYearView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      val form: Form[Boolean] = formProvider(request.claimant, appConfig.earliestTaxYear)

      val preparedForm = request.userAnswers.paidTaxInRelevantYear match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Future.successful(Ok(view(preparedForm, request.claimant)))
  }

  def onSubmit: Action[AnyContent] = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      val form: Form[Boolean] = formProvider(request.claimant, appConfig.earliestTaxYear)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, request.claimant))),
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, PaidTaxInRelevantYearId, value).map(cacheMap =>
            Redirect(navigator.nextPage(PaidTaxInRelevantYearId)(new UserAnswers(cacheMap))))
      )
  }
}
