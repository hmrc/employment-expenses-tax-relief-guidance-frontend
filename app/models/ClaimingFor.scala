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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import utils.{Enumerable, WithName}
import views.html.playComponents.link_start

sealed trait ClaimingFor

object ClaimingFor {

  case object HomeWorking extends WithName("homeWorking") with ClaimingFor
  case object UniformsClothingTools extends WithName("uniformsClothingTools") with ClaimingFor
  case object MileageFuel extends WithName("mileageFuel") with ClaimingFor
  case object TravelExpenses extends WithName("travelExpenses") with ClaimingFor
  case object FeesSubscriptions extends WithName("feesSubscriptions") with ClaimingFor
  case object BuyingEquipment extends WithName("buyingEquipment") with ClaimingFor
  case object Other extends WithName("other") with ClaimingFor

  val values: List[ClaimingFor] = List(
    HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses, FeesSubscriptions, BuyingEquipment, Other
  )

  def options(onlineJourneyShutterEnabled: Boolean, freOnlyJourneyEnabled: Boolean)(implicit messages: Messages): List[CheckboxItem] = {

    val feesOption = {
      val href = "https://www.gov.uk/government/publications/professional-bodies-approved-for-tax-relief-list-3"
      val eventBody = s"""${messages(s"claimingFor.title")}:${messages(s"claimingFor.$FeesSubscriptions")}"""
      val start = link_start(href, eventBody, newWindow = true)
      val end = "</a>"

      new CheckboxItem(
        name = Some("value[4]"),
        id = Some(s"claimingFor.$FeesSubscriptions"),
        value = FeesSubscriptions.toString,
        content = Text(messages(s"claimingFor.$FeesSubscriptions")),
        hint = Some(Hint(
          content = HtmlContent(messages(s"claimingFor.$FeesSubscriptions.description", start, end)))
        )
      )
    }

    val freOption = {
      val href = "https://www.gov.uk/guidance/job-expenses-for-uniforms-work-clothing-and-tools"
      val eventBody = s"""${messages(s"claimingFor.title")}:${messages(s"claimingFor.$UniformsClothingTools")}"""
      val start = link_start(href, eventBody, newWindow = true)
      val end = "</a>"

      new CheckboxItem(
              name = Some("value[1]"),
              id = Some(s"claimingFor.$UniformsClothingTools"),
              value = UniformsClothingTools.toString,
              content = if(freOnlyJourneyEnabled) {Text(messages(s"claimingFor.$UniformsClothingTools"))} else Text(messages(s"claimingFor.$UniformsClothingTools._old")),
              hint = Some(Hint(
              content = if(freOnlyJourneyEnabled) HtmlContent(messages(s"claimingFor.$UniformsClothingTools.description" , start, end)) else if(onlineJourneyShutterEnabled) HtmlContent(messages(s"claimingFor.$UniformsClothingTools.description_old")) else HtmlContent(messages(s"claimingFor.$UniformsClothingTools.oldDescription"))

                ))
            )
    }

    List(
      new CheckboxItem(
        name = Some("value[0]"),
        id = Some(s"claimingFor.$HomeWorking"),
        value = HomeWorking.toString,
        content = Text(messages(s"claimingFor.$HomeWorking")),
        hint = Some(Hint(
          content = if(onlineJourneyShutterEnabled) HtmlContent(messages(s"claimingFor.$HomeWorking.description")) else HtmlContent(messages(s"claimingFor.$HomeWorking.oldDescription"))
        ))
      ),
      freOption
      ,
      new CheckboxItem(
        name = Some("value[2]"),
        id = Some(s"claimingFor.$MileageFuel"),
        value = MileageFuel.toString,
        content = Text(messages(s"claimingFor.$MileageFuel")),
        hint = Some(Hint(
          content = HtmlContent(messages(s"claimingFor.$MileageFuel.description")))
        )
      ),
      new CheckboxItem(
        name = Some("value[3]"),
        id = Some(s"claimingFor.$TravelExpenses"),
        value = TravelExpenses.toString,
        content = Text(messages(s"claimingFor.$TravelExpenses")),
        hint = Some(Hint(
          content = HtmlContent(messages(s"claimingFor.$TravelExpenses.description")))
        )
      ),
      feesOption,
      new CheckboxItem(
        name = Some("value[5]"),
        id = Some(s"claimingFor.$BuyingEquipment"),
        value = BuyingEquipment.toString,
        content = Text(messages(s"claimingFor.$BuyingEquipment")),
        hint = Some(Hint(
          content = HtmlContent(messages(s"claimingFor.$BuyingEquipment.description")))
        )
      ),
      new CheckboxItem(
        name = Some("value[6]"),
        id = Some(s"claimingFor.$Other"),
        value = Other.toString,
        content = Text(messages(s"claimingFor.$Other"))
      )
    )
  }

  implicit val enumerable: Enumerable[ClaimingFor] =
    Enumerable(values.map(v => v.toString -> v): _*)

  val mappings: Map[String, ClaimingFor] = Map(
    HomeWorking.toString           -> HomeWorking,
    UniformsClothingTools.toString -> UniformsClothingTools,
    MileageFuel.toString           -> MileageFuel,
    TravelExpenses.toString        -> TravelExpenses,
    FeesSubscriptions.toString     -> FeesSubscriptions,
    BuyingEquipment.toString       -> BuyingEquipment,
    Other.toString                 -> Other
  )
}
