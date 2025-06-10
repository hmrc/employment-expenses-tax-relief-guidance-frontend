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
import forms.ClaimingForMoreThanOneJobFormProvider
import identifiers.ClaimingForMoreThanOneJobId
import connectors.DataCacheConnector
import models.ClaimingForMoreThanOneJob

import javax.inject.Inject
import utils.{Navigator, UserAnswers}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ClaimingForMoreThanOneJobView

import scala.concurrent.{ExecutionContext, Future}

class ClaimingForMoreThanOneJobController @Inject() (
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: ClaimingForMoreThanOneJobFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: ClaimingForMoreThanOneJobView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form: Form[ClaimingForMoreThanOneJob] = formProvider()

  def onPageLoad: Action[AnyContent] =
    getData.andThen(requireData) { implicit request =>
      val preparedForm = request.userAnswers.claimingForMoreThanOneJob match {
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
              .save[ClaimingForMoreThanOneJob](request.sessionId, ClaimingForMoreThanOneJobId, value)
              .map(cacheMap => Redirect(navigator.nextPage(ClaimingForMoreThanOneJobId)(new UserAnswers(cacheMap))))
        )
    }

}
