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

import controllers.routes
import models.Claimant.You
import play.twirl.api.Html
import uk.gov.hmrc.time.TaxYear
import views.behaviours.ViewBehaviours
import views.html.WillNotPayTaxView

class WillNotPayTaxViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "willNotPayTax.you"

  val application = applicationBuilder().build

  val view = application.injector.instanceOf[WillNotPayTaxView]

  def onwardRoute = routes.IndexController.onPageLoad

  def createView = view.apply(You, onwardRoute)(fakeRequest, messages)

  application.stop

  "WillNotPayTax view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    val link1 = s"""<a href="${frontendAppConfig.taxReliefForEmployeesUrl}">${messages(s"willNotPayTax.$claimant.guidance1")}</a>"""

    val link2 = Html(s"""<a href="${routes.RegisteredForSelfAssessmentController.onPageLoad}">${messages("willNotPayTax.link2", TaxYear.current.startYear.toString, TaxYear.current.finishYear.toString)}</a>""")

    behave like pageWithBodyText(
      createView,
      Html(link1).toString,
      Html(messages(s"willNotPayTax.$claimant.guidance2", link2)).toString
    )
  }
}
