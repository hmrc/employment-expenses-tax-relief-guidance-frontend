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

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import models.ClaimYears
import models.ClaimYears.ThisYear
import models.requests.ClaimantRequest
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.time.TaxYear
import views.html.willNotPayTax

import scala.concurrent.Future

class WillNotPayTaxController @Inject()(appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        getClaimant: GetClaimantAction) extends FrontendController with I18nSupport {

  def onPageLoad = (getData andThen requireData andThen getClaimant).async {
    implicit request =>
      getDatesForYear {
        (taxYear) =>
          Future.successful(Ok(willNotPayTax(appConfig, request.claimant, taxYear.startYear.toString, taxYear.finishYear.toString)))
      }
  }

  private def getDatesForYear(block: TaxYear => Future[Result])
                             (implicit request: ClaimantRequest[AnyContent]): Future[Result] = {

    request.userAnswers.taxYears match {

      case Some(list) if list.contains(ThisYear) =>
        block(ClaimYears.getTaxYear(ThisYear))
      case _ =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}
