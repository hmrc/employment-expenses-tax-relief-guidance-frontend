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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import controllers.helpers.ClaimingForListBuilder
import identifiers.*
import models.ClaimingFor
import models.ClaimingFor.{FeesSubscriptions, HomeWorking, MileageFuel, TravelExpenses}
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{doReturn, reset, verify, when}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.libs.json.Reads
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.twirl.api.HtmlFormat
import utils.{CacheMap, UserAnswers}
import views.html.{UseIformFreOnlyView, UsePrintAndPostFreOnlyView}

import scala.concurrent.ExecutionContext.Implicits.global

class UsePrintAndPostControllerSpec extends SpecBase with BeforeAndAfterEach with BeforeAndAfterAll {

  private val claimingForListBuilder = mock[ClaimingForListBuilder]
  private val cacheMap               = mock[CacheMap]
  private val appConfig              = mock[FrontendAppConfig]

  private val usePrintAndPostFreOnlyView = mock[UsePrintAndPostFreOnlyView]
  private val useIformFreOnlyView        = mock[UseIformFreOnlyView]

  private val application = applicationBuilder(Some(cacheMap))
    .overrides(
      bind[ClaimingForListBuilder].toInstance(claimingForListBuilder),
      bind[FrontendAppConfig].toInstance(appConfig),
      bind[UsePrintAndPostFreOnlyView].toInstance(usePrintAndPostFreOnlyView),
      bind[UseIformFreOnlyView].toInstance(useIformFreOnlyView)
    )
    .build()

  private val claimingForList = ClaimingFor.values.filterNot(_ == MileageFuel)
  private val controller      = application.injector.instanceOf[UsePrintAndPostController]
  val redirectionLink: String = controller.redirectionLink(claimingForList)

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset[Object](
      claimingForListBuilder,
      cacheMap,
      appConfig,
      usePrintAndPostFreOnlyView,
      useIformFreOnlyView
    )

    when(claimingForListBuilder.buildClaimingForList(any[UserAnswers])).thenReturn(claimingForList)

    when(cacheMap.getEntry(any[String])(using any)).thenReturn(None)

    when(usePrintAndPostFreOnlyView.apply(any[List[ClaimingFor]])(using any[Request[?]], any[Messages]))
      .thenReturn(HtmlFormat.empty)
    when(useIformFreOnlyView.apply(any[List[ClaimingFor]], any[String])(using any[Request[?]], any[Messages]))
      .thenReturn(HtmlFormat.empty)
    when(appConfig.pegaServiceJourney).thenReturn(false)
    when(appConfig.employeeExpensesClaimByIformUrl).thenReturn("iform-url")
    when(appConfig.employeeExpensesClaimByPegaServicesUrl).thenReturn("pega-url")
    when(appConfig.employeeExpensesClaimByPostUrl).thenReturn("print-and-post-form-url")
  }

  override def afterAll(): Unit = {
    application.stop()
    super.afterAll()
  }

  "UsePrintAndPostController" must {

    def usePrintAndPostRoute = routes.UsePrintAndPostController.onPageLoad().url
    def testRequest          = FakeRequest(GET, usePrintAndPostRoute)

    "return OK and the correct view for a GET" when {

      "UserAnswers contain non-empty moreThanFiveJobs equal to true" in {
        when(cacheMap.getEntry[Boolean](eqTo(MoreThanFiveJobsId.toString))(using any[Reads[Boolean]]))
          .thenReturn(Some(true))

        for {
          result <- route(application, testRequest).value

          _ = result.header.status mustBe OK
          _ = verify(usePrintAndPostFreOnlyView).apply(eqTo(claimingForList))(using any[Request[?]], any[Messages])
        } yield ()
      }

      "UserAnswers contain non-empty moreThanFiveJobs equal to false" in {
        when(cacheMap.getEntry[Boolean](eqTo(MoreThanFiveJobsId.toString))(using any[Reads[Boolean]]))
          .thenReturn(Some(false))

        for {
          result <- route(application, testRequest).value

          _ = result.header.status mustBe OK
          _ = verify(useIformFreOnlyView).apply(eqTo(claimingForList), eqTo(redirectionLink))(
            using any[Request[?]],
            any[Messages]
          )
        } yield ()
      }

      "UserAnswers does NOT contain moreThanFiveJobs field" in {

        for {
          result <- route(application, testRequest).value

          _ = result.header.status mustBe OK
          _ = verify(useIformFreOnlyView).apply(eqTo(claimingForList), eqTo(redirectionLink))(
            using any[Request[?]],
            any[Messages]
          )
        } yield ()
      }
    }

    "redirection to iform with query params when user claims for FeesSubscriptions " in {
      when(appConfig.employeeExpensesClaimByIformUrl).thenReturn("iform-url")

      val redirectionLink = controller.redirectionLink(List(FeesSubscriptions))
      redirectionLink must include("iform-url")
      redirectionLink must include("claiming-for")

    }

    "redirection to iform without query params when user claims for TravelExpenses " in {
      when(appConfig.employeeExpensesClaimByIformUrl).thenReturn("iform-url")

      val redirectionLink = controller.redirectionLink(List(TravelExpenses))
      redirectionLink must include("iform-url")

    }

    "redirection to pega url when user claims for HomeWorking " in {
      when(appConfig.pegaServiceJourney).thenReturn(true)
      when(appConfig.employeeExpensesClaimByPegaServicesUrl).thenReturn("pega-url")

      val redirectionLink = controller.redirectionLink(List(HomeWorking))
      redirectionLink must include("pega-url")

    }
  }

  "printAndPostGuidance" must {

    def redirectPrintAndPostForm = routes.UsePrintAndPostController.printAndPostGuidance().url
    def testRequest              = FakeRequest(GET, redirectPrintAndPostForm)

    "redirect to the print and post form url" in {
      when(appConfig.employeeExpensesClaimByPostUrl).thenReturn("print-and-post-form-url")

      for {
        result <- route(application, testRequest).value

        _ = result.header.status mustBe OK
        _ = redirectionLink must include("print-and-post-form-url")
      } yield ()
    }
  }

}
