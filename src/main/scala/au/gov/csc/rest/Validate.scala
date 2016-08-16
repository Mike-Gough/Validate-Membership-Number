package au.gov.csc.rest

import bootstrap.liftweb.{MembershipNumber, MshpNumber}
import net.liftweb.http.{LiftRules, S}
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._

object Validate extends RestHelper {

  def init() : Unit = {
    LiftRules.statelessDispatch.append(Validate)
  }
  
  serve {
    case "api" :: "validate" :: q JsonGet _ =>
      val searchString = q ::: S.params("q")
      val mn: MembershipNumber = new MshpNumber(searchString.head)
      val list: List[JField] = mn.validate.map(a => JField("message", JString(a)))
      list.isEmpty match {
        case true =>
          val breakdown: List[JField] = mn match {
            case MshpNumber(prefix, number, pensionCode, suffix) =>
              List(JField("prefix", JString(prefix)),
                JField("number", JString(number)),
                JField("pensionCode", JString(pensionCode)),
                JField("suffix", JString(suffix)))
          }
          JObject(List(JField("id", JString(mn.number)), JField("valid", JString("true"))) ::: breakdown)
        case false => JObject(List(JField("id", JString(mn.number)), JField("valid", JString("false")), JField("error", JArray(list))))
      }
  }
}