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

package utils

import models.ClaimingFor.HomeWorking
import models.EmployerPaid

class NavigatorHelper {

  def isClaimingWfh(userAnswers: UserAnswers): Boolean = {
    def isClaimingForHomeWorking: Boolean = userAnswers.claimingFor.exists(_.contains(HomeWorking))

    def isClaimingAnyOtherExpense: Boolean =
      (userAnswers.claimingFor.isEmpty || userAnswers.claimingFor.exists(_.isEmpty)) &&
        userAnswers.claimAnyOtherExpense.contains(true)

    isClaimingForHomeWorking || isClaimingAnyOtherExpense
  }

  def vehiclesRedirect(userAnswers: UserAnswers): Boolean = {
    def isClaimingMileage: Boolean = userAnswers.claimingMileage.contains(true)

    def didEmployerPayBackSomeExpenses: Boolean =
      userAnswers.employerPaidBackAnyExpenses.contains(EmployerPaid.SomeExpenses)

    def isNotClaimingFuelOrUsingCompanyCar: Boolean =
      userAnswers.claimingFuel.contains(false) || userAnswers.useCompanyCar.contains(false)

    isClaimingMileage && didEmployerPayBackSomeExpenses && isNotClaimingFuelOrUsingCompanyCar
  }

}
