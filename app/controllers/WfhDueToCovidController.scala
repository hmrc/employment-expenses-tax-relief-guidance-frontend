/*
 * Copyright 2021 HM Revenue & Customs
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
import forms.CovidHomeWorkingFormProvider
import identifiers.CovidHomeWorkingId
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.WfhDueToCovidView

import scala.concurrent.{ExecutionContext, Future}

class WfhDueToCovidController @Inject()(
                                     dataCacheConnector: DataCacheConnector,
                                     navigator: Navigator,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     flowEnabled: WorkingFromHomeEnabledAction,
                                     formProvider: CovidHomeWorkingFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: WfhDueToCovidView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form: Form[Boolean] = formProvider()

  def onPageLoad: Action[AnyContent] = (flowEnabled andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.covidHomeWorking match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm))
  }

  def onSubmit: Action[AnyContent] = (flowEnabled andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, CovidHomeWorkingId, value).map(cacheMap =>
            Redirect(navigator.nextPage(CovidHomeWorkingId)(new UserAnswers(cacheMap)))
          )
      )
  }
}
