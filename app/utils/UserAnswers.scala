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

package utils

import uk.gov.hmrc.http.cache.client.CacheMap
import identifiers._
import models._

class UserAnswers(val cacheMap: CacheMap) extends Enumerable.Implicits {
  def willPayTax: Option[Boolean] = cacheMap.getEntry[Boolean](WillPayTaxId.toString)

  def useOwnCar: Option[Boolean] = cacheMap.getEntry[Boolean](UseOwnCarId.toString)

  def useCompanyCar: Option[Boolean] = cacheMap.getEntry[Boolean](UseCompanyCarId.toString)

  def claimingMileage: Option[Boolean] = cacheMap.getEntry[Boolean](ClaimingMileageId.toString)

  def claimingFuel: Option[Boolean] = cacheMap.getEntry[Boolean](ClaimingFuelId.toString)

  def claimingFor: Option[List[ClaimingFor]] = cacheMap.getEntry[List[ClaimingFor]](ClaimingForId.toString)

  def paidTaxInRelevantYear: Option[Boolean] = cacheMap.getEntry[Boolean](PaidTaxInRelevantYearId.toString)

  def employerPaidBackExpenses: Option[Boolean] = cacheMap.getEntry[Boolean](EmployerPaidBackExpensesId.toString)

  def moreThanFiveJobs: Option[Boolean] = cacheMap.getEntry[Boolean](MoreThanFiveJobsId.toString)

  def claimingOverPayAsYouEarnThreshold: Option[Boolean] = cacheMap.getEntry[Boolean](ClaimingOverPayAsYouEarnThresholdId.toString)

  def registeredForSelfAssessment: Option[Boolean] = cacheMap.getEntry[Boolean](RegisteredForSelfAssessmentId.toString)

  def claimant: Option[Claimant] = cacheMap.getEntry[Claimant](ClaimantId.toString)

}
