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

import controllers.actions._
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import views.html.CannotClaimReliefTooLongAgoView

class CannotClaimReliefTooLongAgoController @Inject() (
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    view: CannotClaimReliefTooLongAgoView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData) { implicit request =>
    val startYear = TaxYear.current.startYear.toString
    val endYear   = TaxYear.current.finishYear.toString

    Ok(view(startYear, endYear))
  }

}
