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

package views

import play.api.data.Form
import controllers.routes
import forms.UseCompanyCarFormProvider
import models.Claimant.You
import models.UsingOwnCar
import views.behaviours.YesNoViewBehaviours
import views.html.useCompanyCar

class UseCompanyCarViewSpec extends YesNoViewBehaviours {

  val claimant = You
  val useOfOwnCar = UsingOwnCar

  val messageKeyPrefix = s"useCompanyCar.$claimant.$useOfOwnCar"

  val form = new UseCompanyCarFormProvider()(claimant, useOfOwnCar)

  def createView = () => useCompanyCar(frontendAppConfig, form, claimant, useOfOwnCar)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => useCompanyCar(frontendAppConfig, form, claimant, useOfOwnCar)(fakeRequest, messages)

  "UseCompanyCar view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.UseCompanyCarController.onSubmit().url)

    behave like pageWithBackLink(createView)
  }
}
