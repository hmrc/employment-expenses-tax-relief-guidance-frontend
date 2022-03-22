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

package utils

import org.scalatest.{MustMatchers, WordSpec}
import play.api.data.Form

class RichFieldSpec extends WordSpec with MustMatchers {

  val multiForm: Form[Set[String]] = {

    import play.api.data.Forms._
    import play.api.data._

    Form("value" -> set(text))
  }

  val singleForm: Form[String] = {

    import play.api.data.Forms._
    import play.api.data._

    Form("value" -> text)
  }

  ".values" must {

    "return an empty set when a multi-value field has no value" in {
      val field = multiForm("value")
      field.values must be(empty)
    }

    "return an empty set when a single-value field has no value" in {
      val field = singleForm("value")
      field.values must be(empty)
    }

    "return multiple values when the field is a multi-value field" in {
      val field = multiForm.fill(Set("foo", "bar"))("value")
      field.values mustEqual Seq("foo", "bar")
    }

    "return a single value when the field a single-value field" in {
      val field = singleForm.fill("foo")("value")
      field.values mustEqual Seq("foo")
    }
  }
}
