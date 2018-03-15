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

package forms

import forms.behaviours.BooleanFieldBehaviours
import models.Claimant.You
import models.ClaimYears
import models.ClaimYears.TwoYearsAgo
import play.api.data.FormError

class PaidTaxInRelevantYearFormProviderSpec extends BooleanFieldBehaviours {

  val claimant = You
  val requiredKey = s"paidTaxInRelevantYear.$claimant.error.required"

  val taxYear = ClaimYears.getTaxYear(TwoYearsAgo)
  val startYear = taxYear.startYear.toString
  val finishYear = taxYear.finishYear.toString

  val form = new PaidTaxInRelevantYearFormProvider()(claimant, startYear, finishYear)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, requiredKey, Seq(startYear, finishYear))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(startYear, finishYear))
    )
  }
}
