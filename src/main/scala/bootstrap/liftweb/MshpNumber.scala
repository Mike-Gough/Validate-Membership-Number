package bootstrap.liftweb {

  case class MshpNumber(number: String, prefix: String = "", pensionCode: String = "", suffix: String = "") extends MembershipNumber {
    var external_id = prefix + number + pensionCode + suffix

    override def validate: List[String] = {
      val (minLength, maxLength) = (4, 10)

      def validCase(id: String): Option[String] = {
        id.toUpperCase() == id match {
          case true => None
          case false => Some("must be in upper case.")
        }
      }

      def validMinLength(id: String): Option[String] = {
        id.length >= minLength match {
          case true => None
          case false => Some(f"must be greater than or equal to $minLength%1d characters in length.")
        }
      }

      def validMaxLength(id: String): Option[String] = {
        id.length <= maxLength match {
          case true => None
          case false => Some(f"must be less than or equal to $maxLength%2d characters in length.")
        }
      }

      def validSchemeCode(id: String): Option[String] = {
        val patternScheme = "[A-Z]{2}".r
        val schemes = List("CS", "PS", "OS", "PG", "MS", "DF", "DB", "AD")
        schemes.contains(patternScheme.findFirstIn(id).getOrElse("CS")) match {
          case true => None
          case false => Some("does not contain a valid pension code suffix. Valid codes include " + schemes.mkString(", ") + ".")
        }
      }

      def validSchemeCodeForSource(id: String): Option[String] = {
        val MembershipNumberPattern = "^(Z|P|A|N|R)?(\\d{4,10})(CS|PS|OS|PG|MS|DF|DB|AD)?$".r
        val patternScheme = "[A-Z]{2}".r
        val schemes = List("CS", "PS", "OS", "PG", "MS", "DF", "DB", "AD")
        schemes.contains(patternScheme.findFirstIn(id).getOrElse("CS")) match {
          case true => None
          case false => id.matches(MembershipNumberPattern.regex) match {
            case true => id match {
              case MembershipNumberPattern(prefix, number, pension) =>
                prefix match {
                  case _ if List("A", "N", "R").contains(prefix) => List("MS", "DF", "DB", "AD").contains(pension) match {
                    case true => None
                    case false => Some("Military Service Numbers cannot have a civilian pension code suffix.")
                  }
                  case _ if List("Z", "P").contains(prefix) => List("MS", "DF", "DB", "AD").contains(pension) match {
                    case true => None
                    case false => Some("Military Service Numbers cannot have a civilian pension code suffix.")
                  }
                  case _ => None
                }
            }
            case false => None
          }
        }
      }

      def validFormat(id: String): Option[String] = {
        id.matches("^((Z|P)?\\d{4,8}((CS|PS|OS|PG)|((CS|PS|OS|PG)[A-W]))?)|((A|N|R)?\\d{4,10}((MS|DF|DB|AD)|((MS|DF|DB|AD)[A-W]))?)(X|X\\d{2})?$") match {
          case true => None
          case false => Some("is not a valid Australian Government Service (AGS) or Military Service Number.")
        }
      }

      // Return validation failures
      List(validCase(external_id), validMinLength(external_id), validMaxLength(external_id), validSchemeCode(external_id), validSchemeCodeForSource(external_id), validFormat(external_id)).flatten.map(y => y.toString())
    }
  }

  object MshpNumber {
    // match Regex for format validation and decomposing
    val MembershipNumberPattern = "^(Z|P|A|N|R)?(\\d{4,10})(CS|PS|OS|PG|MS|DF|DB|AD)?([A-W]|X|X\\d{2})?$".r

    def unapply(id: MembershipNumber): Option[(String, String, String, String)] = {
      id.toString() match {
        case MembershipNumberPattern(prefix, number, pension, suffix) if id.isValid =>
            Some((prefix, number, pension, suffix))
        case _ => None
      }
    }
  }
}