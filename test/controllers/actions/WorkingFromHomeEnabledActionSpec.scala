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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.HttpEntity
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, ResponseHeader, Result}
import play.mvc.Http.HeaderNames
import utils.MaterializerSupport

import scala.concurrent.{ExecutionContext, Future}

class WorkingFromHomeEnabledActionSpec extends SpecBase with MockitoSugar with ScalaFutures with MaterializerSupport {

  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val BLOCK_EXECUTED_MESSAGE = ""

  class SetUp {
    val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

    class ActionUnderTest(controllerComponents: MessagesControllerComponents) extends WorkingFromHomeEnabledActionImpl(controllerComponents, mockAppConfig) {}

    val action = new ActionUnderTest(controllerComponents)

    val successfulOkayBlock: Request[AnyContent] => Future[Result] = { _ => Future.successful(Result(ResponseHeader(OK), HttpEntity.NoEntity)) }
  }

  "The ActionBuilder" when {
    "enabled in application configuration" should {
      "allow continuation and execute action block" in new SetUp {
        when(mockAppConfig.workingFromHomeExpensesOnlyEnabled).thenReturn(true)

        val futureResult: Future[Result] = action.invokeBlock(fakeRequest, successfulOkayBlock)

        whenReady(futureResult) { result =>
          result.header.status mustBe OK
          result.body.consumeData.futureValue.utf8String mustBe BLOCK_EXECUTED_MESSAGE
        }
      }
    }

    "disabled in application configuration" should {
      "redirect instead of executing action block" in new SetUp {
        when(mockAppConfig.workingFromHomeExpensesOnlyEnabled).thenReturn(false)

        val futureResult: Future[Result] = action.invokeBlock(fakeRequest, successfulOkayBlock)

        whenReady(futureResult) { result =>
          result.header.status mustBe SEE_OTHER
          result.header.headers.get(HeaderNames.LOCATION) mustBe Some(routes.IndexController.onPageLoad.url)
        }
      }
    }
  }

}
