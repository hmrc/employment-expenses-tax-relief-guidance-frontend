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

package views

import models.ClaimingFor.{BuyingEquipment, FeesSubscriptions, HomeWorking, MileageFuel, Other, TravelExpenses, UniformsClothingTools}
import views.behaviours.NewViewBehaviours
import views.html.UsePrintAndPostDetailedView

class UsePrintAndPostDetailedViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "usePrintAndPostDetailed"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[UsePrintAndPostDetailedView]

  val claimingListFor =  List(
    HomeWorking, UniformsClothingTools, MileageFuel, TravelExpenses, FeesSubscriptions, BuyingEquipment, Other
  )

  def createView = view.apply(claimingListFor)(fakeRequest, messages)

  "UsePrintAndPost view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)
  }

  application.stop()
}
