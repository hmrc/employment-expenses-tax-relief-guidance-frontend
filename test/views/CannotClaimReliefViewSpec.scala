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

class CannotClaimReliefViewSpec extends NewViewBehaviours with MockitoSugar {

  val messageKeyPrefix = s"cannotClaimRelief"
  val mockAppConfig = mock[FrontendAppConfig]
  val application = applicationBuilder().build()
  val view = application.injector.instanceOf[CannotClaimReliefView]

  def createView(appConfig: FrontendAppConfig) = view.apply()(fakeRequest, messages)

  "CannotClaimRelief view" must {
    when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)
    val viewWithFalseFlag = createView(mockAppConfig)
    behave like pageWithBackLink(viewWithFalseFlag)
    }

    "CannotClaimRelief view when freOnlyJourneyEnabled is false" must {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(false)

      val viewWithFalseFlag = createView(mockAppConfig)

      "contain the old heading and guidance link" in {
        val doc = asDocument(viewWithFalseFlag)
        assertContainsText(doc, messages(s"${messageKeyPrefix}.heading_old"))
      }
    }

    "CannotClaimRelief view when freOnlyJourneyEnabled is true" must {
      when(mockAppConfig.freOnlyJourneyEnabled).thenReturn(true)

      val viewWithFalseFlag = createView(mockAppConfig)

      "contain the new heading and body text" in {
        val doc = asDocument(viewWithFalseFlag)
        assertContainsText(doc, messages(s"${messageKeyPrefix}.heading"))
        assertContainsText(doc, messages(s"${messageKeyPrefix}.body"))
      }
    }

    application.stop()
  }
