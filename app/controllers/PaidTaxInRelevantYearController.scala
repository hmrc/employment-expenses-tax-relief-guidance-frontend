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
import forms.PaidTaxInRelevantYearFormProvider
import identifiers.PaidTaxInRelevantYearId
import models.TaxYears
import models.TaxYears.AnotherYear
import models.requests.ClaimantRequest
import play.api.mvc.{AnyContent, Result}
import utils.{Navigator, UserAnswers}
import views.html.paidTaxInRelevantYear

import scala.concurrent.Future

class PaidTaxInRelevantYearController @Inject()(appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                getClaimant: GetClaimantAction,
                                                formProvider: PaidTaxInRelevantYearFormProvider) extends FrontendController with I18nSupport {

  def onPageLoad() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getDatesForYear {
        (startOfYear, endOfYear) =>

          val form: Form[Boolean] = formProvider(request.claimant, startOfYear, endOfYear)

          val preparedForm = request.userAnswers.paidTaxInRelevantYear match {
            case None => form
            case Some(value) => form.fill(value)
          }
          Future.successful(Ok(paidTaxInRelevantYear(appConfig, preparedForm, request.claimant, startOfYear, endOfYear)))
      }

  }

  def onSubmit() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getDatesForYear {
        (startOfYear, endOfYear) =>

          val form: Form[Boolean] = formProvider(request.claimant, startOfYear, endOfYear)

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(paidTaxInRelevantYear(appConfig, formWithErrors, request.claimant, startOfYear, endOfYear))),
            (value) =>
              dataCacheConnector.save[Boolean](request.sessionId, PaidTaxInRelevantYearId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(PaidTaxInRelevantYearId)(new UserAnswers(cacheMap))))
          )
      }

  }

  private def getDatesForYear(block: (String, String) => Future[Result])
                             (implicit request: ClaimantRequest[AnyContent]): Future[Result] = {

    request.userAnswers.taxYears match {

      case Some(List(year)) if year != AnotherYear =>
        val startOfYear = TaxYears.startOfYear(year)
        val endOfYear = startOfYear + 1

        block(startOfYear.toString, endOfYear.toString)
      case _ =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}
