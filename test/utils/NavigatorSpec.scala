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

package utils

import config.FrontendAppConfig
import controllers.routes
import identifiers._
import models.ClaimingForMoreThanOneJob.{MoreThanOneJob, OneJob}
import models.EmployerPaid.{AllExpenses, NoExpenses, SomeExpenses}
import models.{Claimant, ClaimingFor}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class NavigatorSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  private val navigatorHelper   = mock[NavigatorHelper]
  private val frontendAppConfig = mock[FrontendAppConfig]
  private val userAnswers       = mock[UserAnswers]

  private val navigator = new Navigator(navigatorHelper)(frontendAppConfig)

  override def beforeEach(): Unit =
    reset[Object](navigatorHelper, frontendAppConfig, userAnswers)

  "Navigator on firstPage" must {
    "return Call to ClaimingFor page" in {
      navigator.firstPage mustBe routes.ClaimingForController.onPageLoad()
    }
  }

  "Navigator on nextPage" when {

    "provided with an unknown identifier" must {
      "return Call to IndexController" in {
        case object UnknownIdentifier extends Identifier

        navigator.nextPage(UnknownIdentifier)(userAnswers) mustBe routes.IndexController.onPageLoad
      }
    }

    "provided with ClaimingForId identifier" when {

      "UserAnswers.claimingFor is present" when {

        "onlineJourneyShutterEnabled flag is set to true" must {

          ClaimingFor.values.foreach { claimingFor =>
            s"return Call to ClaimantController when claiming for $claimingFor" in {
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(ClaimingForId)(userAnswers) mustBe routes.ClaimantController.onPageLoad()
            }
          }

          "return Call to ClaimantController when claimingFor is an empty List" in {
            when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(true)
            when(userAnswers.claimingFor).thenReturn(Some(List.empty))

            navigator.nextPage(ClaimingForId)(userAnswers) mustBe routes.ClaimantController.onPageLoad()
          }
        }

        "onlineJourneyShutterEnabled flag is set to false" must {

          "return Call to ClaimAnyOtherExpenseController when claiming for HomeWorking" in {
            when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
            when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.HomeWorking)))

            navigator.nextPage(ClaimingForId)(userAnswers) mustBe routes.ClaimAnyOtherExpenseController.onPageLoad()
          }

          ClaimingFor.values.filterNot(_ == ClaimingFor.HomeWorking).foreach { claimingFor =>
            s"return Call to ClaimantController when claiming for $claimingFor" in {
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(ClaimingForId)(userAnswers) mustBe routes.ClaimantController.onPageLoad()
            }
          }

          "return Call to ClaimantController when claimingFor is an empty List" in {
            when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
            when(userAnswers.claimingFor).thenReturn(Some(List.empty))

            navigator.nextPage(ClaimingForId)(userAnswers) mustBe routes.ClaimantController.onPageLoad()
          }
        }
      }

      "UserAnswers.claimingFor is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimingFor).thenReturn(None)

          navigator.nextPage(ClaimingForId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with ClaimantId identifier" when {

      "UserAnswers.claimant is 'You'" must {

        "return Call to DisclaimerController" when {

          "NavigatorHelper.isClaimingWfh returns true" in {
            when(userAnswers.claimant).thenReturn(Some(Claimant.You))
            when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(true)

            navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.DisclaimerController.onPageLoad()
          }
        }

        "return Call to PaidTaxInRelevantYearController" when {

          ClaimingFor.values.filterNot(_ == ClaimingFor.HomeWorking).foreach { claimingFor =>
            s"claimingFor is :$claimingFor" in {
              when(userAnswers.claimant).thenReturn(Some(Claimant.You))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.PaidTaxInRelevantYearController.onPageLoad()
            }
          }

          "claimingFor is an empty List and claimAnyOtherExpense is true" in {
            when(userAnswers.claimant).thenReturn(Some(Claimant.You))
            when(userAnswers.claimingFor).thenReturn(Some(List.empty))

            navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.PaidTaxInRelevantYearController.onPageLoad()
          }
        }

        "return Call to SessionExpiredController" when {
          "claimingFor is empty and claimAnyOtherExpense is false" in {
            when(userAnswers.claimant).thenReturn(Some(Claimant.You))
            when(userAnswers.claimingFor).thenReturn(None)
            when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))

            navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
          }
        }
      }

      "UserAnswers.claimant is 'SomeoneElse'" must {

        "return Call to UsePrintAndPostController.printAndPostGuidance" when {

          ClaimingFor.values.foreach { claimingFor =>
            s"claimingFor is: $claimingFor" in {
              when(userAnswers.claimant).thenReturn(Some(Claimant.SomeoneElse))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.UsePrintAndPostController.printAndPostGuidance()
            }
          }

          "claimingFor is empty" in {
            when(userAnswers.claimant).thenReturn(Some(Claimant.SomeoneElse))
            when(userAnswers.claimingFor).thenReturn(Some(List.empty))

            navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.UsePrintAndPostController.printAndPostGuidance()
          }
        }
      }

      "UserAnswers.claimant is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimant).thenReturn(None)

          navigator.nextPage(ClaimantId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with ClaimAnyOtherExpenseId identifier" when {

      "UserAnswers.claimAnyOtherExpense is true" must {
        "return Call to ClaimantController" in {
          when(userAnswers.claimAnyOtherExpense).thenReturn(Some(true))

          navigator.nextPage(ClaimAnyOtherExpenseId)(userAnswers) mustBe routes.ClaimantController.onPageLoad()
        }
      }

      "UserAnswers.claimAnyOtherExpense is false" must {
        "return Call to ClaimingForController" in {
          when(userAnswers.claimAnyOtherExpense).thenReturn(Some(false))

          navigator.nextPage(ClaimAnyOtherExpenseId)(userAnswers) mustBe routes.ClaimingForController.onPageLoad()
        }
      }

      "UserAnswers.claimAnyOtherExpense is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimAnyOtherExpense).thenReturn(None)

          navigator.nextPage(ClaimAnyOtherExpenseId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with PaidTaxInRelevantYearId identifier" when {

      "UserAnswers.paidTaxInRelevantYear is true" must {
        "return Call to WillPayTaxController" in {
          when(userAnswers.paidTaxInRelevantYear).thenReturn(Some(true))

          navigator.nextPage(PaidTaxInRelevantYearId)(userAnswers) mustBe routes.WillPayTaxController.onPageLoad()
        }
      }

      "UserAnswers.paidTaxInRelevantYear is false" must {
        "return Call to CannotClaimReliefTooLongAgoController" in {
          when(userAnswers.paidTaxInRelevantYear).thenReturn(Some(false))

          navigator.nextPage(PaidTaxInRelevantYearId)(userAnswers) mustBe routes.CannotClaimReliefTooLongAgoController
            .onPageLoad()
        }
      }

      "UserAnswers.paidTaxInRelevantYear is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.paidTaxInRelevantYear).thenReturn(None)

          navigator.nextPage(PaidTaxInRelevantYearId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with NotEntitledSomeYearsId identifier" must {
      "return Call to RegisteredForSelfAssessmentController" in {
        navigator.nextPage(NotEntitledSomeYearsId)(userAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }
    }

    "provided with RegisteredForSelfAssessmentId identifier" when {

      "UserAnswers.registeredForSelfAssessment is true" must {

        "return Call to UseSelfAssessmentController" when {

          val allClaimingFor         = (ClaimingFor.values.map(List(_)) :+ List.empty).map(Some(_))
          val allClaimingForWithNone = allClaimingFor :+ None

          allClaimingForWithNone.foreach { claimingFor =>
            s"UserAnswers.claimingFor is: $claimingFor" in {
              when(userAnswers.registeredForSelfAssessment).thenReturn(Some(true))
              when(userAnswers.claimingFor).thenReturn(claimingFor)

              navigator.nextPage(RegisteredForSelfAssessmentId)(userAnswers) mustBe
                routes.UseSelfAssessmentController.onPageLoad()
            }
          }
        }
      }

      "UserAnswers.registeredForSelfAssessment is false" must {

        "return Call to WhichYearsAreYouClaimingForController" when {
          "NavigatorHelper.isClaimingWfh returns true" in {
            when(userAnswers.registeredForSelfAssessment).thenReturn(Some(false))
            when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(true)

            navigator.nextPage(RegisteredForSelfAssessmentId)(userAnswers) mustBe
              routes.WhichYearsAreYouClaimingForController.onPageLoad()
          }
        }

        "return Call to ClaimingOverPayAsYouEarnThresholdController" when {

          ClaimingFor.values.filterNot(_ == ClaimingFor.HomeWorking).foreach { claimingFor =>
            s"claimingFor is: $claimingFor" in {
              when(userAnswers.registeredForSelfAssessment).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(RegisteredForSelfAssessmentId)(userAnswers) mustBe
                routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
            }
          }

          "claimingFor is an empty List" in {
            when(userAnswers.registeredForSelfAssessment).thenReturn(Some(false))
            when(userAnswers.claimingFor).thenReturn(Some(List.empty))

            navigator.nextPage(RegisteredForSelfAssessmentId)(userAnswers) mustBe
              routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
          }
        }
      }

      "UserAnswers.registeredForSelfAssessment is empty" must {

        "return Call to SessionExpiredController" when
          ClaimingFor.values.foreach { claimingFor =>
            s"claimingFor is: $claimingFor" in {
              when(userAnswers.registeredForSelfAssessment).thenReturn(None)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(RegisteredForSelfAssessmentId)(userAnswers) mustBe
                routes.SessionExpiredController.onPageLoad
            }
          }
      }
    }

    "provided with ClaimingOverPayAsYouEarnThresholdId identifier" when {

      "UserAnswers.claimingOverPayAsYouEarnThreshold is true" must {
        "return Call to RegisterForSelfAssessmentController" in {
          when(userAnswers.claimingOverPayAsYouEarnThreshold).thenReturn(Some(true))

          navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(userAnswers) mustBe
            routes.RegisterForSelfAssessmentController.onPageLoad()
        }
      }

      "UserAnswers.claimingOverPayAsYouEarnThreshold is false" must {
        "return Call to EmployerPaidBackAnyExpensesController" in {
          when(userAnswers.claimingOverPayAsYouEarnThreshold).thenReturn(Some(false))

          navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(userAnswers) mustBe
            routes.EmployerPaidBackAnyExpensesController.onPageLoad()
        }
      }

      "UserAnswers.claimingOverPayAsYouEarnThreshold is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimingOverPayAsYouEarnThreshold).thenReturn(None)

          navigator.nextPage(ClaimingOverPayAsYouEarnThresholdId)(userAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with MoreThanFiveJobsId identifier" when {

      "UserAnswers.moreThanFiveJobs is true" must {
        "return Call to UsePrintAndPostController" in {
          when(userAnswers.moreThanFiveJobs).thenReturn(Some(true))

          navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.UsePrintAndPostController.onPageLoad()
        }
      }

      "UserAnswers.moreThanFiveJobs is false" must {

        val claimingForWithoutClothingAndFuel = ClaimingFor.values
          .filterNot(_ == ClaimingFor.UniformsClothingTools)
          .filterNot(_ == ClaimingFor.MileageFuel)

        "return Call to ClaimingForMoreThanOneJobController" when {
          "UserAnswers.claimingFor is UniformsClothingTools and freOnlyJourneyEnabled is true" in {
            when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
            when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.UniformsClothingTools)))
            when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(true)
            navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe
              routes.ClaimingForMoreThanOneJobController.onPageLoad()
          }
        }

        "return Call to ClaimOnlineController" when {

          "UserAnswers.claimingFor is MileageFuel" when {
            "NavigatorHelper.vehiclesRedirect returns true" in {
              when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))
              when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(false)
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
              when(navigatorHelper.vehiclesRedirect(any[UserAnswers])).thenReturn(true)

              navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.ClaimOnlineController.onPageLoad()
            }
          }

          claimingForWithoutClothingAndFuel.foreach { claimingFor =>
            s"UserAnswers.claimingFor is: $claimingFor, freOnlyJourneyEnabled is false, and onlineJourneyShutterEnabled is false" in {
              when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))
              when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(false)
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)

              navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.ClaimOnlineController.onPageLoad()
            }
          }
        }

        "return Call to UsePrintAndPostController" when {

          claimingForWithoutClothingAndFuel.foreach { claimingFor =>
            s"UserAnswers.claimingFor is: $claimingFor, freOnlyJourneyEnabled is true and onlineJourneyShutterEnabled is true" in {
              when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))
              when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(true)
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(true)

              navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.UsePrintAndPostController.onPageLoad()
            }

            s"UserAnswers.claimingFor is: $claimingFor, freOnlyJourneyEnabled is true and onlineJourneyShutterEnabled is false" in {
              when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))
              when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(true)
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)

              navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.UsePrintAndPostController.onPageLoad()
            }

            s"UserAnswers.claimingFor is: $claimingFor, freOnlyJourneyEnabled is false and onlineJourneyShutterEnabled is true" in {
              when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))
              when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(false)
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(true)

              navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.UsePrintAndPostController.onPageLoad()
            }
          }

          "UserAnswers.claimingFor is MileageFuel" when {
            "NavigatorHelper.vehiclesRedirect returns false" in {
              when(userAnswers.moreThanFiveJobs).thenReturn(Some(false))
              when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))
              when(frontendAppConfig.freOnlyJourneyEnabled).thenReturn(false)
              when(frontendAppConfig.onlineJourneyShutterEnabled).thenReturn(false)
              when(navigatorHelper.vehiclesRedirect(any[UserAnswers])).thenReturn(false)

              navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.UsePrintAndPostController.onPageLoad()
            }
          }
        }
      }

      "UserAnswers.moreThanFiveJobs is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.moreThanFiveJobs).thenReturn(None)

          navigator.nextPage(MoreThanFiveJobsId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with ClaimingForMoreThanOneJobId identifier" when {

      "UserAnswers.claimingForMoreThanOneJob is MoreThanOneJob" must {

        "return Call to ClaimByAlternativeController" in {
          when(userAnswers.claimingForMoreThanOneJob).thenReturn(Some(MoreThanOneJob))

          navigator.nextPage(ClaimingForMoreThanOneJobId)(userAnswers) mustBe
            routes.ClaimByAlternativeController.onPageLoad()
        }
      }

      "UserAnswers.claimingForMoreThanOneJob is OneJob" must {
        "return Call to ClaimOnlineController" in {
          when(userAnswers.claimingForMoreThanOneJob).thenReturn(Some(OneJob))

          navigator.nextPage(ClaimingForMoreThanOneJobId)(userAnswers) mustBe
            routes.ClaimOnlineController.onPageLoad()
        }
      }

      "UserAnswers.claimingForMoreThanOneJob is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimingForMoreThanOneJob).thenReturn(None)

          navigator.nextPage(ClaimingForMoreThanOneJobId)(userAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with EmployerPaidBackAnyExpensesId identifier" when {

      "NavigatorHelper.isClaimingWfh returns true" when {

        Seq(SomeExpenses, NoExpenses).foreach { employerPaidBackAnyExpenses =>
          s"UserAnswers.employerPaidBackAnyExpenses is $employerPaidBackAnyExpenses, and claimingFor contains MileageFuel" must {
            "return Call to UseOwnCarController" in {
              when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(true)
              when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaidBackAnyExpenses))
              when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))

              navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
                routes.UseOwnCarController.onPageLoad()
            }
          }

          s"UserAnswers.employerPaidBackAnyExpenses is $employerPaidBackAnyExpenses, and claimingFor does NOT contain MileageFuel" must {
            "return Call to MoreThanFiveJobsController" in {
              when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(true)
              when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaidBackAnyExpenses))
              val claimingForList = ClaimingFor.values.filterNot(_ == ClaimingFor.MileageFuel)
              when(userAnswers.claimingFor).thenReturn(Some(claimingForList))

              navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
                routes.MoreThanFiveJobsController.onPageLoad()
            }
          }
        }

        "UserAnswers.employerPaidBackAnyExpenses is AllExpenses" must {
          "return Call to CannotClaimWFHReliefController" in {
            when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(true)
            when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(AllExpenses))

            navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
              routes.CannotClaimWFHReliefController.onPageLoad()
          }
        }

        "UserAnswers.employerPaidBackAnyExpenses is empty" must {
          "return Call to SessionExpiredController" in {
            when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(true)
            when(userAnswers.employerPaidBackAnyExpenses).thenReturn(None)

            navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
              routes.SessionExpiredController.onPageLoad
          }
        }
      }

      "NavigatorHelper.isClaimingWfh returns false" when {

        Seq(SomeExpenses, NoExpenses).foreach { employerPaidBackAnyExpenses =>
          s"UserAnswers.employerPaidBackAnyExpenses is $employerPaidBackAnyExpenses" must {

            "return Call to UseOwnCarController" when {
              "UserAnswers.claimingFor contains MileageFuel" in {
                when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(false)
                when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaidBackAnyExpenses))
                when(userAnswers.claimingFor).thenReturn(
                  Some(List(ClaimingFor.UniformsClothingTools, ClaimingFor.MileageFuel))
                )

                navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
                  routes.UseOwnCarController.onPageLoad()
              }
            }

            "return Call to MoreThanFiveJobsController" when {
              "UserAnswers.claimingFor does NOT contain MileageFuel" in {
                when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(false)
                when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(employerPaidBackAnyExpenses))
                val claimingForList = ClaimingFor.values.filterNot(_ == ClaimingFor.MileageFuel)
                when(userAnswers.claimingFor).thenReturn(Some(claimingForList))

                navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
                  routes.MoreThanFiveJobsController.onPageLoad()
              }
            }
          }
        }

        "UserAnswers.employerPaidBackAnyExpenses is AllExpenses" must {
          "return Call to CannotClaimReliefController" in {
            when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(false)
            when(userAnswers.employerPaidBackAnyExpenses).thenReturn(Some(AllExpenses))

            navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
              routes.CannotClaimReliefController.onPageLoad()
          }
        }

        "UserAnswers.employerPaidBackAnyExpenses is empty" must {
          "return Call to SessionExpiredController" in {
            when(navigatorHelper.isClaimingWfh(any[UserAnswers])).thenReturn(false)
            when(userAnswers.employerPaidBackAnyExpenses).thenReturn(None)

            navigator.nextPage(EmployerPaidBackAnyExpensesId)(userAnswers) mustBe
              routes.SessionExpiredController.onPageLoad
          }
        }
      }

    }

    "provided with ClaimingMileageId identifier" must {
      "return Call to UseCompanyCarController" in {
        navigator.nextPage(ClaimingMileageId)(userAnswers) mustBe
          routes.UseCompanyCarController.onPageLoad()
      }
    }

    "provided with UseOwnCarId identifier" when {

      "UserAnswers.useOwnCar is true" must {
        "return Call to ClaimingMileageController" in {
          when(userAnswers.useOwnCar).thenReturn(Some(true))

          navigator.nextPage(UseOwnCarId)(userAnswers) mustBe routes.ClaimingMileageController.onPageLoad()
        }
      }

      "UserAnswers.useOwnCar is false" must {
        "return Call to UseCompanyCarController" in {
          when(userAnswers.useOwnCar).thenReturn(Some(false))

          navigator.nextPage(UseOwnCarId)(userAnswers) mustBe routes.UseCompanyCarController.onPageLoad()
        }
      }

      "UserAnswers.useOwnCar is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.useOwnCar).thenReturn(None)

          navigator.nextPage(UseOwnCarId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with UseCompanyCarId identifier" when {

      "UserAnswers.useCompanyCar is true" must {
        "return Call to ClaimingFuelController" in {
          when(userAnswers.useCompanyCar).thenReturn(Some(true))

          navigator.nextPage(UseCompanyCarId)(userAnswers) mustBe routes.ClaimingFuelController.onPageLoad()
        }
      }

      "UserAnswers.useCompanyCar is false" must {

        val claimsForMergedJourney: Set[ClaimingFor] =
          ClaimingFor.values.filterNot(_ == ClaimingFor.MileageFuel).toSet

        "return Call to MoreThanFiveJobsController" when {

          ClaimingFor.values.foreach { claimingFor =>
            s"UserAnswers.useOwnCar is true, UserAnswers.claimingMileage is true, and UserAnswers.claimingFor is: $claimingFor" in {
              when(userAnswers.useCompanyCar).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(Some(true))
              when(userAnswers.claimingMileage).thenReturn(Some(true))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(UseCompanyCarId)(userAnswers) mustBe routes.MoreThanFiveJobsController.onPageLoad()
            }
          }

          val useOwnCarAndClaimingMileageOptions = List(
            (Some(false), None),
            (Some(false), Some(false)),
            (Some(false), Some(true)),
            (Some(true), None),
            (Some(true), Some(false))
          )
          val testData = for {
            useOwnCarAndClaimingMileage <- useOwnCarAndClaimingMileageOptions
            claimingFor                 <- claimsForMergedJourney
          } yield (useOwnCarAndClaimingMileage._1, useOwnCarAndClaimingMileage._2, claimingFor)

          testData.foreach { case (useOwnCar, claimingMileage, claimingFor) =>
            s"UserAnswers.useOwnCar is: $useOwnCar, UserAnswers.claimingMileage is: $claimingMileage, and UserAnswers.claimingFor is: $claimingFor and MileageFuel" in {
              when(userAnswers.useCompanyCar).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(useOwnCar)
              when(userAnswers.claimingMileage).thenReturn(claimingMileage)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor, ClaimingFor.MileageFuel)))

              navigator.nextPage(UseCompanyCarId)(userAnswers) mustBe routes.MoreThanFiveJobsController.onPageLoad()
            }
          }
        }

        "return Call to CannotClaimMileageCostsController" when {

          val useOwnCarAndClaimingMileageOptions = List(
            (Some(false), None),
            (Some(false), Some(false)),
            (Some(false), Some(true)),
            (Some(true), None),
            (Some(true), Some(false))
          )

          useOwnCarAndClaimingMileageOptions.foreach { case (useOwnCar, claimingMileage) =>
            s"UserAnswers.useOwnCar is: $useOwnCar, UserAnswers.claimingMileage is: $claimingMileage, and UserAnswers.claimingFor is: MileageFuel" in {
              when(userAnswers.useCompanyCar).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(useOwnCar)
              when(userAnswers.claimingMileage).thenReturn(claimingMileage)
              when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))

              navigator.nextPage(UseCompanyCarId)(userAnswers) mustBe routes.CannotClaimMileageCostsController
                .onPageLoad()
            }
          }
        }

        "return Call to SessionExpiredController" when {

          val useOwnCarAndClaimingMileageOptions = List(
            (None, None),
            (None, Some(false)),
            (None, Some(true))
          )
          val testData = for {
            useOwnCarAndClaimingMileage <- useOwnCarAndClaimingMileageOptions
            claimingFor                 <- ClaimingFor.values
          } yield (useOwnCarAndClaimingMileage._1, useOwnCarAndClaimingMileage._2, claimingFor)

          testData.foreach { case (useOwnCar, claimingMileage, claimingFor) =>
            s"UserAnswers.useOwnCar is: $useOwnCar, UserAnswers.claimingMileage is: $claimingMileage, and UserAnswers.claimingFor is: $claimingFor" in {
              when(userAnswers.useCompanyCar).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(useOwnCar)
              when(userAnswers.claimingMileage).thenReturn(claimingMileage)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(UseCompanyCarId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
            }
          }
        }
      }

      "UserAnswers.useCompanyCar is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.useCompanyCar).thenReturn(None)

          navigator.nextPage(UseCompanyCarId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with ClaimingFuelId identifier" when {

      "UserAnswers.claimingFuel is true" must {
        "return Call to MoreThanFiveJobsController" in {
          when(userAnswers.claimingFuel).thenReturn(Some(true))

          navigator.nextPage(ClaimingFuelId)(userAnswers) mustBe routes.MoreThanFiveJobsController.onPageLoad()
        }
      }

      "UserAnswers.claimingFuel is false" must {

        val claimsForMergedJourney: Set[ClaimingFor] =
          ClaimingFor.values.filterNot(_ == ClaimingFor.MileageFuel).toSet

        "return Call to MoreThanFiveJobsController" when {

          ClaimingFor.values.foreach { claimingFor =>
            s"UserAnswers.useOwnCar is true, UserAnswers.claimingMileage is true, and UserAnswers.claimingFor is: $claimingFor" in {
              when(userAnswers.claimingFuel).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(Some(true))
              when(userAnswers.claimingMileage).thenReturn(Some(true))
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(ClaimingFuelId)(userAnswers) mustBe routes.MoreThanFiveJobsController.onPageLoad()
            }
          }

          val useOwnCarAndClaimingMileageOptions = List(
            (Some(false), None),
            (Some(false), Some(false)),
            (Some(false), Some(true)),
            (Some(true), None),
            (Some(true), Some(false))
          )
          val testData = for {
            useOwnCarAndClaimingMileage <- useOwnCarAndClaimingMileageOptions
            claimingFor                 <- claimsForMergedJourney
          } yield (useOwnCarAndClaimingMileage._1, useOwnCarAndClaimingMileage._2, claimingFor)

          testData.foreach { case (useOwnCar, claimingMileage, claimingFor) =>
            s"UserAnswers.useOwnCar is: $useOwnCar, UserAnswers.claimingMileage is: $claimingMileage, and UserAnswers.claimingFor is: $claimingFor and MileageFuel" in {
              when(userAnswers.claimingFuel).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(useOwnCar)
              when(userAnswers.claimingMileage).thenReturn(claimingMileage)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor, ClaimingFor.MileageFuel)))

              navigator.nextPage(ClaimingFuelId)(userAnswers) mustBe routes.MoreThanFiveJobsController.onPageLoad()
            }
          }
        }

        "return Call to CannotClaimMileageCostsController" when {

          val useOwnCarAndClaimingMileageOptions = List(
            (Some(false), None),
            (Some(false), Some(false)),
            (Some(false), Some(true)),
            (Some(true), None),
            (Some(true), Some(false))
          )

          useOwnCarAndClaimingMileageOptions.foreach { case (useOwnCar, claimingMileage) =>
            s"UserAnswers.useOwnCar is: $useOwnCar, UserAnswers.claimingMileage is: $claimingMileage, and UserAnswers.claimingFor is: MileageFuel" in {
              when(userAnswers.claimingFuel).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(useOwnCar)
              when(userAnswers.claimingMileage).thenReturn(claimingMileage)
              when(userAnswers.claimingFor).thenReturn(Some(List(ClaimingFor.MileageFuel)))

              navigator.nextPage(ClaimingFuelId)(userAnswers) mustBe routes.CannotClaimMileageCostsController
                .onPageLoad()
            }
          }
        }

        "return Call to SessionExpiredController" when {

          val useOwnCarAndClaimingMileageOptions = List(
            (None, None),
            (None, Some(false)),
            (None, Some(true))
          )
          val testData = for {
            useOwnCarAndClaimingMileage <- useOwnCarAndClaimingMileageOptions
            claimingFor                 <- ClaimingFor.values
          } yield (useOwnCarAndClaimingMileage._1, useOwnCarAndClaimingMileage._2, claimingFor)

          testData.foreach { case (useOwnCar, claimingMileage, claimingFor) =>
            s"UserAnswers.useOwnCar is: $useOwnCar, UserAnswers.claimingMileage is: $claimingMileage, and UserAnswers.claimingFor is: $claimingFor" in {
              when(userAnswers.claimingFuel).thenReturn(Some(false))
              when(userAnswers.useOwnCar).thenReturn(useOwnCar)
              when(userAnswers.claimingMileage).thenReturn(claimingMileage)
              when(userAnswers.claimingFor).thenReturn(Some(List(claimingFor)))

              navigator.nextPage(ClaimingFuelId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
            }
          }
        }
      }

      "UserAnswers.useCompanyCar is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimingFuel).thenReturn(None)

          navigator.nextPage(ClaimingFuelId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with WillPayTaxId identifier" when {

      "UserAnswers.willPayTax is true" must {
        "return Call to ClaimingMileageController" in {
          when(userAnswers.willPayTax).thenReturn(Some(true))

          navigator.nextPage(WillPayTaxId)(userAnswers) mustBe routes.RegisteredForSelfAssessmentController.onPageLoad()
        }
      }

      "UserAnswers.willPayTax is false" must {
        "return Call to UseCompanyCarController" in {
          when(userAnswers.willPayTax).thenReturn(Some(false))

          navigator.nextPage(WillPayTaxId)(userAnswers) mustBe routes.WillNotPayTaxController.onPageLoad()
        }
      }

      "UserAnswers.willPayTax is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.willPayTax).thenReturn(None)

          navigator.nextPage(WillPayTaxId)(userAnswers) mustBe routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with WhichYearsAreYouClaimingForId identifier" when {

      "UserAnswers.whichYearsAreYouClaimingFor is true" must {
        "return Call to ClaimingMileageController" in {
          when(userAnswers.whichYearsAreYouClaimingFor).thenReturn(Some(true))

          navigator.nextPage(WhichYearsAreYouClaimingForId)(userAnswers) mustBe
            routes.InformCustomerClaimNowInWeeksController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is false" must {
        "return Call to UseCompanyCarController" in {
          when(userAnswers.whichYearsAreYouClaimingFor).thenReturn(Some(false))

          navigator.nextPage(WhichYearsAreYouClaimingForId)(userAnswers) mustBe
            routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.whichYearsAreYouClaimingFor).thenReturn(None)

          navigator.nextPage(WhichYearsAreYouClaimingForId)(userAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with InformCustomerClaimNowInWeeksId identifier" when {

      "UserAnswers.whichYearsAreYouClaimingFor is true" must {
        "return Call to ClaimingMileageController" in {
          when(userAnswers.whichYearsAreYouClaimingFor).thenReturn(Some(true))

          navigator.nextPage(InformCustomerClaimNowInWeeksId)(userAnswers) mustBe
            routes.ClaimingOverPayAsYouEarnThresholdController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is false" must {
        "return Call to UseCompanyCarController" in {
          when(userAnswers.whichYearsAreYouClaimingFor).thenReturn(Some(false))

          navigator.nextPage(InformCustomerClaimNowInWeeksId)(userAnswers) mustBe
            routes.EmployerPaidBackAnyExpensesController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.whichYearsAreYouClaimingFor).thenReturn(None)

          navigator.nextPage(InformCustomerClaimNowInWeeksId)(userAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with WillNotPayTaxId identifier" must {
      "return Call to UseCompanyCarController" in {
        navigator.nextPage(WillNotPayTaxId)(userAnswers) mustBe
          routes.RegisteredForSelfAssessmentController.onPageLoad()
      }
    }

    "provided with RegisterForSelfAssessmentId identifier" must {
      "return Call to UseCompanyCarController" in {
        navigator.nextPage(RegisterForSelfAssessmentId)(userAnswers) mustBe
          routes.EmployerPaidBackAnyExpensesController.onPageLoad()
      }
    }

    "provided with ChangeOtherExpensesId identifier" must {
      "return Call to UseCompanyCarController" in {
        navigator.nextPage(ChangeOtherExpensesId)(userAnswers) mustBe
          routes.ClaimantController.onPageLoad()
      }
    }

    "provided with ChangeUniformsWorkClothingToolsId identifier" must {
      "return Call to UseCompanyCarController" in {
        navigator.nextPage(ChangeUniformsWorkClothingToolsId)(userAnswers) mustBe
          routes.ClaimantController.onPageLoad()
      }
    }

    "provided with ClaimingForCurrentYearId identifier" when {

      "UserAnswers.whichYearsAreYouClaimingFor is true" must {
        "return Call to ClaimingMileageController" in {
          when(userAnswers.claimingForCurrentYear).thenReturn(Some(true))

          navigator.nextPage(ClaimingForCurrentYearId)(userAnswers) mustBe
            routes.SaCheckDisclaimerCurrentYearController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is false" must {
        "return Call to UseCompanyCarController" in {
          when(userAnswers.claimingForCurrentYear).thenReturn(Some(false))

          navigator.nextPage(ClaimingForCurrentYearId)(userAnswers) mustBe
            routes.UseSelfAssessmentController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimingForCurrentYear).thenReturn(None)

          navigator.nextPage(ClaimingForCurrentYearId)(userAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }
    }

    "provided with SaCheckDisclaimerAllYearsId identifier" when {

      "UserAnswers.whichYearsAreYouClaimingFor is true" must {
        "return Call to ClaimingMileageController" in {
          when(userAnswers.claimingForCurrentYear).thenReturn(Some(true))

          navigator.nextPage(SaCheckDisclaimerAllYearsId)(userAnswers) mustBe
            routes.SaCheckDisclaimerAllYearsController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is false" must {
        "return Call to UseCompanyCarController" in {
          when(userAnswers.claimingForCurrentYear).thenReturn(Some(false))

          navigator.nextPage(SaCheckDisclaimerAllYearsId)(userAnswers) mustBe
            routes.UseSelfAssessmentController.onPageLoad()
        }
      }

      "UserAnswers.whichYearsAreYouClaimingFor is empty" must {
        "return Call to SessionExpiredController" in {
          when(userAnswers.claimingForCurrentYear).thenReturn(None)

          navigator.nextPage(SaCheckDisclaimerAllYearsId)(userAnswers) mustBe
            routes.SessionExpiredController.onPageLoad
        }
      }
    }
  }

}
