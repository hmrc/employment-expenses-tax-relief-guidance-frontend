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

package utils

import base.SpecBase
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import controllers.routes
import identifiers._
import models.Claimant

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" must {
    "go to the Index view" when {
      "an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier)(mock[UserAnswers]) mustBe routes.IndexController.onPageLoad()
      }
    }
    "go to the RegisterForSelfAssessment view" when {
      "navigating from the claimant view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant)
          .thenReturn(Some(Claimant.You))

        navigator.nextPage(ClaimantId)(mock[UserAnswers]) mustBe routes.RegisteredForSelfAssessmentController.onPageLoad()
      }
    }
  }
}
