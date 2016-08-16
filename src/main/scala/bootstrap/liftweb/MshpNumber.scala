package bootstrap.liftweb {

  import scala.util.{Failure, Success, Try}

  case class MshpNumber(number: String, prefix: String = "", pensionCode: String = "", suffix: String = "") extends MembershipNumber {
    var external_id = prefix + number + pensionCode + suffix

    override def validate: List[String] = {
      val (minLength, maxLength) = (4, 10)
      val patternScheme = "[A-Z]{2}".r
      val schemes = List("CS", "PS", "OS", "PG", "MS", "DF", "DB", "AD")
      val MembershipNumberPattern = "^(Z|P|A|N|R)?(\\d{4,10})(CS|PS|OS|PG|MS|DF|DB|AD)?([A-W]|X|X\\d{2})?$".r

      val isInvalidCase: PartialFunction[String, String] = {
        case x if x.toUpperCase() != x => "must be in upper case."
      }

      val isInvalidMinLength: PartialFunction[String, String] = {
        case x if x.length < minLength => f"must be greater than or equal to $minLength%1d characters in length."
      }

      val isInvalidMaxLength: PartialFunction[String, String] = {
        case x if x.length > maxLength => f"must be less than or equal to $maxLength%2d characters in length."
      }

      val isInvalidSchemeCode: PartialFunction[String, String] = {
        case x if !schemes.contains(patternScheme.findFirstIn(x).getOrElse("CS")) =>
          "does not contain a valid pension code suffix. Valid codes include " + schemes.mkString(", ") + "."
      }

      def validSchemeCodeForSource(id: String): Try[String] = {
        schemes.contains(patternScheme.findFirstIn(id).getOrElse("CS")) match {
          case true => Success(id)
          case false => id.matches(MembershipNumberPattern.regex) match {
            case true => id match {
              case MembershipNumberPattern(prefix, number, pension, suffix) =>
                prefix match {
                  case "A" | "N" | "R" => pension match {
                    case "MS" | "DF" | "DB" | "AD" => Success(id)
                    case _ => Failure(new IllegalArgumentException("Military Service Numbers cannot have a civilian pension code suffix."))
                  }
                  case "Z" | "P" => pension match {
                    case "MS" | "DF" | "DB" | "AD" => Success(id)
                    case _ => Failure(new IllegalArgumentException("Australian Government Service Numbers cannot have a military pension code suffix."))
                  }
                  case _ => Success(id)
                }
            }
            case false => Success(id)
          }
        }
      }

      val isInvalidFormat: PartialFunction[String, String] = {
        case x if !x.matches("^((Z|P)?\\d{4,8}((CS|PS|OS|PG)|((CS|PS|OS|PG)[A-W]))?)|((A|N|R)?\\d{4,10}((MS|DF|DB|AD)|((MS|DF|DB|AD)[A-W]))?)(X|X\\d{2})?$") =>
          "is not a valid Australian Government Service (AGS) or Military Service Number."
      }

      // Return validation failures
      val id: List[String] = List(external_id)
      List(id.collect(isInvalidCase), id.collect(isInvalidMinLength), id.collect(isInvalidMaxLength), id.collect(isInvalidSchemeCode), List(validSchemeCodeForSource(external_id)).filter(x => x.isFailure).map(y => y.failed.get.toString), id.collect(isInvalidFormat)).flatten
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