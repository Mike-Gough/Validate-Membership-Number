package au.gov.csc.rest

import net.liftweb.http.LiftRules
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._

object Ping extends RestHelper {

  def init() : Unit = {
    LiftRules.statelessDispatch.append(Ping)
  }

  def ping: JObject = {
    JObject(List(JField("ping", JString("pong"))))
  }

  serve("api" / "ping" prefix {
    case _ => ping
  })
}