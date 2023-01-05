import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-play-28"            % "0.73.0",
    "uk.gov.hmrc"         %% "http-caching-client"           % "9.6.0-play-28",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping" % "1.11.0-play-28",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-28"    % "5.24.0",
    "uk.gov.hmrc"         %% "play-frontend-hmrc"            % "3.34.0-play-28",
    "uk.gov.hmrc"         %% "tax-year"                      % "1.3.0",
    "com.typesafe.play"   %% "play-iteratees"                % "2.6.1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus"      %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.scalatestplus"      %% "scalatestplus-mockito"    % "1.0.0-M2",
    "org.scalatestplus.play" %% "scalatestplus-play"       % "5.1.0",
    "org.pegdown"             %  "pegdown"                 % "1.6.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28" % "0.73.0",
    "org.jsoup"               %  "jsoup"                   % "1.13.1",
    "com.typesafe.play"       %% "play-test"               % PlayVersion.current,
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
