package utils

object StringUtils {
  /**
   * Report port from a IP address.
   * @param ipWithPort
   * @return ip without port
   */
  def removePort(ipWithPort: String): String = {
    ipWithPort.replace(ipWithPort.substring(ipWithPort.indexOf(":")), "")
  }
}
