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

import views.behaviours.NewViewBehaviours
import views.html.CannotClaimReliefView
import config.FrontendAppConfig
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.Application
import play.api.test.Helpers._
import play.api.inject.bind

class CannotClaimReliefViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix = "cannotClaimRelief"
  val mockAppConfig    = mock[FrontendAppConfig]

  val application: Application = GuiceApplicationBuilder()
    .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig)) // Bind the mock AppConfig
    .configure("play.http.router" -> "testOnlyDoNotUseInAppConf.Routes") // Ensure routing is set up correctly
    .build()

  val view: CannotClaimReliefView = application.injector.instanceOf[CannotClaimReliefView]

  def createView()(implicit request: play.api.mvc.Request[_], messages: play.api.i18n.Messages) =
    view.apply()

  "CannotClaimRelief view" must {

    "render correctly when freOnlyJourneyEnabled is false" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
      when(mockAppConfig.jobExpensesGuidanceUrl).thenReturn("http://guidance.url")
      when(mockAppConfig.jobExpensesGuidanceUrl).thenReturn("http://guidance.url")
      implicit val request  = fakeRequest
      implicit val messages = this.messages

      val viewWithFalseFlag = createView()

      val doc = asDocument(viewWithFalseFlag)
      assertContainsText(doc, messages(s"$messageKeyPrefix.heading_old"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.guidance"))
      assertRenderedByAttribute(doc, "data-module", Some("hmrc-back-link"))

    }

    "render correctly when freOnlyJourneyEnabled is true" in {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)

      implicit val request  = fakeRequest
      implicit val messages = this.messages

      val viewWithTrueFlag = createView()

      val doc = asDocument(viewWithTrueFlag)
      assertContainsText(doc, messages(s"$messageKeyPrefix.heading"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.body"))
      assertRenderedByAttribute(doc, "data-module", Some("hmrc-back-link"))
    }
  }

  // Clean up application
  application.stop()
}
