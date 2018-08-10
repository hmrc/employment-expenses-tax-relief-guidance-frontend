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

import play.api.i18n.Messages
import utils.{Enumerable, Message, RadioOption, WithName}
import views.html.components.link_start

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

  def options(claimant: Claimant)(implicit messages: Messages): List[RadioOption] = {

    val feesOption = {

      val href = "https://www.gov.uk/government/publications/professional-bodies-approved-for-tax-relief-list-3"
      val eventBody = s"""${messages(s"claimingFor.$claimant.title")}:${messages(s"claimingFor.$FeesSubscriptions")}"""
      val start = link_start(href, eventBody)
      val end = "</a>"

      new RadioOption(
        s"claimingFor.$FeesSubscriptions",
        FeesSubscriptions.toString,
        Message(s"claimingFor.$FeesSubscriptions"),
        Some(Message(s"claimingFor.$FeesSubscriptions.$claimant.description", start, end))
      )
    }

    List(
      new RadioOption(
        s"claimingFor.$UniformsClothingTools",
        UniformsClothingTools.toString,
        Message(s"claimingFor.$UniformsClothingTools"),
        Some(Message(s"claimingFor.$UniformsClothingTools.$claimant.description"))
      ),
      new RadioOption(
        s"claimingFor.$MileageFuel",
        MileageFuel.toString,
        Message(s"claimingFor.$claimant.$MileageFuel"),
        Some(Message(s"claimingFor.$MileageFuel.$claimant.description"))
      ),
      new RadioOption(
        s"claimingFor.$TravelExpenses",
        TravelExpenses.toString,
        Message(s"claimingFor.$TravelExpenses"),
        Some(Message(s"claimingFor.$TravelExpenses.$claimant.description"))
      ),
      feesOption,
      new RadioOption(
        s"claimingFor.$HomeWorking",
        HomeWorking.toString,
        Message(s"claimingFor.$HomeWorking"),
        Some(Message(s"claimingFor.$HomeWorking.$claimant.description"))
      ),
      new RadioOption(
        s"claimingFor.$BuyingEquipment",
        BuyingEquipment.toString,
        Message(s"claimingFor.$BuyingEquipment"),
        Some(Message(s"claimingFor.$BuyingEquipment.$claimant.description"))
      ),
      new RadioOption(
        s"claimingFor.$Other",
        Other.toString,
        Message(s"claimingFor.$Other")
      )
    )
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
