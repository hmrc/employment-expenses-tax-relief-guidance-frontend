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

package models

import utils.{Enumerable, Message, RadioOption, WithName}

sealed trait WhichYearsAreYouClaimingFor

object WhichYearsAreYouClaimingFor extends Enumerable.Implicits {
  case object CurrentYear extends WithName("current.tax.year.text") with WhichYearsAreYouClaimingFor
  case object PreviousYear extends WithName("previous.tax.year.text") with WhichYearsAreYouClaimingFor
  case object BothYear extends WithName("both.tax.years.text") with WhichYearsAreYouClaimingFor

  private val keyPrefix = "whichYearsAreYouClaimingFor"

  val values: Seq[WhichYearsAreYouClaimingFor] = Seq(
    CurrentYear, PreviousYear, BothYear
  )

  val actVal = Map("current.tax.year.text" -> "1", "previous.tax.year.text" -> "2", "both.tax.years.text" -> "3")
  val options: Seq[RadioOption] = values.map {
    value => RadioOption(id = s"$keyPrefix.$value",
      value = s"${actVal.get(value.toString).get}",
      message = Message(s"$keyPrefix.$value"))
  }

  implicit val enumerable: Enumerable[WhichYearsAreYouClaimingFor] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
