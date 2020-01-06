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

package views

import play.api.data.Form
import controllers.routes
import forms.UseCompanyCarFormProvider
import models.Claimant.You
import models.UsingOwnCar
import views.behaviours.YesNoViewBehaviours
import views.html.UseCompanyCarView

class UseCompanyCarViewSpec extends YesNoViewBehaviours {

  val useOfOwnCar = UsingOwnCar

  val messageKeyPrefix = s"useCompanyCar.$claimant.$useOfOwnCar"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[UseCompanyCarView]

  val form = new UseCompanyCarFormProvider()(claimant, useOfOwnCar)

  def createView(form: Form[_]) = view.apply(form, You, useOfOwnCar)(fakeRequest, messages)

  "UseCompanyCar view" must {

    behave like normalPage(createView(form), messageKeyPrefix)

    behave like yesNoPage(createView, messageKeyPrefix, routes.UseCompanyCarController.onSubmit().url)

    behave like pageWithBackLink(createView(form))
  }

  application.stop
}
