import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"     % "0.19.0-play26",
    "uk.gov.hmrc" %% "logback-json-logger"           % "4.6.0",
    "uk.gov.hmrc" %% "govuk-template"                % "5.44.0-play-26",
    "uk.gov.hmrc" %% "play-health"                   % "3.14.0-play-26",
    "uk.gov.hmrc" %% "play-ui"                       % "8.3.0-play-26",
    "uk.gov.hmrc" %% "http-caching-client"           % "9.0.0-play-26",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "0.2.0",
    "uk.gov.hmrc" %% "bootstrap-play-26"             % "1.1.0",
    "uk.gov.hmrc" %% "play-language"                 % "4.1.0",
    "uk.gov.hmrc" %% "tax-year"                      % "0.6.0"

  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-26"   % "1.1.0"  classifier "tests",
    "org.scalatest"           %% "scalatest"           % "3.0.8",
    "org.scalatestplus.play"  %% "scalatestplus-play"  % "3.1.2",
    "org.pegdown"             %  "pegdown"             % "1.6.0",
    "org.jsoup"               %  "jsoup"               % "1.12.1",
    "com.typesafe.play"       %% "play-test"           % PlayVersion.current,
    "org.mockito"             %  "mockito-all"         % "1.10.19",
    "org.scalacheck"          %% "scalacheck"          % "1.14.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
