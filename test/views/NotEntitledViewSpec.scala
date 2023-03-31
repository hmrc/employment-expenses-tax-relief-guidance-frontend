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

package views

import play.twirl.api.HtmlFormat
import views.behaviours.NewViewBehaviours
import views.html.NotEntitledView

class NotEntitledViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = s"notEntitled.$claimant"

  val application = applicationBuilder().build()

  val view = application.injector.instanceOf[NotEntitledView]

  def createView: HtmlFormat.Appendable = view.apply(claimant)(fakeRequest, messages)

  val taxReliefForEmployeesUrl = frontendAppConfig.taxReliefForEmployeesUrl

  "NotEntitled view" must {
    behave like normalPage(createView, messageKeyPrefix)
    behave like pageWithBackLink(createView)
    behave like pageWithHyperLink(createView, taxReliefForEmployeesUrl)
  }

  application.stop()
}
