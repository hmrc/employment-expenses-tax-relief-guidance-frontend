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

import config.FrontendAppConfig
import controllers.routes
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import views.behaviours.NewViewBehaviours
import org.mockito.Mockito.when
import views.html.DisclaimerView
import play.twirl.api.Html
import uk.gov.hmrc.time.TaxYear

class DisclaimerViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix: String = "disclaimer"

  val mockAppConfig = mock[FrontendAppConfig]

  val application: Application = applicationBuilder()
    .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
    .build()

  val view: DisclaimerView = application.injector.instanceOf[DisclaimerView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  def createView(): Html = view.apply()(fakeRequest, messages)

  val earliestTaxYear: String =
    TaxYear.current.back(4).startYear.toString

  "DisclaimerView" should {
    behave.like(normalPage(createView(), messageKeyPrefix))
    behave.like(pageWithBackLink(createView()))
  }


  "DisclaimerView title and heading" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, "disclaimer.title")
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.heading"))

  }
  "show new summary" when {
    "when workingFromHomePolicyChangeEnabled is enabled- notificationBanner content  is displayed " in {
      when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(true)
      when(mockAppConfig.earliestTaxYear).thenReturn(earliestTaxYear)
      val doc = asDocument(createView())
      doc.text() must include(messages("disclaimer.guidance.summary_wfhPolicyChange", earliestTaxYear))
      doc.text() must include(messages("disclaimer.claim.after.h2_wfhPolicyChange", earliestTaxYear))
      doc.text() must include(messages("disclaimer.accept_wfhPolicyChange"))
    }

    "when workingFromHomePolicyChangeEnabled is disabled- notificationBanner content  is displayed" in {
      when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(false)
      val doc = asDocument(createView())
      assertContainsMessages(doc, messages(s"$messageKeyPrefix.guidance.summary_freOnly"))
      assertContainsMessages(doc, messages(s"$messageKeyPrefix.accept"))
      assertContainsMessages(doc, messages(s"$messageKeyPrefix.claim.before.insetText"))
    }
  }


  "when workingFromHomePolicyChangeEnabled is enabled-  warning content is displayed " in {
    when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(true)
    val doc = asDocument(createView())
    doc.text() must include(messages("disclaimer.warning_wfhPolicyChange"))
  }

  "when workingFromHomePolicyChangeEnabled is disabled-  warning content is displayed " in {
    when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(false)
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.warning"))

  }

  "when workingFromHomePolicyChangeEnabled is enabled-  button content is displayed " in {
    when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(true)
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.button.continue_wfhPolicyChange"))

  }

  "when workingFromHomePolicyChangeEnabled is disabled-  button content is displayed " in {
    when(mockAppConfig.workingFromHomePolicyChangeEnabled).thenReturn(false)
    val doc = asDocument(createView())
    assertContainsMessages(doc, messages(s"$messageKeyPrefix.button.continue"))
  }
  application.stop()
}
