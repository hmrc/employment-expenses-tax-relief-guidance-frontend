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

package models

import utils.{Enumerable, Message, RadioOption, WithName}

sealed trait ClaimAnyOtherExpense

object ClaimAnyOtherExpense extends Enumerable.Implicits {
  case object YesClaimAnyOtherExpense extends WithName("yesClaimAnyOtherExpense") with ClaimAnyOtherExpense
  case object NoClaimAnyOtherExpense  extends WithName("noClaimAnyOtherExpense") with ClaimAnyOtherExpense

  private val keyPrefix = "claimAnyOtherExpense"

  val values: Seq[ClaimAnyOtherExpense] = Seq(
    YesClaimAnyOtherExpense,
    NoClaimAnyOtherExpense
  )

  val options: Seq[RadioOption] = values.map { value =>
    RadioOption(
      id = s"$keyPrefix.$value",
      value = s"${value == YesClaimAnyOtherExpense}",
      message = Message(s"$keyPrefix.$value")
    )
  }

  implicit val enumerable: Enumerable[ClaimAnyOtherExpense] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
