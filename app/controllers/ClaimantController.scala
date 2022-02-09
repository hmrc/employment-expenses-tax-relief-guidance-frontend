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

import connectors.DataCacheConnector
import controllers.actions._
import forms.ClaimantFormProvider
import identifiers.ClaimantId
import javax.inject.Inject
import models.Claimant
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsString, JsValue}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.ClaimantView

import scala.concurrent.{ExecutionContext, Future}

class ClaimantController @Inject()(
                                    dataCacheConnector: DataCacheConnector,
                                    navigator: Navigator,
                                    getData: DataRetrievalAction,
                                    formProvider: ClaimantFormProvider,
                                    val controllerComponents: MessagesControllerComponents,
                                    view: ClaimantView
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form: Form[Claimant] = formProvider()

  def onPageLoad: Action[AnyContent] = getData {
    implicit request =>
      val preparedForm = request.userAnswers.flatMap(_.claimant) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm))
  }

  def onSubmit: Action[AnyContent] = getData.async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),
        value => {

          val cacheMap = CacheMap(
            request.sessionId,
            Map[String, JsValue](ClaimantId.toString -> JsString(value.toString))
          )

          dataCacheConnector.save(cacheMap).map(cacheMap =>
            Redirect(navigator.nextPage(ClaimantId)(new UserAnswers(cacheMap)))
          )

        }

      )
  }
}
