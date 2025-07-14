import sbt._

object AppDependencies {
  val bootstrapVersion = "9.14.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                    % "2.6.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % "3.3.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"            % "12.7.0",
    "uk.gov.hmrc"       %% "tax-year"                              % "5.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
