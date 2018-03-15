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

package controllers

import play.api.data.Form
import play.api.libs.json.{JsArray, JsBoolean, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.FakeNavigator
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.PaidTaxInRelevantYearFormProvider
import identifiers.{ClaimantId, PaidTaxInRelevantYearId, TaxYearsId}
import models.Claimant.You
import models.ClaimYears
import models.ClaimYears.{AnotherYear, LastYear, TwoYearsAgo}
import views.html.paidTaxInRelevantYear

class PaidTaxInRelevantYearControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  val claimant = You

  val taxYear = ClaimYears.getTaxYear(TwoYearsAgo)
  val startYear = taxYear.startYear.toString
  val finishYear = taxYear.finishYear.toString

  val formProvider = new PaidTaxInRelevantYearFormProvider()
  val form = formProvider(claimant, startYear, finishYear)

  val getValidPrecursorData = new FakeDataRetrievalAction(
    Some(
      CacheMap(
        cacheMapId,
        Map(
          ClaimantId.toString -> JsString(claimant.toString),
          TaxYearsId.toString -> JsArray(Seq(JsString(TwoYearsAgo.toString)))
        )
      )
    )
  )

  def controller(dataRetrievalAction: DataRetrievalAction = getValidPrecursorData) =
    new PaidTaxInRelevantYearController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl, new GetClaimantActionImpl, formProvider)

  def viewAsString(form: Form[_] = form) =
    paidTaxInRelevantYear(frontendAppConfig, form, claimant, startYear, finishYear)(fakeRequest, messages).toString

  "PaidTaxInRelevantYear Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        PaidTaxInRelevantYearId.toString -> JsBoolean(true),
        TaxYearsId.toString -> JsArray(Seq(JsString(TwoYearsAgo.toString))),
        ClaimantId.toString -> JsString(claimant.toString)
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      contentAsString(result) mustBe viewAsString(form.fill(true))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "true"))
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired when TaxYears has been answered with more than one answer" in {
      val invalidData = Map(
        PaidTaxInRelevantYearId.toString -> JsBoolean(true),
        TaxYearsId.toString -> JsArray(Seq(JsString(TwoYearsAgo.toString), JsString(LastYear.toString))),
        ClaimantId.toString -> JsString(claimant.toString)
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, invalidData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired when TaxYears has been answered with AnotherYear" in {
      val invalidData = Map(
        PaidTaxInRelevantYearId.toString -> JsBoolean(true),
        TaxYearsId.toString -> JsArray(Seq(JsString(AnotherYear.toString))),
        ClaimantId.toString -> JsString(claimant.toString)
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, invalidData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
