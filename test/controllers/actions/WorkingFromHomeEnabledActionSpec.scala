package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import controllers.Assets.{OK, Ok, SEE_OTHER}
import controllers.routes
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, Result}
import play.mvc.Http.HeaderNames
import utils.MaterializerSupport

import scala.concurrent.Future

class WorkingFromHomeEnabledActionSpec extends SpecBase with MockitoSugar with ScalaFutures with MaterializerSupport {

  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val BLOCK_EXECUTED_MESSAGE = "block executed"

  class SetUp {
    val mockAppConfig = mock[FrontendAppConfig]

    class ActionUnderTest(controllerComponents: MessagesControllerComponents) extends WorkingFromHomeEnabledActionImpl(controllerComponents, mockAppConfig) {}

    val action = new ActionUnderTest(controllerComponents)

    val successfulOkayBlock: Request[AnyContent] => Future[Result] = { _ => Future.successful(Ok(BLOCK_EXECUTED_MESSAGE)) }
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
          result.header.headers.get(HeaderNames.LOCATION) mustBe Some(routes.IndexController.onPageLoad().url)
        }
      }
    }
  }

}
