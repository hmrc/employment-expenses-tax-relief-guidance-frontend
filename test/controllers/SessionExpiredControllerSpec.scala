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
import config.FrontendAppConfig
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SessionExpiredControllerSpec extends SpecBase with MockitoSugar {

  "SessionExpired Controller" must {

    val mockAppConfig = mock[FrontendAppConfig]
    when(mockAppConfig.taxReliefForEmployeesUrl).thenReturn("https://www.gov.uk/tax-relief-for-employees")
    when(mockAppConfig.contactFormServiceIdentifier).thenReturn("test")
    when(mockAppConfig.contactHost).thenReturn("https://www.gov.uk/")
    val application = applicationBuilder()
      .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
      .build()

    val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad.url)

    "Return OK and correct view for get when the WFH toggle is disabled" in {

      when(mockAppConfig.workingFromHomeExpensesOnlyEnabled).thenReturn(false)

      val result = route(application, request).value

      status(result) mustEqual OK
    }

    "Return OK and correct view for get when the WFH toggle is enabled" in {

      when(mockAppConfig.workingFromHomeExpensesOnlyEnabled).thenReturn(true)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }
  }

}
