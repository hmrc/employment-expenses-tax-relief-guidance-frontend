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
import forms.RegisteredForSelfAssessmentFormProvider
import identifiers.RegisteredForSelfAssessmentId
import utils.{Navigator, UserAnswers}
import views.html.registeredForSelfAssessment

import scala.concurrent.Future

class RegisteredForSelfAssessmentController @Inject()(appConfig: FrontendAppConfig,
                                                      override val messagesApi: MessagesApi,
                                                      dataCacheConnector: DataCacheConnector,
                                                      navigator: Navigator,
                                                      getData: DataRetrievalAction,
                                                      requireData: DataRequiredAction,
                                                      getClaimant: GetClaimantAction,
                                                      formProvider: RegisteredForSelfAssessmentFormProvider) extends FrontendController with I18nSupport {

  def onPageLoad() = (getData andThen requireData andThen getClaimant) {
    implicit request =>
      val form: Form[Boolean] = formProvider(request.claimant)
      val preparedForm = request.userAnswers.registeredForSelfAssessment match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(registeredForSelfAssessment(appConfig, preparedForm, request.claimant))
  }

  def onSubmit() = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      val form: Form[Boolean] = formProvider(request.claimant)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(registeredForSelfAssessment(appConfig, formWithErrors, request.claimant))),
        (value) =>
          dataCacheConnector.save[Boolean](request.sessionId, RegisteredForSelfAssessmentId, value).map(cacheMap =>
            Redirect(navigator.nextPage(RegisteredForSelfAssessmentId)(new UserAnswers(cacheMap))))
      )
  }
}
