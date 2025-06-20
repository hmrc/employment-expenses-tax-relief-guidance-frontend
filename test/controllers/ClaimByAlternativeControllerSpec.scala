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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ClaimByAlternativeControllerSpec extends SpecBase {

  lazy val claimByAlternativeRoute: String = routes.ClaimByAlternativeController.onPageLoad().url

  "ClaimByAlternative Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(Some(claimantIdCacheMap)).build()

      val request = FakeRequest(GET, claimByAlternativeRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder().build()

      val request = FakeRequest(GET, claimByAlternativeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

  }

}
