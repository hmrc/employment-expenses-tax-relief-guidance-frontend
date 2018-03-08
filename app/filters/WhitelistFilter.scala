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

package filters

import akka.stream.Materializer
import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.mvc._
import uk.gov.hmrc.whitelist._

import scala.concurrent.Future

class WhitelistFilter @Inject()(appConfig: FrontendAppConfig)(implicit val materializer: Materializer) extends Filter {

  override def mat = materializer

  val impl = new AkamaiWhitelistFilter {
    override def mat = materializer

    override val whitelist = appConfig.whitelistedIps

    override def destination = Call("GET", "https://www.gov.uk/")

    override def excludedPaths = appConfig.whitelistExcluded.map { path => Call("GET", path) }
  }

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    impl(f)(rh)
  }
}
