/*
 * Copyright 2018 HM Revenue & Customs
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

import utils.{Enumerable, RadioOption, WithName}
import uk.gov.hmrc.time.TaxYear

sealed trait ClaimYears

object ClaimYears {

  case object ThisYear extends WithName("thisYear") with ClaimYears
  case object LastYear extends WithName("lastYear") with ClaimYears
  case object TwoYearsAgo extends WithName("twoYearsAgo") with ClaimYears
  case object ThreeYearsAgo extends WithName("threeYearsAgo") with ClaimYears
  case object FourYearsAgo extends WithName("fourYearsAgo") with ClaimYears
  case object AnotherYear extends WithName("anotherYear") with ClaimYears

  val values: List[ClaimYears] = List(
    ThisYear, LastYear, TwoYearsAgo, ThreeYearsAgo, FourYearsAgo, AnotherYear
  )

  def options: List[RadioOption] = List(
    taxYearRadioOption(TaxYear.current, ThisYear),
    taxYearRadioOption(TaxYear.current.back(1), LastYear),
    taxYearRadioOption(TaxYear.current.back(2), TwoYearsAgo),
    taxYearRadioOption(TaxYear.current.back(3), ThreeYearsAgo),
    taxYearRadioOption(TaxYear.current.back(4), FourYearsAgo),
    new RadioOption(s"taxYears.$AnotherYear", AnotherYear.toString, "taxYears.anotherYear")
  )

  def taxYearRadioOption(taxYear: TaxYear, option: ClaimYears) =
    new RadioOption(
      s"taxYears.$option",
      option.toString,
      "taxYears.specificYear",
      taxYear.startYear.toString,
      taxYear.finishYear.toString
    )

  val mappings: Map[String, ClaimYears] = Map(
    ThisYear.toString      -> ThisYear,
    LastYear.toString      -> LastYear,
    TwoYearsAgo.toString   -> TwoYearsAgo,
    ThreeYearsAgo.toString -> ThreeYearsAgo,
    FourYearsAgo.toString  -> FourYearsAgo,
    AnotherYear.toString   -> AnotherYear
  )

  implicit val enumerable: Enumerable[ClaimYears] =
    Enumerable(values.map(v => v.toString -> v): _*)

  def getTaxYear(year: ClaimYears): TaxYear = year match {
    case ThisYear      => TaxYear.current
    case LastYear      => TaxYear.current.back(1)
    case TwoYearsAgo   => TaxYear.current.back(2)
    case ThreeYearsAgo => TaxYear.current.back(3)
    case FourYearsAgo  => TaxYear.current.back(4)
    case AnotherYear   => throw new IllegalArgumentException("Cannot get an exact tax year for Another Year")
  }
}
