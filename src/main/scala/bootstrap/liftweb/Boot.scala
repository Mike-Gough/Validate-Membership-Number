package bootstrap.liftweb

import au.gov.csc.rest.{Ping, Validate}
import net.liftweb.http.{LiftRules, NotFoundAsTemplate, ParsePath}
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
}