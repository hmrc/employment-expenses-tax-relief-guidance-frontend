/*
 * Copyright 2019 HM Revenue & Customs
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
import forms.UseCompanyCarFormProvider
import identifiers.UseCompanyCarId
import models.{NotUsingOwnCar, UseOfOwnCar, UsingOwnCar}
import models.requests.ClaimantRequest
import play.api.mvc.{AnyContent, Result}
import utils.{Navigator, UserAnswers}
import views.html.useCompanyCar

import scala.concurrent.Future

class UseCompanyCarController @Inject()(appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        getClaimant: GetClaimantAction,
                                        formProvider: UseCompanyCarFormProvider) extends FrontendController with I18nSupport {

  def onPageLoad() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getUseOfOwnCar {
        useOfOwnCar =>

          val form: Form[Boolean] = formProvider(request.claimant, useOfOwnCar)

          val preparedForm = request.userAnswers.useCompanyCar match {
            case None => form
            case Some(value) => form.fill(value)
          }
          Future.successful(Ok(useCompanyCar(appConfig, preparedForm, request.claimant, useOfOwnCar)))
      }
  }

  def onSubmit() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getUseOfOwnCar {
        useOfOwnCar =>

          val form: Form[Boolean] = formProvider(request.claimant, useOfOwnCar)

          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(useCompanyCar(appConfig, formWithErrors, request.claimant, useOfOwnCar))),
            (value) =>
              dataCacheConnector.save[Boolean](request.sessionId, UseCompanyCarId, value).map(cacheMap =>
                Redirect(navigator.nextPage(UseCompanyCarId)(new UserAnswers(cacheMap))))
          )
      }
  }

  private def getUseOfOwnCar(block: UseOfOwnCar => Future[Result])
                            (implicit request: ClaimantRequest[AnyContent]): Future[Result] = {

    request.userAnswers.useOwnCar match {
      case Some(true)  => block(UsingOwnCar)
      case Some(false) => block(NotUsingOwnCar)
      case None        => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}
