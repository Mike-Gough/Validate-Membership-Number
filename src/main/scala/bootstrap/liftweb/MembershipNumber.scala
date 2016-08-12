import scala.util.Try

package bootstrap.liftweb {

  trait MembershipNumber {
    val prefix: String
    val number: String
    val pensionCode: String
    val suffix: String

    override def toString: String = (prefix + number + pensionCode + suffix).toUpperCase()

    def validate: List[Try[String]]

    def isValid: Boolean = {
      validate.forall(x => x.isSuccess)
    }
  }
}