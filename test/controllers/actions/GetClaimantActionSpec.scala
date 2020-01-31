/*
 * Copyright 2020 HM Revenue & Customs
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
import models.Claimant.You
import models.requests.DataRequest
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import utils.UserAnswers

import scala.concurrent.ExecutionContext.Implicits.global

class GetClaimantActionSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val sessionId = "abc"
  class Harness() extends GetClaimantActionImpl() {
    def callRefine[A](request: DataRequest[A]) = refine(request)
  }

  "Get Claimant Action" when {

    "claimant is not present in the incoming request" must {

      "redirect to Session Expired" in {
        val answers = mock[UserAnswers]
        when(answers.claimant) thenReturn None

        val incomingRequest = DataRequest(fakeRequest, sessionId, answers)
        val action = new Harness()
        val futureResult = action.callRefine(incomingRequest)

        whenReady(futureResult) {

          case Left(httpResult) =>
            httpResult.header.status mustEqual SEE_OTHER
            httpResult.header.headers(LOCATION) mustEqual sessionExpiredUrl

          case Right(_) =>
            fail("Expected left but got right")
        }
      }
    }

    "claiming is present in the incoming request" must {

      "return a ClaimantRequest" in {
        val answers = mock[UserAnswers]
        when(answers.claimant) thenReturn Some(You)

        val incomingRequest = DataRequest(fakeRequest, sessionId, answers)
        val action = new Harness()
        val futureResult = action.callRefine(incomingRequest)

        whenReady(futureResult) {

          case Right(result) =>
            result.claimant mustEqual You

          case Left(_) =>
            fail("Expected right but got left")
        }
      }
    }
  }
}
