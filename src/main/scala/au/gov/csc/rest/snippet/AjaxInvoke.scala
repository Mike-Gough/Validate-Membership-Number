package au.gov.csc.rest.snippet

import bootstrap.liftweb.{MembershipNumber, MshpNumber}
import net.liftweb.common.Loggable
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.{ValById}
import net.liftweb.http.js.JsCmds
import net.liftweb.json.DefaultFormats
import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq}

object AjaxInvoke extends Loggable {

  implicit val formats = DefaultFormats

  def textSuccess = "span [class+]" #> "text-muted"
  def textDanger = "span [class+]" #> "text-danger"

  def isValid(in: String) : NodeSeq = {
    val mn: MembershipNumber = MshpNumber(in)
    val result: String = mn.isValid match {
      case true => "Valid Membership Number"
      case false => "Membership Number " + mn.validate.head
    }
    val txt = <span>{result}</span>
    mn.isValid match {
      case true => textSuccess(txt)
      case false => textDanger(txt)
    }
  }

  def render = "button [onclick]" #>
    SHtml.ajaxCall(ValById("sn"), s => JsCmds.SetHtml("inject", isValid(s)))
}