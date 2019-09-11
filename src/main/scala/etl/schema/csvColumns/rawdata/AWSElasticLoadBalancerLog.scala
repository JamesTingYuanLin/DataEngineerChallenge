package etl.schema.csvColumns.rawdata

import etl.schema.csvColumns.ColumnsDefinition

object AWSElasticLoadBalancerLog {
  val clientIpWithPort = ColumnsDefinition("clientIpWithPort", 2)
  val time = ColumnsDefinition("time", 0)
  val url = ColumnsDefinition("url", 12)
}
