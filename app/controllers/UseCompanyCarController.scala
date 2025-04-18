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

import connectors.DataCacheConnector
import controllers.actions._
import forms.UseCompanyCarFormProvider
import identifiers.UseCompanyCarId

import javax.inject.Inject
import models.requests.DataRequest
import models.{NotUsingOwnCar, UseOfOwnCar, UsingOwnCar}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Navigator, UserAnswers}
import views.html.UseCompanyCarView

import scala.concurrent.{ExecutionContext, Future}

class UseCompanyCarController @Inject() (
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: UseCompanyCarFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: UseCompanyCarView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    getUseOfOwnCar { useOfOwnCar =>
      val form: Form[Boolean] = formProvider(useOfOwnCar)

      val preparedForm = request.userAnswers.useCompanyCar match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Future.successful(Ok(view(preparedForm, useOfOwnCar)))
    }
  }

  def onSubmit: Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    getUseOfOwnCar { useOfOwnCar =>
      val form: Form[Boolean] = formProvider(useOfOwnCar)

      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, useOfOwnCar))),
          value =>
            dataCacheConnector
              .save[Boolean](request.sessionId, UseCompanyCarId, value)
              .map(cacheMap => Redirect(navigator.nextPage(UseCompanyCarId)(new UserAnswers(cacheMap))))
        )
    }
  }

  private def getUseOfOwnCar(
      block: UseOfOwnCar => Future[Result]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =

    request.userAnswers.useOwnCar match {
      case Some(true)  => block(UsingOwnCar)
      case Some(false) => block(NotUsingOwnCar)
      case None        => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad))
    }

}
