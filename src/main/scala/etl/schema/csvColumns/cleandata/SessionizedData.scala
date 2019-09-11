package etl.schema.csvColumns.cleandata

import etl.schema.csvColumns.ColumnsDefinition

object SessionizedData {
  val ip = ColumnsDefinition("clientIp", 0)
  val sessionizedData = ColumnsDefinition("sessionizedData", 1)
}
