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

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.ClaimingForFormProvider
import identifiers.ClaimingForId
import javax.inject.Inject
import models.ClaimingFor
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.claimingFor

import scala.concurrent.{ExecutionContext, Future}

class ClaimingForController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        getClaimant: GetClaimantAction,
                                        formProvider: ClaimingForFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: claimingFor
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  def onPageLoad: Action[AnyContent] = (Action andThen getData andThen requireData andThen getClaimant) {
    implicit request =>
      val form = formProvider(request.claimant)

      val preparedForm = request.userAnswers.claimingFor match {
        case None => form
        case Some(value) => form.fill(value.toSet)
      }
      Ok(view(appConfig, preparedForm, request.claimant))
  }

  def onSubmit: Action[AnyContent] = (Action andThen getData andThen requireData andThen getClaimant).async {
    implicit request =>
      val form = formProvider(request.claimant)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(appConfig, formWithErrors, request.claimant))),
        value =>
          dataCacheConnector.save[Set[ClaimingFor]](request.sessionId, ClaimingForId, value).map(cacheMap =>
            Redirect(navigator.nextPage(ClaimingForId)(new UserAnswers(cacheMap))))
      )
  }
}
