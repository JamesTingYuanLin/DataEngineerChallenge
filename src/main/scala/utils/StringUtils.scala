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

  /**
   * Remove URL's parameters.
   *
   * @param urlWithParam
   * @return url without parameter
   */
  def removeUrlParameters(urlWithParam: String): String = {
    val urlParam = getUrlParameters(urlWithParam)
    if (urlParam.equals("")) { // no param in URL
      return urlWithParam
    }
    return urlWithParam.replace(urlParam, "")
  }

  /**
   * Find URL parameters.
   *
   * @param urlWithParam
   * @return URL param
   */
  def getUrlParameters(urlWithParam: String): String = {
    val indexOfParam = urlWithParam.indexOf("?")
    if (indexOfParam < 0) { // no param in URL
      return ""
    }
    return urlWithParam.substring(urlWithParam.indexOf("?"))
  }
}
