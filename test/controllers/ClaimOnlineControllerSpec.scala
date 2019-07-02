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

package controllers

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, _}
import models.ClaimingFor._
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.UserAnswers
import views.html.claimOnline

import scala.concurrent.ExecutionContext.Implicits.global

class ClaimOnlineControllerSpec extends SpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction) =
    new ClaimOnlineController(frontendAppConfig, dataRetrievalAction, new DataRequiredActionImpl, controllerComponents)

  def viewAsString(eeEligible: Boolean) = claimOnline(frontendAppConfig, eeEligible)(fakeRequest, messages).toString

  "ClaimOnline Controller" must {

    "return OK for a GET if some data" in {
      val userAnswers = new UserAnswers(CacheMap(cacheMapId, Map("claimingFor" -> Json.toJson(Seq(UniformsClothingTools.string)))))

      val dataAction = new FakeDataRetrievalAction(Some(userAnswers.cacheMap))

      val result = controller(dataAction).onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(true)
    }

    "return SEE_OTHER for a GET if no data" in {
      val result = controller(getEmptyCacheMap).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
    }

    "return correct view for a GET when user eligible for employee expenses" in {
      val userAnswers = new UserAnswers(CacheMap(cacheMapId, Map("claimingFor" -> Json.toJson(Seq(UniformsClothingTools.string)))))

      val dataAction = new FakeDataRetrievalAction(Some(userAnswers.cacheMap))

      val result = controller(dataAction).onPageLoad(fakeRequest)

      contentAsString(result) mustBe viewAsString(true)
    }

    "return correct view for a GET when user not eligible for employee expenses" in {
      val userAnswers = new UserAnswers(CacheMap(cacheMapId, Map("claimingFor" -> Json.toJson(Seq(UniformsClothingTools.string, MileageFuel.string)))))

      val dataAction = new FakeDataRetrievalAction(Some(userAnswers.cacheMap))

      val result = controller(dataAction).onPageLoad(fakeRequest)

      contentAsString(result) mustBe viewAsString(false)
    }
  }
}
