import scala.util.{Failure, Success, Try}

package bootstrap.liftweb {

  case class MshpNumber(number: String, prefix: String = "", pensionCode: String = "", suffix: String = "") extends MembershipNumber {
    var external_id = prefix + number + pensionCode + suffix

    override def validate: List[Try[String]] = {
      val (minLength, maxLength) = (4, 10)

      def validCase(id: String): Try[String] = {
        id.toUpperCase() == id match {
          case true => Success(id)
          case false => Failure(new IllegalArgumentException("must be in upper case."))
        }
      }

      def validMinLength(id: String): Try[String] = {
        id.length >= minLength match {
          case true => Success(id)
          case false => Failure(new IllegalArgumentException(f"must be greater than or equal to $minLength%1d characters in length."))
        }
      }

      def validMaxLength(id: String): Try[String] = {
        id.length <= maxLength match {
          case true => Success(id)
          case false => Failure(new IllegalArgumentException(f"must be less than or equal to $maxLength%2d characters in length."))
        }
      }

      def validSchemeCode(id: String): Try[String] = {
        val patternScheme = "[A-Z]{2}".r
        val schemes = List("CS", "PS", "OS", "PG", "MS", "DF", "DB", "AD")
        schemes.contains(patternScheme.findFirstIn(id).getOrElse("CS")) match {
          case true => Success(id)
          case false => Failure(new IllegalArgumentException("does not contain a valid pension code suffix. Valid codes include " + schemes.mkString(", ") + "."))
        }
      }

      def validSchemeCodeForSource(id: String): Try[String] = {
        val MembershipNumberPattern = "^(Z|P|A|N|R)?(\\d{4,10})(CS|PS|OS|PG|MS|DF|DB|AD)?$".r
        val milSchemes = List("MS", "DF", "DB", "AD")
        val civSchemes = List("MS", "DF", "DB", "AD")
        val patternScheme = "[A-Z]{2}".r
        val schemes = List("CS", "PS", "OS", "PG", "MS", "DF", "DB", "AD")
        schemes.contains(patternScheme.findFirstIn(id).getOrElse("CS")) match {
          case true => Success(id)
          case false => id.matches(MembershipNumberPattern.regex) match {
            case true => id match {
              case MembershipNumberPattern(prefix, number, pension) =>
                prefix match {
                  case "A" => milSchemes.contains(pension) match {
                    case true => Success(id)
                    case false => Failure(new IllegalArgumentException("Military Service Numbers cannot have a civilian pension code suffix."))
                  }
                  case "N" => milSchemes.contains(pension) match {
                    case true => Success(id)
                    case false => Failure(new IllegalArgumentException("Military Service Numbers cannot have a civilian pension code suffix."))
                  }
                  case "R" => milSchemes.contains(pension) match {
                    case true => Success(id)
                    case false => Failure(new IllegalArgumentException("Military Service Numbers cannot have a civilian pension code suffix."))
                  }
                  case "Z" => civSchemes.contains(pension) match {
                    case true => Success(id)
                    case false => Failure(new IllegalArgumentException("Australian Government Service Numbers cannot have a military pension code suffix."))
                  }
                  case "P" => civSchemes.contains(pension) match {
                    case true => Success(id)
                    case false => Failure(new IllegalArgumentException("Australian Government Service Numbers cannot have a military pension code suffix."))
                  }
                  case _ => Success(id)
                }
            }
            case false => Success(id)
          }
        }

      }

      def validFormat(id: String): Try[String] = {
        id.matches("^((Z|P)?\\d{4,8}((CS|PS|OS|PG)|((CS|PS|OS|PG)[A-W]))?)|((A|N|R)?\\d{4,10}((MS|DF|DB|AD)|((MS|DF|DB|AD)[A-W]))?)(X|X\\d{2})?$") match {
          case true => Success(id)
          case false => Failure(new IllegalArgumentException("is not a valid Australian Government Service (AGS) or Military Service Number."))
        }
      }

      // Return validation failures
      List(validCase(external_id), validMinLength(external_id), validMaxLength(external_id), validSchemeCode(external_id), validSchemeCodeForSource(external_id), validFormat(external_id)).filter(x => x.isFailure)
    }
  }

  object MshpNumber {
    // match Regex for format validation and decomposing
    val MembershipNumberPattern = "^(Z|P|A|N|R)?(\\d{4,10})(CS|PS|OS|PG|MS|DF|DB|AD)?([A-W]|X|X\\d{2})?$".r

    def unapply(id: MembershipNumber): Option[(String, String, String, String)] = {
      id.toString() match {
        case MembershipNumberPattern(prefix, number, pension, suffix) =>
          if (id.isValid)
            Some((prefix, number, pension, suffix))
          else
            None
        case _ => None
      }
    }
  }

}