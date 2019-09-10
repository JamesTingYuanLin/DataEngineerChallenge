package utils

object StringUtils {
  def tokenize(s: String, delimiter: String) = {
    s.split(delimiter)
  }

  def removePort(ipWithPort: String): String = {
    ipWithPort.replace(ipWithPort.substring(ipWithPort.indexOf(":")), "")
  }
}
