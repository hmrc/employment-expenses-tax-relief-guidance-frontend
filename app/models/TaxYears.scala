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

sealed trait TaxYears

object TaxYears {

  case object ThisYear extends WithName("thisYear") with TaxYears
  case object LastYear extends WithName("lastYear") with TaxYears
  case object TwoYearsAgo extends WithName("twoYearsAgo") with TaxYears
  case object ThreeYearsAgo extends WithName("threeYearsAgo") with TaxYears
  case object FourYearsAgo extends WithName("fourYearsAgo") with TaxYears
  case object AnotherYear extends WithName("anotherYear") with TaxYears

  val values: List[TaxYears] = List(
    ThisYear, LastYear, TwoYearsAgo, ThreeYearsAgo, FourYearsAgo, AnotherYear
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("taxYears", value.toString)
  }

  val mappings: Map[String, TaxYears] = Map(
    ThisYear.toString      -> ThisYear,
    LastYear.toString      -> LastYear,
    TwoYearsAgo.toString   -> TwoYearsAgo,
    ThreeYearsAgo.toString -> ThreeYearsAgo,
    FourYearsAgo.toString  -> FourYearsAgo,
    AnotherYear.toString   -> AnotherYear
  )

  implicit val enumerable: Enumerable[TaxYears] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
