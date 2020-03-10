/*
 * Copyright 2020 HM Revenue & Customs
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
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.AccessibilityStatementView

class AccessibilityStatementViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "accessibilityStatement"

  "AccessibilityStatement view" must {
    val serviceUrl = "url"
    val subDomain = "domain"

    val introductionLink = s"""<a href="${frontendAppConfig.accessibilityStatementUrl}">${messages("accessibilityStatement.introduction.paragraph2.linkText")}</a>"""
    val serviceLink = s"""<a href="$serviceUrl">$subDomain</a>"""
    val usingThisServiceLink = s"""<a href="${frontendAppConfig.accessibilityStatementUrl}">${messages("accessibilityStatement.usingThisService.paragraph3.linkText")}</a>"""
    val howAccessibleThisServiceIsLink = s"""<a href="${frontendAppConfig.w3StandardsUrl}">${messages("accessibilityStatement.howAccessibleThisServiceIs.paragraph1.linkText")}</a>"""
    val reportingAccessibilityProblemsWithThisServiceLink = s"""<a href="${frontendAppConfig.contactAccessibilityUrl}">${messages("accessibilityStatement.reportingAccessibilityProblemsWithThisService.paragraph1.linkText")}</a>"""
    val whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaintLink1 = s"""<a href="${frontendAppConfig.equalityAdvisoryServiceUrl}">${messages("accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.paragraph1.linkText1")}</a>"""
    val whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaintLink2 = s"""<a href="${frontendAppConfig.equalityNIUrl}">${messages("accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.paragraph1.linkText2")}</a>"""
    val contactUsLink = s"""<a href="${frontendAppConfig.dealingHmrcAdditionalNeedsUrl}">${messages("accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph3.linkText")}</a>"""
    val technicalInformationLink = s"""<a href="${frontendAppConfig.w3StandardsUrl}">${messages("accessibilityStatement.technicalInformationAboutThisServicesAccessibility.paragraph2.linkText")}</a>"""
    val dacLink = s"""<a href="${frontendAppConfig.dacUrl}">${messages("accessibilityStatement.howWeTestedThisService.paragraph2.linkText")}</a>"""

    val application: Application = applicationBuilder().build
    val view: AccessibilityStatementView = application.injector.instanceOf[AccessibilityStatementView]
    val applyView: HtmlFormat.Appendable = view.apply(serviceUrl, subDomain)(fakeRequest, messages)

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(applyView)
          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first

          span.text mustEqual messages("site.service_name")
        }

        "display the correct browser title" in {
          val doc = asDocument(applyView)
          val expectedFullTitle = getFullTitle(s"$messageKeyPrefix.title", messages("site.service_name"))
          assertEqualsMessage(doc, "title", expectedFullTitle)
        }

        "display the correct heading" in {
          val doc = asDocument(applyView)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", messages("site.service_name"))
        }

        "display language toggles" in {
          val doc = asDocument(applyView)
          assertRenderedByCssSelector(doc, "p.translate")
        }
      }
    }

    behave like pageWithBodyText(applyView,
      messages("accessibilityStatement.title", messages("site.service_name")),
      messages("accessibilityStatement.heading", messages("site.service_name")),
      "accessibilityStatement.introduction.paragraph1",
      messages("accessibilityStatement.introduction.paragraph2", introductionLink),
      messages("accessibilityStatement.introduction.paragraph3", messages("site.service_name"), serviceLink),
      "accessibilityStatement.usingThisService.heading",
      "accessibilityStatement.usingThisService.aboutTheService",
      "accessibilityStatement.usingThisService.paragraph1",
      "accessibilityStatement.usingThisService.listItem1",
      "accessibilityStatement.usingThisService.listItem2",
      "accessibilityStatement.usingThisService.listItem3",
      "accessibilityStatement.usingThisService.listItem4",
      "accessibilityStatement.usingThisService.listItem5",
      "accessibilityStatement.usingThisService.paragraph2",
      messages("accessibilityStatement.usingThisService.paragraph3", usingThisServiceLink),
      "accessibilityStatement.howAccessibleThisServiceIs.heading",
      messages("accessibilityStatement.howAccessibleThisServiceIs.paragraph1", howAccessibleThisServiceIsLink),
      "accessibilityStatement.whatToDoIfYouHaveDifficultyUsingThisService.heading",
      "accessibilityStatement.reportingAccessibilityProblemsWithThisService.heading",
      messages("accessibilityStatement.reportingAccessibilityProblemsWithThisService.paragraph1", reportingAccessibilityProblemsWithThisServiceLink),
      "accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.heading",
      messages("accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.paragraph1", whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaintLink1, whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaintLink2),
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.heading",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph1",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph2",
      messages("accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph3", contactUsLink),
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.heading",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.paragraph1",
      messages("accessibilityStatement.technicalInformationAboutThisServicesAccessibility.compliant", howAccessibleThisServiceIsLink),
      "accessibilityStatement.howWeTestedThisService.heading",
      messages("accessibilityStatement.howWeTestedThisService.paragraph1", frontendAppConfig.accessibilityStatementLastTested),
      messages("accessibilityStatement.howWeTestedThisService.paragraph2", dacLink),
      messages("accessibilityStatement.howWeTestedThisService.paragraph3", frontendAppConfig.accessibilityStatementLastTested, frontendAppConfig.accessibilityStatementFirstPublished)
    )

    application.stop

    behave like pageWithBackLink(applyView)
  }
}
