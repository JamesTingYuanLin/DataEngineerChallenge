package etl.schema.csvColumns.cleandata

import etl.schema.csvColumns.ColumnsDefinition

object TotalSessionTimeAndCountPerUser {
  val ip = ColumnsDefinition("clientIp", 0)
  val time = ColumnsDefinition("totalSessionTime", 1)
  val count = ColumnsDefinition("totalSessionCount", 2)
}
