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
import forms.ClaimAnyOtherExpenseFormProvider
import identifiers.{ClaimAnyOtherExpenseId, ClaimantId}

import javax.inject.Inject
import models.Claimant
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsBoolean, JsString, JsValue}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.ClaimAnyOtherExpenseView

import scala.concurrent.{ExecutionContext, Future}



class ClaimAnyOtherExpenseController @Inject()(
                                                dataCacheConnector: DataCacheConnector,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                flowEnabled: WorkingFromHomeEnabledAction,
                                                formProvider: ClaimAnyOtherExpenseFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: ClaimAnyOtherExpenseView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController
  with I18nSupport with Enumerable.Implicits with Logging {

  val form: Form[Boolean] = formProvider()

  def redirectToHome: Action[AnyContent] = (flowEnabled andThen getData) {
    implicit request =>
      Redirect(routes.ClaimAnyOtherExpenseController.onPageLoad())
  }
  def onPageLoad: Action[AnyContent] = (flowEnabled andThen getData) {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(_.claimAnyOtherExpense) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm))
  }

  def onSubmit: Action[AnyContent] = (flowEnabled andThen getData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),
        value => {

          val cacheMap = CacheMap(request.sessionId, Map[String, JsValue](
            ClaimantId.toString                     -> JsString( Claimant.You.string),
            ClaimAnyOtherExpenseId.toString  -> JsBoolean(value)
          ))

          logger.info(s"Saving/caching data for session with id [${request.sessionId}]")

          dataCacheConnector.save(cacheMap).map(cacheMap =>
            Redirect(navigator.nextPage(ClaimAnyOtherExpenseId)(new UserAnswers(cacheMap)))
          )

        }

      )
  }

}
