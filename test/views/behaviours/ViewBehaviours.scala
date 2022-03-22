/*
 * Copyright 2022 HM Revenue & Customs
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

package views.behaviours

import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  protected def getFullTitle(messageKey: String, args: Any*) =
    messages(messageKey, args: _*) + " – " + messages("site.service_name") + " – " + messages("site.gov.uk")

  def normalPage(view: HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 expectedGuidanceKeys: String*) = {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view)
          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first

          span.text mustEqual messages("site.service_name")
        }

        "display the correct browser title" in {
          val doc = asDocument(view)
          val expectedFullTitle = getFullTitle(s"$messageKeyPrefix.title")
          assertEqualsMessage(doc, "title", expectedFullTitle)
        }

        "display the correct heading" in {
          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {
          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view)
          assertRenderedByCssSelector(doc, "p.translate")
        }
      }
    }
  }


  def pageWithOverrideBackLink(view: HtmlFormat.Appendable) = {

    "behave like a page with a override back link" must {
      "have a back link" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "back-link-override")
      }
    }
  }

  def pageWithBackLink(view: HtmlFormat.Appendable) = {

    "behave like a page with a back link" must {
      "have a back link" in {
        val doc = asDocument(view)
        assertRenderedById(doc, "back-link")
      }
    }
  }

  def pageWithBodyText(view: HtmlFormat.Appendable, messageKey: String*): Unit = {

    "behave like a page with body text" must {

      "display content" in {
        val doc = asDocument(view)
        for (key <- messageKey) {
          assertContainsMessages(doc, key)
        }
      }
    }
  }

  def pageWithHyperLink(view: HtmlFormat.Appendable,
                        url: String,
                        id: String = "link"): Unit = {

    "behave like a page with a url link" must {
      "display link" in {
        val doc = asDocument(view)
        doc.getElementById(id).attr("href") mustBe url
      }
    }
  }
}
