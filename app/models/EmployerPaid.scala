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

import utils.{Enumerable, RadioOption, WithName}

sealed trait EmployerPaid

object EmployerPaid {

  case object NoExpenses extends WithName("noExpenses") with EmployerPaid
  case object SomeExpenses extends WithName("someExpenses") with EmployerPaid
  case object AllExpenses extends WithName("allExpenses") with EmployerPaid

  val values: Set[EmployerPaid] = Set(
    NoExpenses, SomeExpenses, AllExpenses
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("employerPaid", value.toString)
  }

  implicit val enumerable: Enumerable[EmployerPaid] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
