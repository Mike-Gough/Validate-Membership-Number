package bootstrap.liftweb

import au.gov.csc.rest.{Ping, Validate}
import net.liftweb.common.Box
import net.liftweb.http._
import net.liftweb.sitemap.{Menu, SiteMap}
import net.liftweb.util.NamedPF

class Boot {

  def boot {
    def sitemap(): SiteMap = SiteMap(Menu.i("Home") / "index",
                                     Menu.i("Home") / "api")

    LiftRules.addToPackages("au.gov.csc.rest")
    LiftRules.setSiteMap(sitemap())

    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) =>
        NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

    def my403 : Box[LiftResponse] =
      for {
        session <- S.session
        req <- S.request
        template = Templates("403" :: Nil)
        response <- session.processTemplate(template, req, req.path, 403)
      } yield response

    LiftRules.responseTransformers.append {
      case resp if resp.toResponse.code == 403 => my403 openOr resp
      case resp => resp
    }

    LiftRules.dispatch.append(Ping)
    LiftRules.dispatch.append(Validate)
  }

  // Use HTML5 for rendering
  LiftRules.htmlProperties.default.set( (r: Req) =>
    new Html5Properties(r.userAgent) )
}