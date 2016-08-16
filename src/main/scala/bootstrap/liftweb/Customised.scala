package bootstrap.liftweb

import net.liftweb.common.Box
import net.liftweb.http.{LiftResponse, S, Templates}

object Customised {

  val definedPages = 400 :: 403 :: 500 :: Nil

  def unapply(resp: LiftResponse) : Option[LiftResponse] =
    definedPages.find(_ == resp.toResponse.code).flatMap(toResponse)
  def toResponse(status: Int) : Box[LiftResponse] =

    for {
      session <- S.session
      req <- S.request
      template = Templates(status.toString :: Nil)
      response <- session.processTemplate(template, req, req.path, status)
    } yield response

}