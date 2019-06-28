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

import java.util.Base64

import com.google.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import play.api.i18n.Lang
import controllers.routes
import uk.gov.hmrc.play.config.{OptimizelyConfig, ServicesConfig}
import uk.gov.hmrc.time.TaxYear

@Singleton
class FrontendAppConfig @Inject() (override val runModeConfiguration: Configuration, environment: Environment) extends ServicesConfig {

  override protected def mode = environment.mode

  private def loadConfig(key: String) = runModeConfiguration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  private lazy val contactHost = runModeConfiguration.getString("contact-frontend.host").getOrElse("")
  private val contactFormServiceIdentifier = "employmentexpensestaxreliefguidancefrontend"

  lazy val analyticsToken = loadConfig(s"google-analytics.token")
  lazy val analyticsHost = loadConfig(s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val authUrl = baseUrl("auth")
  lazy val loginUrl = loadConfig("urls.login")
  lazy val loginContinueUrl = loadConfig("urls.loginContinue")

  lazy val selfAssessmentTaxReturnsUrl = loadConfig("urls.selfAssessmentTaxReturn")

  lazy val languageTranslationEnabled = runModeConfiguration.getBoolean("microservice.services.features.welsh-translation").getOrElse(true)
  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))
  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  lazy val whitelistedIps: Seq[String] = Some(
    new String(Base64.getDecoder.decode(runModeConfiguration.getString("microservice.services.whitelist.ips").getOrElse("")),
      "UTF-8")
  ).map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val whitelistExcluded = Some(
    new String(Base64.getDecoder.decode(runModeConfiguration.getString("microservice.services.whitelist.excluded").getOrElse("")), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val optimizelyConfig = new OptimizelyConfig(runModeConfiguration)

  def earliestTaxYear = {
    TaxYear.current.back(4).startYear.toString
  }

}
