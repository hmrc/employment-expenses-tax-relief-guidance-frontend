/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.helpers

import models.ClaimingFor
import models.ClaimingFor.{HomeWorking, MileageFuel}
import utils.UserAnswers

class ClaimingForListBuilder {

  def buildClaimingForList(userAnswers: UserAnswers): List[ClaimingFor] = {
    def onlyWfhClaim = userAnswers.claimAnyOtherExpense.getOrElse(false)

    if (onlyWfhClaim) {
      List(HomeWorking)
    } else {
      buildListFromUserAnswers(userAnswers)
    }
  }

  private def buildListFromUserAnswers(userAnswers: UserAnswers): List[ClaimingFor] = {
    def containsMileageFuel = userAnswers.claimingFor.exists(_.contains(MileageFuel))
    def isClaimingMileage   = userAnswers.claimingMileage.getOrElse(false)
    def isClaimingFuel      = userAnswers.claimingFuel.getOrElse(false)

    val claimingForList = userAnswers.claimingFor.getOrElse(Nil)

    if (containsMileageFuel && (isClaimingMileage || isClaimingFuel)) {
      claimingForList
    } else {
      claimingForList.filterNot(_ == MileageFuel)
    }
  }

}
