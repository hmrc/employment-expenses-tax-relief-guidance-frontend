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

package controllers.actions

import base.SpecBase
import models.requests.{DataRequest, OptionalDataRequest}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Result
import play.api.test.Helpers._
import utils.UserAnswers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val sessionId = "abc"

  class Harness() extends DataRequiredActionImpl() {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "Data Required Action" when {

    "there is no data in the incoming request" must {

      "redirect to Session Expired" in {
        val incomingRequest = OptionalDataRequest(fakeRequest, sessionId, None)
        val action = new Harness()
        val futureResult = action.callRefine(incomingRequest)

        whenReady(futureResult) {

          case Left(httpResult) =>
            httpResult.header.status mustBe SEE_OTHER
            httpResult.header.headers(LOCATION) mustEqual sessionExpiredUrl
            
          case Right(_) =>
            fail("Expected left but got right")
        }
      }
    }

    "there is data in the incoming request" must {

      "return a DataRequest" in {
        val answers = mock[UserAnswers]
        val incomingRequest = OptionalDataRequest(fakeRequest, sessionId, Some(answers))
        val action = new Harness()
        val futureResult = action.callRefine(incomingRequest)

        whenReady(futureResult) { result =>
          result.isRight mustBe true
        }
      }
    }
  }
}
