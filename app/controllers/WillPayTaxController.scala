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

package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.WillPayTaxFormProvider
import identifiers.WillPayTaxId
import models.ClaimYears
import models.ClaimYears.ThisYear
import models.requests.ClaimantRequest
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.time.TaxYear
import utils.{Navigator, UserAnswers}
import views.html.willPayTax

import scala.concurrent.Future

class WillPayTaxController @Inject()(appConfig: FrontendAppConfig,
                                     override val messagesApi: MessagesApi,
                                     dataCacheConnector: DataCacheConnector,
                                     navigator: Navigator,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     getClaimant: GetClaimantAction,
                                     formProvider: WillPayTaxFormProvider) extends FrontendController with I18nSupport {

  def onPageLoad() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getDatesForYear {
        (taxYear) =>

          val startYear = taxYear.startYear.toString
          val finishYear = taxYear.finishYear.toString
          val form: Form[Boolean] = formProvider(request.claimant, startYear, finishYear)

          val preparedForm = request.userAnswers.willPayTax match {
            case None => form
            case Some(value) => form.fill(value)
          }
          Future.successful(Ok(willPayTax(appConfig, preparedForm, request.claimant, startYear, finishYear)))
      }
  }

  def onSubmit() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getDatesForYear {
        (taxYear) =>

          val startYear = taxYear.startYear.toString
          val finishYear = taxYear.finishYear.toString
          val form: Form[Boolean] = formProvider(request.claimant, startYear, finishYear)

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(willPayTax(appConfig, formWithErrors, request.claimant, startYear, finishYear))),
            (value) =>
              dataCacheConnector.save[Boolean](request.sessionId, WillPayTaxId, value).map(cacheMap =>
                Redirect(navigator.nextPage(WillPayTaxId)(new UserAnswers(cacheMap))))
          )
      }
  }

  private def getDatesForYear(block: TaxYear => Future[Result])
                             (implicit request: ClaimantRequest[AnyContent]): Future[Result] = {

    request.userAnswers.taxYears match {

      case Some(list) if list.contains(ThisYear) =>
        block(ClaimYears.getTaxYear(ThisYear))
      case _ =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}
