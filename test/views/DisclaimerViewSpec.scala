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
import config.FrontendAppConfig
import org.mockito.Mockito.when
import views.html.DisclaimerView
import play.api.Application
import play.twirl.api.Html



class DisclaimerViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix: String = "disclaimer"

  val mockAppConfig = mock[FrontendAppConfig]

  val application: Application = applicationBuilder()
    .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
    .build()

  val view: DisclaimerView = application.injector.instanceOf[DisclaimerView]

  def onwardRoute: Call = routes.IndexController.onPageLoad

  def createView(): Html = view.apply()(fakeRequest, messages)

  "DisclaimerView" should {
    behave like normalPage(createView(), messageKeyPrefix)
    behave like pageWithBackLink(createView())
  }
    "show new summary" when {
      "when onlinefreJourneyEnabled is enabled- all disclaimerView content is displayed " in {
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
        val doc = asDocument(createView())
        assertContainsMessages(doc, messages(s"${messageKeyPrefix}.guidance.summary_freOnly"))
      }

     "when onlinefreJourneyEnabled is disabled- all disclaimerView content is displayed " in {
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        val doc = asDocument(createView())
        assertContainsMessages(doc,messages(s"${messageKeyPrefix}.guidance.summary"))
      }
      "when onlinefreJourneyEnabled is enabled- all disclaimerView insetText is displayed " in {
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)
        val doc = asDocument(createView())
        assertContainsMessages(doc, messages(s"${messageKeyPrefix}.claim.after.insetText_freOnly"))
      }
      "when onlinefreJourneyEnabled is disabled- all disclaimerView insetText is displayed " in {
        when(mockAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
        when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
        val doc = asDocument(createView())
        assertContainsMessages(doc, messages(s"${messageKeyPrefix}.claim.after.insetText"))
      }
    }
    application.stop()
  }
