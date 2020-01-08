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

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{nonEmptyText, set}
import models.{Claimant, ClaimingFor}

class ClaimingForFormProvider @Inject() extends Mappings {

  def apply(claimant: Claimant): Form[Set[ClaimingFor]] =
    Form(
      "value" -> claimingForMapping(claimant).verifying(s"claimingFor.$claimant.error.required", _.nonEmpty)
    )

  private def claimingForMapping(claimant: Claimant): Mapping[Set[ClaimingFor]] =
    set(nonEmptyText)
        .verifying(s"claimingFor.$claimant.error.required", _.forall(ClaimingFor.mappings.keySet.contains _))
        .transform(_.map(ClaimingFor.mappings.apply), _.map(_.toString))
}
