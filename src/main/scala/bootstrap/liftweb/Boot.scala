package bootstrap.liftweb

import au.gov.csc.rest.{Ping, Validate}
import net.liftweb.http._
import net.liftweb.sitemap.{Menu, SiteMap}
import net.liftweb.util.NamedPF

class Boot {

  def boot {
    LiftRules.addToPackages("au.gov.csc.rest")
    LiftRules.setSiteMap(SiteMap(Menu("Home") / "index"))

    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) =>
        NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

    LiftRules.dispatch.append(Ping)
    LiftRules.dispatch.append(Validate)
  }

  // Use HTML5 for rendering
  LiftRules.htmlProperties.default.set( (r: Req) =>
    new Html5Properties(r.userAgent) )
}