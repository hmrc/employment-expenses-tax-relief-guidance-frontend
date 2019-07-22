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
import controllers.actions._
import identifiers.NotEntitledSomeYearsId
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.Navigator
import views.html.NotEntitledSomeYearsView

class NotEntitledSomeYearsController @Inject()(
                                                appConfig: FrontendAppConfig,
                                                navigator: Navigator,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                getClaimant: GetClaimantAction,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: NotEntitledSomeYearsView
                                              ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (Action andThen getData andThen requireData andThen getClaimant) {
    implicit request =>
      val nextPage = navigator.nextPage(NotEntitledSomeYearsId)(request.userAnswers)
      Ok(view(appConfig, request.claimant, nextPage))
  }
}
