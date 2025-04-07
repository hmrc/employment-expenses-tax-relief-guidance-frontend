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
import identifiers._
import models.ClaimingFor
import models.ClaimingFor.MileageFuel
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.libs.json.Reads
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{CacheMap, UserAnswers}
import views.html.{UseIformFreOnlyView, UsePrintAndPostDetailedView, UsePrintAndPostFreOnlyView, UsePrintAndPostView}

import scala.concurrent.ExecutionContext.Implicits.global

class UsePrintAndPostControllerSpec extends SpecBase with BeforeAndAfterEach with BeforeAndAfterAll {

  private val claimingForListBuilder = mock[ClaimingForListBuilder]
  private val cacheMap               = mock[CacheMap]
  private val appConfig              = mock[FrontendAppConfig]

  private val usePrintAndPostView         = mock[UsePrintAndPostView]
  private val usePrintAndPostDetailedView = mock[UsePrintAndPostDetailedView]
  private val usePrintAndPostFreOnlyView  = mock[UsePrintAndPostFreOnlyView]
  private val useIformFreOnlyView         = mock[UseIformFreOnlyView]

  private val application = applicationBuilder(Some(cacheMap))
    .overrides(
      bind[ClaimingForListBuilder].toInstance(claimingForListBuilder),
      bind[FrontendAppConfig].toInstance(appConfig),
      bind[UsePrintAndPostView].toInstance(usePrintAndPostView),
      bind[UsePrintAndPostDetailedView].toInstance(usePrintAndPostDetailedView),
      bind[UsePrintAndPostFreOnlyView].toInstance(usePrintAndPostFreOnlyView),
      bind[UseIformFreOnlyView].toInstance(useIformFreOnlyView)
    )
    .build()

  private val claimingForList = ClaimingFor.values.filterNot(_ == MileageFuel)

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(
      claimingForListBuilder,
      cacheMap,
      appConfig,
      usePrintAndPostView,
      usePrintAndPostDetailedView,
      usePrintAndPostFreOnlyView,
      useIformFreOnlyView
    )

    when(claimingForListBuilder.buildClaimingForList(any[UserAnswers])).thenReturn(claimingForList)

    when(cacheMap.getEntry(any[String])(any[Reads[_]])).thenReturn(None)

    when(usePrintAndPostView.apply(any[Boolean], any[Boolean])(any[Request[_]], any[Messages]))
      .thenReturn(HtmlFormat.empty)
    when(usePrintAndPostDetailedView.apply(any[List[ClaimingFor]])(any[Request[_]], any[Messages]))
      .thenReturn(HtmlFormat.empty)
    when(usePrintAndPostFreOnlyView.apply(any[List[ClaimingFor]])(any[Request[_]], any[Messages]))
      .thenReturn(HtmlFormat.empty)
    when(useIformFreOnlyView.apply(any[List[ClaimingFor]])(any[Request[_]], any[Messages])).thenReturn(HtmlFormat.empty)
  }

  override def afterAll(): Unit = {
    application.stop()
    super.afterAll()
  }

  "UsePrintAndPostController" must {

    def usePrintAndPostRoute = routes.UsePrintAndPostController.onPageLoad().url
    def testRequest          = FakeRequest(GET, usePrintAndPostRoute)

    "return OK and the correct view for a GET" when {

      "both freOnlyJourney and onlineJourneyShutter are enabled" when {

        "UserAnswers contain non-empty moreThanFiveJobs equal to true" in {
          when(appConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(appConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(cacheMap.getEntry[Boolean](eqTo(MoreThanFiveJobsId.toString))(any[Reads[Boolean]]))
            .thenReturn(Some(true))

          for {
            result <- route(application, testRequest).value

            _ = result.header.status mustBe OK
            _ = verify(usePrintAndPostFreOnlyView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
          } yield ()
        }

        "UserAnswers contain non-empty moreThanFiveJobs equal to false" in {
          when(appConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(appConfig.onlineJourneyShutterEnabled).thenReturn(true)
          when(cacheMap.getEntry[Boolean](eqTo(MoreThanFiveJobsId.toString))(any[Reads[Boolean]]))
            .thenReturn(Some(false))

          for {
            result <- route(application, testRequest).value

            _ = result.header.status mustBe OK
            _ = verify(useIformFreOnlyView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
          } yield ()
        }

        "UserAnswers does NOT contain moreThanFiveJobs field" in {
          when(appConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(appConfig.onlineJourneyShutterEnabled).thenReturn(true)

          for {
            result <- route(application, testRequest).value

            _ = result.header.status mustBe OK
            _ = verify(useIformFreOnlyView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
          } yield ()
        }
      }

      "freOnlyJourney is enabled but onlineJourneyShutter is disabled" when {

        "UserAnswers contain non-empty moreThanFiveJobs equal to true" in {
          when(appConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(appConfig.onlineJourneyShutterEnabled).thenReturn(false)
          when(cacheMap.getEntry[Boolean](eqTo(MoreThanFiveJobsId.toString))(any[Reads[Boolean]]))
            .thenReturn(Some(true))

          for {
            result <- route(application, testRequest).value

            _ = result.header.status mustBe OK
            _ = verify(usePrintAndPostFreOnlyView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
          } yield ()
        }

        "UserAnswers contain non-empty moreThanFiveJobs equal to false" in {
          when(appConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(appConfig.onlineJourneyShutterEnabled).thenReturn(false)
          when(cacheMap.getEntry[Boolean](eqTo(MoreThanFiveJobsId.toString))(any[Reads[Boolean]]))
            .thenReturn(Some(false))

          for {
            result <- route(application, testRequest).value

            _ = result.header.status mustBe OK
            _ = verify(useIformFreOnlyView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
          } yield ()
        }

        "UserAnswers does NOT contain moreThanFiveJobs field" in {
          when(appConfig.freOnlyJourneyEnabled).thenReturn(true)
          when(appConfig.onlineJourneyShutterEnabled).thenReturn(false)

          for {
            result <- route(application, testRequest).value

            _ = result.header.status mustBe OK
            _ = verify(useIformFreOnlyView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
          } yield ()
        }
      }

      "freOnlyJourney is disabled but onlineJourneyShutter is enabled" in {
        when(appConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(appConfig.onlineJourneyShutterEnabled).thenReturn(true)

        for {
          result <- route(application, testRequest).value

          _ = result.header.status mustBe OK
          _ = verify(usePrintAndPostDetailedView).apply(eqTo(claimingForList))(any[Request[_]], any[Messages])
        } yield ()
      }

      "both freOnlyJourney and onlineJourneyShutter are disabled" in {
        when(appConfig.freOnlyJourneyEnabled).thenReturn(false)
        when(appConfig.onlineJourneyShutterEnabled).thenReturn(false)

        for {
          result <- route(application, testRequest).value

          _ = result.header.status mustBe OK
          _ = verify(usePrintAndPostView).apply(eqTo(false), eqTo(false))(any[Request[_]], any[Messages])
        } yield ()
      }
    }
  }

}
