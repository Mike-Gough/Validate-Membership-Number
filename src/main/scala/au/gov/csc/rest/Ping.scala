package au.gov.csc.rest

import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._

object Ping extends RestHelper {

  serve( "api" / "ping" prefix {
    case _ => JObject(List(JField("Ping", JString("Pong"))))
  })
}