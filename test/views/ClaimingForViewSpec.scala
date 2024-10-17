/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.ClaimingForFormProvider
import models.ClaimingFor
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.twirl.api.Html
import views.behaviours.CheckboxViewBehaviours
import views.html.ClaimingForView

class ClaimingForViewSpec extends CheckboxViewBehaviours[ClaimingFor] with MockitoSugar {

  val messageKeyPrefix: String = "claimingFor"
  val form = new ClaimingForFormProvider()()

  // Create an instance of the application with the provided `freOnlyJourneyEnabled` value
  def createApp(freJourneyEnabled: Boolean): Application = {
    val mockAppConfig = mock[FrontendAppConfig]
    when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(freJourneyEnabled)

    GuiceApplicationBuilder()
      .overrides(bind[FrontendAppConfig].toInstance(mockAppConfig))
      .configure("play.http.router" -> "testOnlyDoNotUseInAppConf.Routes")
      .build()
  }

  // Create the view using the provided form and application
  def createView(form: Form[_], freJourneyEnabled: Boolean): Html = {
    val app = createApp(freJourneyEnabled)
    val view: ClaimingForView = app.injector.instanceOf[ClaimingForView]
    val result = view.apply(form)(fakeRequest, messages)
    app.stop()
    result
  }

  "ClaimingFor view" when {

    "freOnlyJourneyEnabled is true" must {

        val doc = asDocument(createView(form, freJourneyEnabled = true))

        behave like normalPage(createView(form, freJourneyEnabled = true), messageKeyPrefix)
        behave like checkboxPage(
          form,
          form => createView(form, freJourneyEnabled = true),
          messageKeyPrefix,
          ClaimingFor.options(onlineJourneyShutterEnabled = false, freOnlyJourneyEnabled = true)
        )

    }

    "freOnlyJourneyEnabled is false" must {

        val doc = asDocument(createView(form, freJourneyEnabled = false))

        behave like normalPage(createView(form, freJourneyEnabled = false), messageKeyPrefix)
        behave like checkboxPage(
          form,
          form => createView(form, freJourneyEnabled = false),
          messageKeyPrefix,
          ClaimingFor.options(onlineJourneyShutterEnabled = false, freOnlyJourneyEnabled = false)
        )

    }

    "rendered" must {
      "contain checkboxes for each option" in {
        val doc = asDocument(createView(form, freJourneyEnabled = true))
        for ((option, index) <- ClaimingFor.options(onlineJourneyShutterEnabled = false, freOnlyJourneyEnabled = true).zipWithIndex) {
          assertContainsRadioButton(doc, option.id.get, s"value[$index]", option.value, isChecked = false)
        }
      }
    }
  }
}
