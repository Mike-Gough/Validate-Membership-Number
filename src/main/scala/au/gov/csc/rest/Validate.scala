package au.gov.csc.rest

import bootstrap.liftweb.{MembershipNumber, MshpNumber}
import net.liftweb.http.S
import net.liftweb.http.rest._
import scala.util.{Failure, Success}
import net.liftweb.json.JsonAST._

object Validate extends RestHelper {

  serve( "api" / "validate" prefix {
    case "isValid" :: q JsonGet _ =>
      val searchString = q ::: S.params("q")
      val mn: MembershipNumber = new MshpNumber(searchString.head)
      val list: List[JField] = mn.validate.map(a => JField("error", JString(a.failed.get.getMessage())))
      list.isEmpty match {
        case true => JObject(List(JField("valid", JString("true")))): JObject
        case false => JObject(List(JField("valid", JString("false"))) ::: list): JObject
      }
  })
}