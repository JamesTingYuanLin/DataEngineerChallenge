package utils

object StringUtils {
  def removePort(ipWithPort: String): String = {
    ipWithPort.replace(ipWithPort.substring(ipWithPort.indexOf(":")), "")
  }
}
