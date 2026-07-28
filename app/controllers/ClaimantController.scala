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
import controllers.actions.*
import forms.ClaimantFormProvider
import identifiers.ClaimantId

import javax.inject.Inject
import models.Claimant
import models.requests.DataRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.ClaimantView

import scala.concurrent.{ExecutionContext, Future}

class ClaimantController @Inject() (
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: ClaimantFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: ClaimantView
)(using ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Enumerable.Implicits {

  val form: Form[Claimant] = formProvider()

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData) { request =>
    given DataRequest[_] = request
    val preparedForm = request.userAnswers.claimant match {
      case None        => form
      case Some(value) => form.fill(value)
    }
    Ok(view(preparedForm))
  }

  def onSubmit: Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[_] = request
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[?]) => Future.successful(BadRequest(view(formWithErrors))),
        value =>
          dataCacheConnector
            .save[Claimant](request.sessionId, ClaimantId, value)
            .map(cacheMap => Redirect(navigator.nextPage(ClaimantId)(new UserAnswers(cacheMap))))
      )
  }

}
