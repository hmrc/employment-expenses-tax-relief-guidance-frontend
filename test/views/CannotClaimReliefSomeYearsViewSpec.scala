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

package views

import controllers.routes
import models.Claimant.You
import views.behaviours.ViewBehaviours
import views.html.cannotClaimReliefSomeYears

class CannotClaimReliefSomeYearsViewSpec extends ViewBehaviours {

  def onwardRoute = routes.IndexController.onPageLoad()

  val messageKeyPrefix = s"cannotClaimReliefSomeYears.$claimant"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[cannotClaimReliefSomeYears]

  def createView= view.apply(frontendAppConfig, claimant, onwardRoute)(fakeRequest, messages)

  "CannotClaimReliefSomeYears view" must {

    behave like pageWithBackLink(createView)

    "have the correct banner title" in {
      val doc = asDocument(createView)
      assertRenderedById(doc, "pageTitle")
    }

    "display the correct browser title" in {
      val doc = asDocument(createView)
      val expectedFullTitle = getFullTitle(s"$messageKeyPrefix.title", frontendAppConfig.earliestTaxYear)
      assertEqualsMessage(doc, "title", expectedFullTitle)
    }

    "display the correct heading" in {
      val doc = asDocument(createView)
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", frontendAppConfig.earliestTaxYear)
    }
  }

  application.stop
}
