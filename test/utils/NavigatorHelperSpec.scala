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

import models.ClaimingFor
import models.EmployerPaid.{AllExpenses, NoExpenses, SomeExpenses}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class NavigatorHelperSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  private val userAnswers = mock[UserAnswers]

  private val navigatorHelper = new NavigatorHelper

  override def beforeEach(): Unit =
    reset(userAnswers)

  "NavigatorHelper on isClaimingWfh" must {

    "return true" when {

      "userAnswers.claimingFor contains HomeWorking" in {
        when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))

        navigatorHelper.isClaimingWfh(userAnswers) mustBe true
      }

      "userAnswers.claimingFor is an empty List and userAnswers.claimAnyOtherExpense is true" in {
        when(userAnswers.claimingFor).thenReturn(Some(List.empty))
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(true))

        navigatorHelper.isClaimingWfh(userAnswers) mustBe true
      }

      "userAnswers.claimingFor is empty and userAnswers.claimAnyOtherExpense is true" in {
        when(userAnswers.claimingFor).thenReturn(None)
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(true))

        navigatorHelper.isClaimingWfh(userAnswers) mustBe true
      }
    }

    "return false" when {

      "userAnswers.claimingFor does NOT contain HomeWorking" in {
        val claimingForList = ClaimingFor.values.filterNot(_ == ClaimingFor.HomeWorking)
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))

        navigatorHelper.isClaimingWfh(userAnswers) mustBe false
      }

      "userAnswers.claimingFor is an empty List and userAnswers.claimAnyOtherExpense is false" in {
        when(userAnswers.claimingFor).thenReturn(Some(List.empty))
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))

        navigatorHelper.isClaimingWfh(userAnswers) mustBe false
      }

      "userAnswers.claimingFor is empty and userAnswers.claimAnyOtherExpense is false" in {
        when(userAnswers.claimingFor).thenReturn(None)
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))

        navigatorHelper.isClaimingWfh(userAnswers) mustBe false
      }

      "userAnswers.claimingFor is an empty List and userAnswers.claimAnyOtherExpense is empty" in {
        when(userAnswers.claimingFor).thenReturn(Some(List.empty))
        when(userAnswers.claimAnyOtherExpense).thenReturn(None)

        navigatorHelper.isClaimingWfh(userAnswers) mustBe false
      }

      "userAnswers.claimingFor is empty and userAnswers.claimAnyOtherExpense is empty" in {
        when(userAnswers.claimingFor).thenReturn(None)
        when(userAnswers.claimAnyOtherExpense).thenReturn(None)

        navigatorHelper.isClaimingWfh(userAnswers) mustBe false
      }
    }
  }

  "NavigatorHelper on vehiclesRedirect" must {

    "return true" when
      Seq(
        (Some(false), Some(false)),
        (Some(true), Some(false)),
        (Some(false), Some(true)),
        (Some(false), None),
        (None, Some(false))
      ).foreach { case (claimingFuel, useCompanyCar) =>
        s"claimingMileage is true, employerPaidBackAnyExpenses is SomeExpenses, claimingFuel is $claimingFuel and useCompanyCar is $useCompanyCar" in {
          when(userAnswers.claimingMileage).thenReturn(Some(true))
          when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(SomeExpenses))
          when(userAnswers.claimingFuel).thenReturn(claimingFuel)
          when(userAnswers.useCompanyCar).thenReturn(useCompanyCar)

          navigatorHelper.vehiclesRedirect(userAnswers) mustBe true
        }
      }

    "return false" when {

      Seq(
        (Some(false), Some(false)),
        (Some(true), Some(false)),
        (Some(false), Some(true)),
        (Some(false), None),
        (None, Some(false))
      ).foreach { case (claimingFuel, useCompanyCar) =>

        Seq(Some(false), None).foreach { claimingMileage =>
          s"claimingMileage is $claimingMileage, employerPaidBackAnyExpenses is SomeExpenses, claimingFuel is $claimingFuel and useCompanyCar is $useCompanyCar" in {
            when(userAnswers.claimingMileage).thenReturn(claimingMileage)
            when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(SomeExpenses))
            when(userAnswers.claimingFuel).thenReturn(claimingFuel)
            when(userAnswers.useCompanyCar).thenReturn(useCompanyCar)

            navigatorHelper.vehiclesRedirect(userAnswers) mustBe false
          }
        }

        Seq(Some(NoExpenses), Some(AllExpenses), None).foreach { employerPaidBackAnyExpenses =>
          s"claimingMileage is true, employerPaidBackAnyExpenses is $employerPaidBackAnyExpenses, claimingFuel is $claimingFuel and useCompanyCar is $useCompanyCar" in {
            when(userAnswers.claimingMileage).thenReturn(Some(true))
            when(userAnswers.employerPaidBackAnyExpenses).thenReturn(employerPaidBackAnyExpenses)
            when(userAnswers.claimingFuel).thenReturn(claimingFuel)
            when(userAnswers.useCompanyCar).thenReturn(useCompanyCar)

            navigatorHelper.vehiclesRedirect(userAnswers) mustBe false
          }
        }
      }

      Seq(
        (Some(true), None),
        (None, Some(true)),
        (Some(true), Some(true)),
        (None, None)
      ).foreach { case (claimingFuel, useCompanyCar) =>
        s"claimingMileage is true, employerPaidBackAnyExpenses is SomeExpenses, claimingFuel is $claimingFuel and useCompanyCar is $useCompanyCar" in {
          when(userAnswers.claimingMileage).thenReturn(Some(true))
          when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(SomeExpenses))
          when(userAnswers.claimingFuel).thenReturn(claimingFuel)
          when(userAnswers.useCompanyCar).thenReturn(useCompanyCar)

          navigatorHelper.vehiclesRedirect(userAnswers) mustBe false
        }
      }

    }
  }

}
