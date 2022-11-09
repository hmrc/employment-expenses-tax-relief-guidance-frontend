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

package forms.mappings

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.validation.{Invalid, Valid}

class ConstraintsSpec extends AnyWordSpec with Matchers with Constraints {


  "firstError" must {

    "return Valid when all constraints pass" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("foo")
      result mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }

    "return Invalid when the second constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.regexp", """^\w+$""")
    }

    "return Invalid for the first error when both constraints fail" in {
      val result = firstError(maxLength(-1, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.length", -1)
    }
  }

  "minimumValue" must {

    "return Valid for a number greater than the threshold" in {
      val result = minimumValue(1, "error.min").apply(2)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = minimumValue(1, "error.min").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      val result = minimumValue(1, "error.min").apply(0)
      result mustEqual Invalid("error.min", 1)
    }
  }

  "maximumValue" must {

    "return Valid for a number less than the threshold" in {
      val result = maximumValue(1, "error.max").apply(0)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = maximumValue(1, "error.max").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      val result = maximumValue(1, "error.max").apply(2)
      result mustEqual Invalid("error.max", 1)
    }
  }

  "inRange" must {

    "return Valid for a number equal to the bottom threshold" in {
      val result = inRange(1, 10, "error.outOfRange").apply(1)
      result mustEqual Valid
    }

    "return Valid for a number equal to the upper threshold" in {
      val result = inRange(1, 10, "error.outOfRange").apply(10)
      result mustEqual Valid
    }

    "return Valid for a number within range" in {
      val result = inRange(1, 10, "error.outOfRange").apply(5)
      result mustEqual Valid
    }

    "return Invalid for a number below the bottom threshold" in {
      val result = inRange(1, 10, "error.outOfRange").apply(0)
      result mustEqual Invalid("error.outOfRange", 1, 10)
    }

    "return Invalid for a number above the upper threshold" in {
      val result = inRange(1, 10, "error.outOfRange").apply(11)
      result mustEqual Invalid("error.outOfRange", 1, 10)
    }
  }

  "regexp" must {

    "return Valid for an input that matches the expression" in {
      val result = regexp("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "return Invalid for an input that does not match the expression" in {
      val result = regexp("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid", """^\d+$""")
    }
  }

  "maxLength" must {

    "return Valid for a string shorter than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 9)
      result mustEqual Valid
    }

    "return Valid for an empty string" in {
      val result = maxLength(10, "error.length")("")
      result mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 10)
      result mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }
  }
}
