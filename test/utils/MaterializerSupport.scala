package utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait MaterializerSupport {

  implicit val system: ActorSystem = ActorSystem("Sys")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

}
