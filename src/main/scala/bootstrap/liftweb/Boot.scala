package bootstrap.liftweb

import au.gov.csc.rest.{Ping, Validate}
import net.liftweb.http._
import net.liftweb.sitemap.{Menu, SiteMap}

class Boot {

  def boot {

    LiftRules.addToPackages("au.gov.csc.rest")
    Ping.init()
    Validate.init()

    LiftRules.responseTransformers.append {
      case Customised(resp) => resp
      case resp => resp
    }

    LiftRules.setSiteMap(SiteMap(Menu.i("Home") / "index",
                                 Menu.i("Home") / "rest"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set( (r: Req) =>
      new Html5Properties(r.userAgent) )

  }
}