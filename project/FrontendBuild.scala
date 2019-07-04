import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "employment-expenses-tax-relief-guidance-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  val compile: Seq[ModuleID] = Seq(
    ws,
    "org.reactivemongo" %% "play2-reactivemongo"            % "0.18.0-play26",
    "uk.gov.hmrc" %% "logback-json-logger"           % "4.4.0",
    "uk.gov.hmrc" %% "govuk-template"                % "5.33.0-play-26",
    "uk.gov.hmrc" %% "play-health"                   % "3.14.0-play-26",
    "uk.gov.hmrc" %% "play-ui"                       % "7.40.0-play-26",
    "uk.gov.hmrc" %% "http-caching-client"           % "8.4.0-play-25",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "0.2.0",
    "uk.gov.hmrc" %% "bootstrap-play-26"             % "0.37.0",
    "uk.gov.hmrc" %% "play-language"                 % "3.4.0",
    "uk.gov.hmrc" %% "play-whitelist-filter"         % "2.0.0",
    "uk.gov.hmrc" %% "tax-year"                      % "0.5.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test: Seq[ModuleID] = Seq(
        "org.scalatest"           %% "scalatest"           % "3.0.4",
        "org.scalatestplus.play"  %% "scalatestplus-play"  % "2.0.1",
        "org.pegdown"             %  "pegdown"             % "1.6.0",
        "org.jsoup"               %  "jsoup"               % "1.10.3",
        "com.typesafe.play"       %% "play-test"           % PlayVersion.current,
        "org.mockito"             %  "mockito-all"         % "1.10.19",
        "org.scalacheck"          %% "scalacheck"          % "1.13.4"
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
