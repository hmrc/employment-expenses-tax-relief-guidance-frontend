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

import models.Claimant.You
import views.behaviours.{NewViewBehaviours, ViewBehaviours}
import views.html.CannotClaimMileageCostsView

class CannotClaimMileageCostsViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "cannotClaimMileageCosts.you"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[CannotClaimMileageCostsView]

  def createView = view.apply(You)(fakeRequest, messages)

  val buisnessMileageFuelCostsUrl = frontendAppConfig.buisnessMileageFuelCostsUrl

  "CannotClaimMileageCosts view" must {
    behave like normalPage(createView, messageKeyPrefix)
    behave like pageWithBackLink(createView)
    behave like pageWithHyperLink(createView, buisnessMileageFuelCostsUrl)
  }

  application.stop
}
