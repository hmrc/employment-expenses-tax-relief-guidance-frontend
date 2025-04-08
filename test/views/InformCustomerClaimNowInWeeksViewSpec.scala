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

package views

import play.api.Application
import play.twirl.api.Html
import views.behaviours.NewViewBehaviours
import views.html.InformCustomerClaimNowInWeeksView
import config.FrontendAppConfig
import base.SpecBase
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._

class InformCustomerClaimNowInWeeksViewSpec extends NewViewBehaviours with MockitoSugar {

  val mockAppConfig = mock[FrontendAppConfig]

  val messageKeyPrefix: String = "informCustomerClaimNowInWeeks"

  val application = applicationBuilder()
    .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
    .build()

  val view: InformCustomerClaimNowInWeeksView = application.injector.instanceOf[InformCustomerClaimNowInWeeksView]

  def createView(): Html = view.apply()(fakeRequest, messages)

  val para1 = "If you work at home one or more days in a week, you can claim for that whole week."

  val para2_old =
    "If you are not sure how many weeks you will be eligible to claim for, we advise you to wait until you know because any further changes cannot be made using this service and may take longer to process."

  val para2 =
    "If you are not sure how many weeks you will be eligible to claim for, we advise you to wait until you know because any further changes may take longer to process."

  val para3 = "If you would like to claim now, we will check to see if you are eligible."

  "Inform customer claim now in weeks view" should {

    "have the correct banner title" in {
      val doc    = asDocument(createView())
      val banner = doc.select(".govuk-header__service-name")

      banner.text() mustEqual messages("service.name")
    }

    "show content" when {
      "when onlineJourneyShutterEnabled is enabled- all informCustomerClaimNowInWeeksView content is displayed " in {
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        val doc = asDocument(createView())
        assertContainsMessages(doc, para1)
        assertContainsMessages(doc, para2)
        assertContainsMessages(doc, para3)

      }
      "when onlineJourneyShutterEnabled is disabled- all informCustomerClaimNowInWeeksView content is displayed " in {
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
        val doc = asDocument(createView())
        assertContainsMessages(doc, para1)
        assertContainsMessages(doc, para2_old)
        assertContainsMessages(doc, para3)
      }
      "when onlinefreJourneyEnabled is enabled- all informCustomerClaimNowInWeeksView content is displayed " in {
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
        val doc = asDocument(createView())
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading_freOnly"))
      }
      "when onlinefreJourneyEnabled is disabled- all informCustomerClaimNowInWeeksView content is displayed " in {
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        val doc = asDocument(createView())
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading"))
      }
    }

    behave.like(pageWithBackLink(createView()))

  }

}
