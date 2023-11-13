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

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.EmployerPaidBackExpensesFormProvider
import identifiers.EmployerPaidBackExpensesId
import models.EmployerPaid

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Navigator, UserAnswers}
import views.html.EmployerPaidBackExpensesView

import scala.concurrent.{ExecutionContext, Future}

class EmployerPaidBackExpensesController @Inject()(
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    formProvider: EmployerPaidBackExpensesFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: EmployerPaidBackExpensesView,
                                                    appConfig: FrontendAppConfig
                                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[EmployerPaid] = formProvider()

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.employerPaidBackExpenses match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit: Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),
        value => {

          dataCacheConnector.save[EmployerPaid](request.sessionId, EmployerPaidBackExpensesId, value).map(cacheMap =>
            Redirect(navigator.nextPage(EmployerPaidBackExpensesId)(new UserAnswers(cacheMap)))
          )

        }

      )
  }
}
