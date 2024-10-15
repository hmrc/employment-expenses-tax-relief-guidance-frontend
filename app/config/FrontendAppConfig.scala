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

package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.time.TaxYear

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration) {

  lazy val serviceName: String = configuration.get[String]("appName")

  lazy val contactHost: String = configuration.get[String]("contact-frontend.host")
  lazy val contactFormServiceIdentifier: String = configuration.get[String]("contact-frontend.serviceId")

  lazy val mongo_ttl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val selfAssessmentTaxReturnsUrl: String = configuration.get[String]("urls.selfAssessmentTaxReturn")
  lazy val taxReliefForEmployeesUrl: String = configuration.get[String]("urls.taxReliefForEmployees")
  lazy val buisnessMileageFuelCostsUrl: String = configuration.get[String]("urls.buisnessMileageFuelCostsUrl")
  lazy val employeeExpensesUrl: String = configuration.get[String]("urls.employeeExpensesUrl")
  lazy val professionalSubscriptionsUrl: String = configuration.get[String]("urls.professionalSubscriptionsUrl")
  lazy val employeeExpensesClaimOnlineUrl: String = configuration.get[String]("urls.employeeExpensesClaimOnlineUrl")
  lazy val employeeExpensesClaimByPostUrl: String = configuration.get[String]("urls.employeeExpensesClaimByPostUrl")
  lazy val fileSelfAssessmentLoginUrl: String = configuration.get[String]("urls.fileSelfAssessmentLoginUrl")
  lazy val annualInvestmentAllowanceUrl: String = configuration.get[String]("urls.annualInvestmentAllowanceUrl")
  lazy val workingFromHomeExpensesUrl: String = configuration.get[String]("urls.workingFromHomeExpensesUrl")
  lazy val jobExpensesGuidanceUrl: String = configuration.get[String]("urls.jobExpensesGuidanceUrl")

  val workingFromHomeExpensesOnlyEnabled: Boolean = configuration.getOptional[Boolean]("workingFromHomeExpensesOnly.enabled").getOrElse(false)

  val mergedJourneyEnabled: Boolean = configuration.getOptional[Boolean]("mergedJourney.enabled").getOrElse(false)
  val onlineJourneyShutterEnabled: Boolean = configuration.getOptional[Boolean]("onlineJourneyShutter.enabled").getOrElse(false)
  val freOnlyJourneyEnabled: Boolean = configuration.getOptional[Boolean]("freOnlyJourney.enabled").getOrElse(false)

  lazy val claimingForCurrentYearBackButtonOverride: String = configuration.get[String]("claimingForCurrentYear.backButtonOverride.reference")
  lazy val registeredForSelfBackButtonOverride: String = configuration.get[String]("registeredForSelf.backButtonOverride.reference")

  def mergedJourneyUrl(wfh: Boolean, psubs: Boolean, fre: Boolean): String = s"${employeeExpensesUrl}/merged-journey-set-up?wfh=${wfh}&psubs=${psubs}&fre=${fre}"

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))

  def earliestTaxYear: String = {
    TaxYear.current.back(4).startYear.toString
  }

}
