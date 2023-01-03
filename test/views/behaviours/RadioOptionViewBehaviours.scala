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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait RadioOptionViewBehaviours extends NewQuestionViewBehaviours[Int] {
  val numberOfOptions: Int

  def radioOptionPage(createView: (Form[Int]) => HtmlFormat.Appendable,
                messageKeyPrefix: String,
                headingArgs: Any*) = {

    "behave like a page with a radio options question" when {
      "rendered" must {
        "contain a legend for the question" in {
          val doc = asDocument(createView(form))
          val legends = doc.getElementsByTag("legend")
          legends.size mustBe 1
          legends.first.text contains messages(s"$messageKeyPrefix.heading", headingArgs:_*)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          for(i <- 1 to numberOfOptions) {
            assertRenderedById(doc, answerId(i))
          }
        }

        "have no values checked when rendered with no form" in {
          val doc = asDocument(createView(form))
          for(i <- 1 to numberOfOptions) {
            assert(!doc.getElementById(answerId(i)).hasAttr("checked"))
          }
        }

        "not render an error summary" in {
          val doc = asDocument(createView(form))
          assertNotRenderedById(doc, "error-summary_header")
        }
      }

      "render with a value" must {
        behave like answeredRadioOptionPage(createView)
      }

      "render with an error" must {
        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedByCssSelector(doc, ".govuk-error-summary")
        }

        "show an error in the value field's label" in {
          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementById("value-error")
          errorSpan.text mustBe s"Error: ${messages(errorMessage)}"
        }
      }
    }
  }


  def answeredRadioOptionPage(createView: Form[Int] => HtmlFormat.Appendable) = {
    for(answer <- 1 to numberOfOptions){
      s"have only the correct value ($answer) checked" in {
        val doc = asDocument(createView(form.fill(answer)))
        assert(doc.getElementById(answerId(answer)).hasAttr("checked"))
        for(notAnswer <- 1 to numberOfOptions){
          if(notAnswer != answer){
            assert(!doc.getElementById(answerId(notAnswer)).hasAttr("checked"))
          }
        }
      }
      s"not render an error summary for value ($answer)" in {
        val doc = asDocument(createView(form.fill(answer)))
        assertNotRenderedById(doc, "error-summary_header")
      }
    }
  }

  private def answerId(answer: Int): String = {
    s"value${if(answer != 1) s"-$answer" else ""}"
  }
}
