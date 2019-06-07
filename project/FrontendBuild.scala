import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {

  val appName = "employment-expenses-tax-relief-guidance-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "play-reactivemongo"            % "6.7.0",
    "uk.gov.hmrc" %% "logback-json-logger"           % "4.4.0",
    "uk.gov.hmrc" %% "govuk-template"                % "5.30.0-play-25",
    "uk.gov.hmrc" %% "play-health"                   % "3.14.0-play-25",
    "uk.gov.hmrc" %% "play-ui"                       % "7.40.0-play-25",
    "uk.gov.hmrc" %% "http-caching-client"           % "8.4.0-play-25",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "0.2.0",
    "uk.gov.hmrc" %% "bootstrap-play-25"             % "4.12.0",
    "uk.gov.hmrc" %% "play-language"                 % "3.4.0",
    "uk.gov.hmrc" %% "play-whitelist-filter"         % "2.0.0",
    "uk.gov.hmrc" %% "tax-year"                      % "0.5.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest"          %% "scalatest"          % "3.0.4" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope,
        "org.pegdown"            % "pegdown"             % "1.6.0" % scope,
        "org.jsoup"              % "jsoup"               % "1.10.3" % scope,
        "com.typesafe.play"      %% "play-test"          % PlayVersion.current % scope,
        "org.mockito"            % "mockito-all"         % "1.10.19" % scope,
        "org.scalacheck"         %% "scalacheck"         % "1.13.4" % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
