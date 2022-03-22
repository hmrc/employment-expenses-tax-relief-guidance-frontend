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
import forms.{RegisteredForSelfAssessmentFormProvider, RegisteredForSelfAssessmentSelfAssessmentFormProvider}
import identifiers.RegisteredForSelfAssessmentId
import models.requests.ClaimantRequest

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Navigator, UserAnswers}
import views.html.RegisteredForSelfAssessmentView

import scala.concurrent.{ExecutionContext, Future}

class RegisteredForSelfAssessmentController @Inject()(
                                                       dataCacheConnector: DataCacheConnector,
                                                       navigator: Navigator,
                                                       getData: DataRetrievalAction,
                                                       requireData: DataRequiredAction,
                                                       getClaimant: GetClaimantAction,
                                                       formProvider: RegisteredForSelfAssessmentFormProvider,
                                                       formProviderSelfAssessment: RegisteredForSelfAssessmentSelfAssessmentFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: RegisteredForSelfAssessmentView,
                                                       appConfig: FrontendAppConfig
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData andThen getClaimant) {
    implicit request =>
      val form: Form[Boolean] = formProvider(request.claimant)

      val preparedForm = request.userAnswers.registeredForSelfAssessment match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val backButtonOverride = BackButtonSetting(request)

      Ok(view(preparedForm, request.claimant, backButtonOverride))
  }

  def onSubmit: Action[AnyContent] = (getData andThen requireData andThen getClaimant).async {
    implicit request =>

      val backButtonOverride = BackButtonSetting(request)

      val form: Form[Boolean] = if (backButtonOverride.nonEmpty) {
        formProviderSelfAssessment(request.claimant)
      } else {
        formProvider(request.claimant)
      }

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(view(formWithErrors, request.claimant, backButtonOverride)))
        },
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, RegisteredForSelfAssessmentId, value).map(cacheMap =>
            Redirect(navigator.nextPage(RegisteredForSelfAssessmentId)(new UserAnswers(cacheMap)))
          )
      )
  }

  private def BackButtonSetting(request: ClaimantRequest[AnyContent]): Option[String] = {
    request.userAnswers.claimAnyOtherExpense match {
      case Some(true) => Some(appConfig.registeredForSelfBackButtonOverride)
      case _ => None
    }
  }
}
