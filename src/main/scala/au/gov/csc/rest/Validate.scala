package au.gov.csc.rest

import bootstrap.liftweb.{MembershipNumber, MshpNumber}
import net.liftweb.http.S
import net.liftweb.http.rest._
import scala.util.{Failure, Success}
import net.liftweb.json.JsonAST._

object Validate extends RestHelper {

  serve {
    case "api" :: "validate" :: q JsonGet _ =>
      val searchString = q ::: S.params("q")
      val mn: MembershipNumber = new MshpNumber(searchString.head)
      val list: List[JField] = mn.validate.map(a => JField("message", JString(a)))
      list.isEmpty match {
        case true => JObject(List(JField("id", JString(mn.number)), JField("valid", JString("true"))))
        case false => JObject(List(JField("id", JString(mn.number)), JField("valid", JString("false")), JField("error", JArray(list))))
      }
  }
}