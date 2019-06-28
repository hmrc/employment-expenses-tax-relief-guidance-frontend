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

import models.Claimant.You
import models.ClaimYears
import models.ClaimYears.FourYearsAgo
import uk.gov.hmrc.time.TaxYear
import views.behaviours.ViewBehaviours
import views.html.cannotClaimReliefTooLongAgo

class CannotClaimReliefTooLongAgoViewSpec extends ViewBehaviours {

  val claimant = You

  val fourYearsAgo = ClaimYears.getTaxYear(FourYearsAgo)
  val startYearFouryearsAgo = fourYearsAgo.startYear.toString
  val endYear = TaxYear.current.finishYear.toString
  val startYear = TaxYear.current.startYear.toString
  val messageKeyPrefix = s"cannotClaimReliefTooLongAgo.$claimant"

  def createView = () => cannotClaimReliefTooLongAgo(frontendAppConfig, claimant,startYearFouryearsAgo, startYear, endYear)(fakeRequest, messages)

  "CannotClaimReliefTooLongAgo view" must {

    behave like pageWithBackLink(createView)

    "have the correct banner title" in {
      val doc = asDocument(createView())
      val nav = doc.getElementById("proposition-menu")
      val span = nav.children.first
      span.text mustBe messagesApi("site.service_name")
    }

    "display the correct browser title" in {
      val doc = asDocument(createView())
      val expectedFullTitle = getFullTitle(s"$messageKeyPrefix.title", startYearFouryearsAgo)
      assertEqualsMessage(doc, "title", expectedFullTitle)
    }

    "display the correct heading" in {
      val doc = asDocument(createView())
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", startYearFouryearsAgo)
    }
  }
}
