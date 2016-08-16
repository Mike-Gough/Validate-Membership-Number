package bootstrap.liftweb {

  case class MshpNumber(number: String, prefix: String = "", pensionCode: String = "", suffix: String = "") extends MembershipNumber {
    var external_id = prefix + number + pensionCode + suffix

    override def validate: List[String] = {
      val (minLength, maxLength, agsLength) = (4, 10, 8)
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

      def validSchemeCodeForSource(id: String): Option[String] = {
        schemes.contains(patternScheme.findFirstIn(id).getOrElse("CS")) match {
          case true => None
          case false => id.matches(MembershipNumberPattern.regex) match {
            case true => id match {
              case MembershipNumberPattern(prefix, number, pension, suffix) =>
                prefix match {
                  case "A" | "N" | "R" => pension match {
                    case "MS" | "DF" | "DB" | "AD" => None
                    case _ => Some("Military Service Numbers cannot have a civilian pension code suffix.")
                  }
                  case "Z" | "P" => pension match {
                    case "MS" | "DF" | "DB" | "AD" => None
                    case _ => Some("Australian Government Service Numbers cannot have a military pension code suffix.")
                  }
                  case _ => None
                }
            }
            case false => None
          }
        }
      }

      val isInvalidSchemeCodeForSource: PartialFunction[String, String] = {
        case x if validSchemeCodeForSource(x).getOrElse("None") != "None" =>
          validSchemeCodeForSource(x).get
      }

      val isInvalidFormat: PartialFunction[String, String] = {
        case x if !x.matches("^((Z|P)?\\d{4,8}((CS|PS|OS|PG)|((CS|PS|OS|PG)[A-W]))?)|((A|N|R)?\\d{4,10}((MS|DF|DB|AD)|((MS|DF|DB|AD)[A-W]))?)(X|X\\d{2})?$") =>
          "is not a valid Australian Government Service (AGS) or Military Service Number."
      }

      def isInvalidAGSFormatModulo(id: String): Option[String] = {
        if (List(id).collect(isInvalidFormat).isEmpty) {
          id match {
            case MembershipNumberPattern(prefix, number, pension, suffix) =>
              prefix match {
                case "A" | "N" | "R" | "P" | "Z" => None
                case _ =>
                  if (number.length() != agsLength) {
                    Some("must contain $agsLength%1d digits to be a valid Australian Government Service Number")
                  } else {
                    var result: Int = 0
                    val multiplyBy: List[Int] = List(7, 9, 10, 5, 8, 4, 2, 1)
                    (number.toList.map(x => x.asDigit), multiplyBy).zipped.foreach((x, y) => result += x * y)
                    result % 11 match {
                      case 0 => None
                      case _ => Some("is not a valid Australian Government Service (AGS) Number")
                    }
                  }
              }
          }
        } else {
          None
        }
      }

      val isInvalidAGSFormat: PartialFunction[String, String] = {
        case x if isInvalidAGSFormatModulo(x).getOrElse("None") != "None" =>
          isInvalidAGSFormatModulo(x).get
      }

      // Return validation failures
      val id: List[String] = List(external_id)
      List(id.collect(isInvalidCase), id.collect(isInvalidMinLength), id.collect(isInvalidMaxLength), id.collect(isInvalidSchemeCode), id.collect(isInvalidSchemeCodeForSource), id.collect(isInvalidAGSFormat), id.collect(isInvalidFormat)).flatten
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