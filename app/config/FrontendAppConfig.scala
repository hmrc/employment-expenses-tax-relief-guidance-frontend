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

package config

import controllers.routes
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.play.config.OptimizelyConfig
import uk.gov.hmrc.time.TaxYear

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration) {
  lazy val serviceName = configuration.get[String]("appName")

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "employmentexpensestaxreliefguidancefrontend"

  lazy val analyticsToken = configuration.get[String](s"google-analytics.token")
  lazy val analyticsHost = configuration.get[String](s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val loginUrl = configuration.get[String]("urls.login")
  lazy val loginContinueUrl = configuration.get[String]("urls.loginContinue")

  lazy val mongo_ttl = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val selfAssessmentTaxReturnsUrl = configuration.get[String]("urls.selfAssessmentTaxReturn")

  lazy val taxReliefForEmployeesUrl = configuration.get[String]("urls.taxReliefForEmployees")
  lazy val buisnessMileageFuelCostsUrl = configuration.get[String]("urls.buisnessMileageFuelCostsUrl")
  lazy val employeeExpensesUrl = configuration.get[String]("urls.employeeExpensesUrl")
  lazy val professionalSubscriptionsUrl = configuration.get[String]("urls.professionalSubscriptionsUrl")
  lazy val employeeExpensesClaimOnlineUrl = configuration.get[String]("urls.employeeExpensesClaimOnlineUrl")
  lazy val employeeExpensesClaimByPostUrl = configuration.get[String]("urls.employeeExpensesClaimByPostUrl")
  lazy val whoMustSendATaxReturnUrl = configuration.get[String]("urls.whoMustSendATaxReturnUrl")
  lazy val fileSelfAssessmentLoginUrl = configuration.get[String]("urls.fileSelfAssessmentLoginUrl")
  lazy val annualInvestmentAllowanceUrl = configuration.get[String]("urls.annualInvestmentAllowanceUrl")

  lazy val languageTranslationEnabled = configuration.get[Boolean]("microservice.services.features.welsh-translation")
  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))
  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val optimizelyConfig = new OptimizelyConfig(configuration)

  def earliestTaxYear = {
    TaxYear.current.back(4).startYear.toString
  }

}
