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
import models.requests.OptionalDataRequest
import play.api.mvc.Request
import utils.UserAnswers

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WfhDueToCovidStatusService @Inject()(dataCacheConnector: DataCacheConnector)(implicit ec: ExecutionContext) {

  def getStatusBySessionId[A](sessionId: String)(request: Request[A]): Future[Option[String]] = {

    dataCacheConnector.fetchBySessionId(sessionId).map {
      case None => None
      case Some(data) =>
        val dataRequestResult = OptionalDataRequest(request, sessionId, Some(new UserAnswers(data)))
        dataRequestResult.userAnswers.map(_.whichYearsAreYouClaimingFor).map(x =>x).flatten.map(_.toString)
    }
  }

}
