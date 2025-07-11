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

import utils.{Enumerable, RadioOption, WithName}

sealed trait ClaimingForMoreThanOneJob

object ClaimingForMoreThanOneJob extends Enumerable.Implicits {

  case object OneJob         extends WithName("oneJob") with ClaimingForMoreThanOneJob
  case object MoreThanOneJob extends WithName("moreThanOneJob") with ClaimingForMoreThanOneJob

  val values: Seq[ClaimingForMoreThanOneJob] = Seq(
    OneJob,
    MoreThanOneJob
  )

  val options: Seq[RadioOption] =
    values.map(value => RadioOption("claimingForMoreThanOneJob", value.toString))

  implicit val enumerable: Enumerable[ClaimingForMoreThanOneJob] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
