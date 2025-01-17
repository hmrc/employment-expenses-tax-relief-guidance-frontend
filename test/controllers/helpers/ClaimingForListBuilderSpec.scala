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
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import utils.UserAnswers

class ClaimingForListBuilderSpec extends PlaySpec with BeforeAndAfterEach {

  private val claimingForListBuilder = new ClaimingForListBuilder

  private val userAnswers = mock[UserAnswers]
  private val claimingForList = ClaimingFor.values

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(userAnswers)
  }

  "ClaimingForListBuilder on buildClaimingForList" must {

    "return List with ONLY HomeWorking" when {
      "claimAnyOtherExpense is true" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(true))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs List(HomeWorking)
      }
    }

    "return List with MileageFuel" when {

      val expectedClaimingForList = claimingForList

      "claimingFor contains MileageFuel and both claimingMileage and claimingFuel are true" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(Some(true))
        when(userAnswers.claimingFuel).thenReturn(Some(true))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }

      "claimingFor contains MileageFuel, claimingMileage is true but claimingFuel is false" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(Some(true))
        when(userAnswers.claimingFuel).thenReturn(Some(false))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }

      "claimingFor contains MileageFuel, claimingMileage is false but claimingFuel is true" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(Some(false))
        when(userAnswers.claimingFuel).thenReturn(Some(true))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }

      "claimingFor contains MileageFuel, claimingMileage is true but claimingFuel is empty" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(Some(true))
        when(userAnswers.claimingFuel).thenReturn(None)

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }

      "claimingFor contains MileageFuel, claimingMileage is empty but claimingFuel is true" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(None)
        when(userAnswers.claimingFuel).thenReturn(Some(true))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }
    }

    "return List with NO MileageFuel" when {

      val expectedClaimingForList = claimingForList.filterNot(_ == MileageFuel)

      "claimingFor contains MileageFuel but both claimingMileage and claimingFuel are false" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(Some(false))
        when(userAnswers.claimingFuel).thenReturn(Some(false))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }

      "claimingFor contains MileageFuel but both claimingMileage and claimingFuel are empty" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        when(userAnswers.claimingFor).thenReturn(Some(claimingForList))
        when(userAnswers.claimingMileage).thenReturn(None)
        when(userAnswers.claimingFuel).thenReturn(None)

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }

      "claimingFor contains NO MileageFuel" in {
        when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))
        val claimingForListWithoutMileageFuel = claimingForList.filterNot(_ == MileageFuel)
        when(userAnswers.claimingFor).thenReturn(Some(claimingForListWithoutMileageFuel))

        val result = claimingForListBuilder.buildClaimingForList(userAnswers)

        result must contain theSameElementsAs expectedClaimingForList
      }
    }
  }
}
