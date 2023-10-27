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
import forms.ClaimingForFormProvider
import identifiers.ClaimingForId
import javax.inject.Inject
import models.ClaimingFor
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.ClaimingForView

import scala.concurrent.{ExecutionContext, Future}

class ClaimingForController @Inject()(
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        formProvider: ClaimingForFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: ClaimingForView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  def onPageLoad: Action[AnyContent] = getData {
    implicit request =>
      val form = formProvider()
      val preparedForm = request.userAnswers.flatMap(_.claimingFor) match {
        case None => form
        case Some(value) => form.fill(value.toSet)
      }

      val backLinkEnabled: Boolean = request.userAnswers.flatMap(_.claimAnyOtherExpense) match {
        case None | Some(true) => false
        case Some(false)       => true
      }

      Ok(view(preparedForm, backLinkEnabled))
  }

  def onSubmit: Action[AnyContent] = getData.async {
    implicit request =>
      val form = formProvider()

      val backLinkEnabled: Boolean = request.userAnswers.flatMap(_.claimAnyOtherExpense) match {
        case None | Some(true) => false
        case Some(false) => true
      }

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, backLinkEnabled))),
        value =>
          dataCacheConnector.save[Set[ClaimingFor]](request.sessionId, ClaimingForId, value).map(cacheMap =>
            Redirect(navigator.nextPage(ClaimingForId)(new UserAnswers(cacheMap))))
      )
  }
}
