/*
 * Copyright 2019 HM Revenue & Customs
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

package base

import config.FrontendAppConfig
import controllers.actions.{DataRequiredAction, DataRequiredActionImpl, DataRequiredActionSpec, DataRetrievalAction, DataRetrievalActionImpl, DataRetrievalActionSpec, FakeDataRetrievalAction, GetClaimantAction, GetClaimantActionImpl}
import identifiers.ClaimantId
import models.Claimant
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.{Injector, bind}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsString
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.UserAnswers


trait SpecBase extends PlaySpec with GuiceOneAppPerSuite {

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def controllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  def fakeRequest = FakeRequest("", "")

  val cacheMapId = "id"

  def emptyCacheMap = CacheMap(cacheMapId, Map())

  def emptyUserAnswers = new UserAnswers(emptyCacheMap)

  def getEmptyCacheMap = new FakeDataRetrievalAction(Some(emptyCacheMap))

  def dontGetAnyData = new FakeDataRetrievalAction(None)

  def getCacheMapWithClaimant(claimant: Claimant) =
    new FakeDataRetrievalAction(
      Some(CacheMap(cacheMapId, Map(ClaimantId.toString -> JsString(claimant.toString))))
    )
  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(Some(emptyCacheMap))),
        bind[GetClaimantAction].to[GetClaimantActionImpl])

}
