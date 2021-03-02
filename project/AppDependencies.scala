import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "simple-reactivemongo"          % "7.30.0-play-26",
    "uk.gov.hmrc"         %% "logback-json-logger"           % "4.8.0",
    "uk.gov.hmrc"         %% "govuk-template"                % "5.55.0-play-26",
    "uk.gov.hmrc"         %% "play-health"                   % "3.15.0-play-26",
    "uk.gov.hmrc"         %% "play-ui"                       % "8.21.0-play-26",
    "uk.gov.hmrc"         %% "http-caching-client"           % "9.1.0-play-26",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping" % "1.2.0-play-26",
    "uk.gov.hmrc"         %% "bootstrap-play-26"             % "1.13.0",
    "uk.gov.hmrc"         %% "play-language"                 % "4.3.0-play-26",
    "uk.gov.hmrc"         %% "tax-year"                      % "1.1.0",
    "com.typesafe.play"   %% "play-iteratees"                % "2.6.1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"             % "3.0.8",
    "org.scalatestplus.play"  %% "scalatestplus-play"    % "3.1.3",
    "org.pegdown"             %  "pegdown"               % "1.6.0",
    "org.jsoup"               %  "jsoup"                 % "1.13.1",
    "com.typesafe.play"       %% "play-test"             % PlayVersion.current,
    "org.mockito"             %  "mockito-all"           % "1.10.19",
    "org.scalacheck"          %% "scalacheck"            % "1.14.3"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
