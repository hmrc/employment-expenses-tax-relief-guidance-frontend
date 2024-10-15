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

import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem

trait CheckboxViewBehaviours[A] extends NewViewBehaviours {

  def assertContainsCheckBox(doc: Document, id: String, name: String, value: String, isChecked: Boolean): Assertion = {
    assertRenderedById(doc, id)
    val checkbox = doc.getElementById(id)
    assert(checkbox.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(checkbox.attr("value") == value, s"\n\nElement $id does not have value $value")
    if (isChecked) {
      assert(checkbox.hasAttr("checked"), s"\n\nElement $id is not checked")
    } else {
      assert(!checkbox.hasAttr("checked"), s"\n\nElement $id is checked")
    }
  }

  def checkboxPage(form: Form[Set[A]],
                   createView: Form[Set[A]] => HtmlFormat.Appendable,
                   messageKeyPrefix: String,
                   options: Seq[CheckboxItem],
                   fieldKey: String = "value",
                   legend: Option[String] = None): Unit = {

    "behave like a checkbox page" must {
      "contain a h1 for the question" in {
        val doc = asDocument(createView(form))
        val legends = doc.getElementsByTag("h1")
        legends.size mustBe 1
        legends.text contains legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
      }

      "contain an input for the value" in {
        val doc = asDocument(createView(form))
        for (option <- options) {
          assertRenderedById(doc, option.id.get)
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(createView(form))
        for (option <- options) {
          doc.select(s"label[for=${option.id.get}]").text mustEqual option.content.asHtml.toString()
        }
      }

      "rendered" must {

        "contain checkboxes for the values" in {
          val doc = asDocument(createView(form))
          for (option <- options) {
            assertContainsRadioButton(doc, option.id.get, option.name.get, option.value, isChecked = false)
          }
        }
      }

      for (option <- options) {

        s"rendered with a value of '${option.value}'" must {

          s"have the '${option.value}' checkbox selected" in {
            val formWithData = form.bind(Map("value" -> s"${option.value}"))
            val doc = asDocument(createView(formWithData))
            assertContainsCheckBox(doc, option.id.get, option.name.get, option.value, isChecked = false)
          }
        }
      }

      "rendered with all values" must {

        val valuesMap: Map[String, String] = options.zipWithIndex.map {
          case (option, i) => s"value[$i]" -> option.value
        }.toMap

        val formWithData = form.bind(valuesMap)
        val doc = asDocument(createView(formWithData))

        for(option <- options) {
          s"have ${option.value} value selected" in {
            assertContainsCheckBox(doc, option.id.get, option.name.get, option.value, isChecked = false)
          }
        }
      }

      "not render an error summary" in {
        val doc = asDocument(createView(form))
        assertNotRenderedById(doc, "govuk-error-summary__title")
      }


      "show error in the title" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        doc.title.contains(messages("error.browser.title.prefix")) mustBe true
      }

      "show an error summary" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        assertRenderedByClass(doc, "govuk-error-summary__title")
      }

      "show an error associated with the value field" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        val errorSpan = doc.getElementsByClass("govuk-error-message").first
        errorSpan.text mustBe (messages("error.browser.title.prefix") + " " + messages("error.invalid"))
        doc.getElementsByTag("div").first.attr("aria-describedby") contains errorSpan.attr("id")
      }
    }
  }
}