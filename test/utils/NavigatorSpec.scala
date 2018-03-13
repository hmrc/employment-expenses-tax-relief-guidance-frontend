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

package utils

import base.SpecBase
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import controllers.routes
import identifiers._
import models.Claimant
import models.Claimant.{SomeoneElse, You}
import models.HowManyYearsWasTaxPaid
import models.TaxYears._

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" must {
    "go to the Index view" when {
      "an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier)(mock[UserAnswers]) mustBe
          routes.IndexController.onPageLoad()
      }
    }

    "go to the TaxYears view" when {
      "navigating from the claimant view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant)
          .thenReturn(Some(Claimant.You))

        navigator.nextPage(ClaimantId)(mockAnswers) mustBe
          routes.TaxYearsController.onPageLoad()
      }
    }

    "go to the CannotClaimReliefTooLongAgo view" when {
      "navigating from TaxYears and selecting Another Year only" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.taxYears) thenReturn Some(List(AnotherYear))

        navigator.nextPage(TaxYearsId)(mockAnswers) mustBe
          routes.CannotClaimReliefTooLongAgoController.onPageLoad()
      }
    }

    "go to the PaidTaxInRelevantYear view" when {
      "navigating from TaxYears and selecting one specific year" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.taxYears) thenReturn Some(List(ThisYear))

        navigator.nextPage(TaxYearsId)(mockAnswers) mustBe
          routes.PaidTaxInRelevantYearController.onPageLoad()
      }
    }

    "go to CannotClaimReliefSomeYears view" when {
      "navigating from TaxYears and selecting Another Year and at least one specfic year" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.taxYears) thenReturn Some(List(ThisYear, AnotherYear))

        navigator.nextPage(TaxYearsId)(mockAnswers) mustBe
          routes.CannotClaimReliefSomeYearsController.onPageLoad()
      }
    }

    "go to HowManyYearsWasTaxPaid view" when {
      "navigating from TaxYears and selecting multiple specific years" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.taxYears) thenReturn Some(List(ThisYear, LastYear))

        navigator.nextPage(TaxYearsId)(mockAnswers) mustBe
          routes.HowManyYearsWasTaxPaidController.onPageLoad()
      }

      "navigating from CannotClaimReliefSomeYears" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)

        navigator.nextPage(CannotClaimReliefSomeYearsId)(mockAnswers) mustBe
          routes.HowManyYearsWasTaxPaidController.onPageLoad()
      }
    }

    "go to the ClaimingOverPayAsYouEarnThreshold view" when {
      "answering No from the RegisterForSelfAssessment view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.registeredForSelfAssessment)
          .thenReturn(Some(false))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
      }
    }

    "go to the UseSelfAssessment view" when {
      "answering Yes from the RegisterForSelfAssessment view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.registeredForSelfAssessment)
          .thenReturn(Some(true))

        navigator.nextPage(RegisteredForSelfAssessmentId)(mockAnswers) mustBe
          routes.UseSelfAssessmentController.onPageLoad()
      }
    }

    "go to the RegisteredForSelfAssessment view" when {
      "answering Yes from the PaidTaxInRelevantYears view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.paidTaxInRelevantYear) thenReturn Some(true)

        navigator.nextPage(PaidTaxInRelevantYearId)(mockAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }

      "answering All from the HowManyYearsWasTaxPaid view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.howManyYearsWasTaxPaid) thenReturn Some(HowManyYearsWasTaxPaid.All)

        navigator.nextPage(HowManyYearsWasTaxPaidId)(mockAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }

      "navigating from NotEntitledSomeYears" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)

        navigator.nextPage(NotEntitledSomeYearsId)(mockAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }
    }

    "go to the RegisterForSelfAssessment view" when {
      "answering Yes from the ClaimingOverPayAsYouEarnThreshold view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingOverPayAsYouEarnThreshold) thenReturn Some(true)

        navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
          routes.RegisterForSelfAssessmentController.onPageLoad()
      }
    }

    "go to the NotEntitled view" when {
      "answering None from the HowManyYearsWasTaxPaid view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.howManyYearsWasTaxPaid) thenReturn Some(HowManyYearsWasTaxPaid.None)

        navigator.nextPage(HowManyYearsWasTaxPaidId)(mockAnswers) mustBe
          routes.NotEntitledController.onPageLoad()
      }

      "answering No from the PaidTaxInRelevantYear view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimant) thenReturn Some(You)
        when(mockAnswers.paidTaxInRelevantYear) thenReturn Some(false)

        navigator.nextPage(PaidTaxInRelevantYearId)(mockAnswers) mustBe
          routes.NotEntitledController.onPageLoad()
      }
    }

    "go to the EmployerPaidBackExpenses view" when {
      "answering No from the ClaimingOverPayAsYouEarnThreshold view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.claimingOverPayAsYouEarnThreshold) thenReturn Some(false)

        navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(mockAnswers) mustBe
          routes.EmployerPaidBackExpensesController.onPageLoad()
      }
    }

    "go to the UsePrintAndPost view" when {
      "answering No from the EmployerPaidBackExpenses view and the claimant is someone else" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.employerPaidBackExpenses) thenReturn Some(false)
        when(mockAnswers.claimant) thenReturn Some(SomeoneElse)

        navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }

      "answering Yes from the MoreThanFiveJobs view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.moreThanFiveJobs) thenReturn Some(true)

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.UsePrintAndPostController.onPageLoad()
      }
    }

    "go to MoreThanFiveJobs view" when {
      "answering No from the EmployerPaidBackExpenses view and the claimant is you" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.employerPaidBackExpenses) thenReturn Some(false)
        when(mockAnswers.claimant) thenReturn Some(You)

        navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
          routes.MoreThanFiveJobsController.onPageLoad()
      }
    }

    "go to ClaimOnline view" when {
      "answering No from the MoreThanFiveJobs view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.moreThanFiveJobs) thenReturn Some(false)

        navigator.nextPage(MoreThanFiveJobsId)(mockAnswers) mustBe
          routes.ClaimOnlineController.onPageLoad()
      }
    }

    "go to CannotClaimRelief view" when {
      "answering Yes from the EmployerPaidBackExpenses view" in {
        val mockAnswers = mock[UserAnswers]
        when(mockAnswers.employerPaidBackExpenses) thenReturn Some(true)
        when(mockAnswers.claimant) thenReturn Some(You)

        navigator.nextPage(EmployerPaidBackExpensesId)(mockAnswers) mustBe
          routes.CannotClaimReliefController.onPageLoad()
      }
    }
  }
}
