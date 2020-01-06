/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.actions._
import controllers.routes
import identifiers.ClaimantId
import models.Claimant
import models.Claimant.You
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.JsString
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.UserAnswers


trait SpecBase extends PlaySpec with GuiceOneAppPerSuite {

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def controllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  def sessionExpiredUrl: String = routes.SessionExpiredController.onPageLoad().url

  val cacheMapId = "id"

  val claimant: Claimant.You.type = You

  def claimantIdCacheMap = CacheMap(cacheMapId, Map(ClaimantId.toString -> JsString(claimant.toString)))

  def emptyUserAnswers = new UserAnswers(claimantIdCacheMap)

  def getclaimantIdCacheMap = new FakeDataRetrievalAction(Some(claimantIdCacheMap), controllerComponents)

  def dontGetAnyData = new FakeDataRetrievalAction(None, controllerComponents)

  def getCacheMapWithClaimant(claimant: Claimant) =
    new FakeDataRetrievalAction(
      Some(CacheMap(cacheMapId, Map(ClaimantId.toString -> JsString(claimant.toString)))),
      controllerComponents
    )
  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(cacheMap: Option[CacheMap] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(cacheMap, controllerComponents)),
        bind[GetClaimantAction].to[GetClaimantActionImpl]
      )
}
