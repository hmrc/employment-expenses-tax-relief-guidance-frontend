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

import controllers.actions.{DataRequiredAction, DataRetrievalAction}
import identifiers.InformCustomerClaimNowInWeeksId
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Navigator
import views.html.InformCustomerClaimNowInWeeksView

class InformCustomerClaimNowInWeeksController @Inject() (
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    navigator: Navigator,
    informClaimNowInWeeksView: InformCustomerClaimNowInWeeksView,
    val controllerComponents: MessagesControllerComponents
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData)(implicit request => Ok(informClaimNowInWeeksView()))

  def onSubmit: Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    Redirect(navigator.nextPage(InformCustomerClaimNowInWeeksId)(request.userAnswers))
  }

}
