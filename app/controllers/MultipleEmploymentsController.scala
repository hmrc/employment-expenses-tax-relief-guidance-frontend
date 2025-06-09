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

import controllers.actions._
import forms.MultipleEmploymentsFormProvider
import identifiers.MultipleEmploymentsId
import connectors.DataCacheConnector
import models.MultipleEmployments

import javax.inject.Inject
import utils.{Navigator, UserAnswers}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.MultipleEmploymentsView

import scala.concurrent.{ExecutionContext, Future}

class MultipleEmploymentsController @Inject() (
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: MultipleEmploymentsFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: MultipleEmploymentsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form: Form[MultipleEmployments] = formProvider()

  def onPageLoad: Action[AnyContent] =
    getData.andThen(requireData) { implicit request =>
      val preparedForm = request.userAnswers.multipleEmployments match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
    }

  def onSubmit: Action[AnyContent] =
    getData.andThen(requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            dataCacheConnector
              .save[MultipleEmployments](request.sessionId, MultipleEmploymentsId, value)
              .map(cacheMap => Redirect(navigator.nextPage(MultipleEmploymentsId)(new UserAnswers(cacheMap))))
        )
    }

}
