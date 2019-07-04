import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.uglify.Import.{uglifyCompressOptions, _}
import com.typesafe.sbt.web.Import._
import net.ground5hark.sbt.concat.Import._
import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

trait MicroService {

  import uk.gov.hmrc._
  import DefaultBuildSettings._
  import play.sbt.routes.RoutesKeys
  import uk.gov.hmrc.SbtAutoBuildPlugin
  import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
  import uk.gov.hmrc.versioning.SbtGitVersioning

  val appName: String

  lazy val appDependencies: Seq[ModuleID] = ???
  lazy val plugins: Seq[Plugins] = Seq.empty
  lazy val playSettings: Seq[Setting[_]] = Seq.empty

  lazy val scoverageExcludePatterns = List(
    "<empty>",
    "Reverse.*",
    ".*filters.*",
    ".*handlers.*",
    ".*components.*",
    ".*models.*",
    ".*repositories.*",
    ".*BuildInfo.*",
    ".*javascript.*",
    ".*FrontendAuditConnector.*",
    ".*Routes.*",
    ".*GuiceInjector",
    ".*DataCacheConnector",
    ".*ControllerConfiguration",
    ".*LanguageSwitchController"
  )

  lazy val microservice = Project(appName, file("."))
    .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins: _*)
    .settings(playSettings: _*)
    .settings(majorVersion := 0)
    .settings(
      name := appName,
      RoutesKeys.routesImport ++= Seq("models._"),
      TwirlKeys.templateImports ++= Seq(
        "play.twirl.api.HtmlFormat",
        "play.twirl.api.HtmlFormat._",
        "uk.gov.hmrc.play.views.html.helpers._",
        "uk.gov.hmrc.play.views.html.layouts._",
        "controllers.routes._",
        "viewmodels._"
      ))
    .settings(
      ScoverageKeys.coverageExcludedFiles := scoverageExcludePatterns.mkString("", ";", ""),
      ScoverageKeys.coverageMinimum := 90,
      ScoverageKeys.coverageFailOnMinimum := true,
      ScoverageKeys.coverageHighlighting := true,
      parallelExecution in Test := false
    )
    .settings(scalaSettings: _*)
    .settings(publishingSettings: _*)
    .settings(inConfig(Test)(testSettings): _*)
    .settings(defaultSettings(): _*)
    .settings(
      scalacOptions ++= Seq("-feature"),
      libraryDependencies ++= appDependencies,
      retrieveManaged := true,
      evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
    )
    .settings(resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("emueller", "maven"),
      "hmrc-releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"
    ))
    .settings(
      // concatenate js
      Concat.groups := Seq(
        "javascripts/employmentexpensestaxreliefguidancefrontend-app.js" -> group(Seq("javascripts/show-hide-content.js", "javascripts/employmentexpensestaxreliefguidancefrontend.js"))
      ),
      // prevent removal of unused code which generates warning errors due to use of third-party libs
      uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
      pipelineStages := Seq(digest),
      // below line required to force asset pipeline to operate in dev rather than only prod
      pipelineStages in Assets := Seq(concat, uglify),
      // only compress files generated by concat
      includeFilter in uglify := GlobFilter("employmentexpensestaxreliefguidancefrontend-*.js")
    )

  lazy val testSettings: Seq[Def.Setting[_]] = Seq(
    fork        := true,
    javaOptions ++= Seq(
      "-Dconfig.resource=test.application.conf"
    )
  )
}


