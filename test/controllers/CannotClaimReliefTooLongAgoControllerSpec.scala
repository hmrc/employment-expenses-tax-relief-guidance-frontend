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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.time.TaxYear
import views.html.cannotClaimReliefTooLongAgo

class CannotClaimReliefTooLongAgoControllerSpec extends SpecBase {

  private val endYear = TaxYear.current.finishYear.toString
  private val startYear = TaxYear.current.startYear.toString

  "CannotClaimReliefTooLongAgo Controller" must {

    "return OK and the correct view for a GET" in {
      val application = applicationBuilder(Some(claimantIdCacheMap)).build()

      val request = FakeRequest(GET, routes.CannotClaimReliefTooLongAgoController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[cannotClaimReliefTooLongAgo]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(frontendAppConfig, claimant, startYear, endYear)(fakeRequest, messages).toString

      application.stop
    }
  }
}
