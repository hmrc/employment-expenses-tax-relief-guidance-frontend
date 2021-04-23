import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "simple-reactivemongo"          % "8.0.0-play-27",
    "uk.gov.hmrc"         %% "logback-json-logger"           % "5.1.0",
    "uk.gov.hmrc"         %% "govuk-template"                % "5.65.0-play-27",
    "uk.gov.hmrc"         %% "play-health"                   % "3.16.0-play-27",
    "uk.gov.hmrc"         %% "play-ui"                       % "9.0.0-play-27",
    "uk.gov.hmrc"         %% "http-caching-client"           % "9.3.0-play-27",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping" % "1.6.0-play-27",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-27"    % "4.2.0",
    "uk.gov.hmrc"         %% "play-language"                 % "4.11.0-play-27",
    "uk.gov.hmrc"         %% "tax-year"                      % "1.3.0",
    "com.typesafe.play"   %% "play-iteratees"                % "2.6.1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"    % "3.1.3",
    "org.pegdown"             %  "pegdown"               % "1.6.0",
    "org.jsoup"               %  "jsoup"                 % "1.13.1",
    "com.typesafe.play"       %% "play-test"             % PlayVersion.current,
    "org.mockito"             %  "mockito-all"           % "1.10.19",
    "org.scalacheck"          %% "scalacheck"            % "1.14.3"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
