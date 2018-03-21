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

sealed trait ClaimingFor

object ClaimingFor {

  case object UniformsClothingTools extends WithName("uniformsClothingTools") with ClaimingFor
  case object MileageFuel extends WithName("mileageFuel") with ClaimingFor
  case object TravelExpenses extends WithName("travelExpenses") with ClaimingFor
  case object FeesSubscriptions extends WithName("feesSubscriptions") with ClaimingFor
  case object HomeWorking extends WithName("homeWorking") with ClaimingFor
  case object BuyingEquipment extends WithName("buyingEquipment") with ClaimingFor
  case object Other extends WithName("other") with ClaimingFor

  val values: List[ClaimingFor] = List(
    UniformsClothingTools, MileageFuel, TravelExpenses, FeesSubscriptions, HomeWorking, BuyingEquipment, Other
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("claimingFor", value.toString)
  }

  implicit val enumerable: Enumerable[ClaimingFor] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  val mappings: Map[String, ClaimingFor] = Map(
    UniformsClothingTools.toString -> UniformsClothingTools,
    MileageFuel.toString           -> MileageFuel,
    TravelExpenses.toString        -> TravelExpenses,
    FeesSubscriptions.toString     -> FeesSubscriptions,
    HomeWorking.toString           -> HomeWorking,
    BuyingEquipment.toString       -> BuyingEquipment,
    Other.toString                 -> Other
  )
}
