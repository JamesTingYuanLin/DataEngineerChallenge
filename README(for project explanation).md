*The entry point(main method) is app.Test (/src/main/scala/app/Test.scala)

/----- Answer to the 4 questions  -----/

1. Sessionize the web log by IP. Sessionize = aggregrate all page hits by visitor/IP during a session. https://en.wikipedia.org/wiki/Session_(web_analytics)
  Ans: in file "sessionizedData"
2. Determine the average session time
  Ans: in file "avgSessionTime"
3. Determine unique URL visits per session. To clarify, count a hit to a unique URL only once per session.
  Ans: in file "sessionizedData"
4. Find the most engaged users, ie the IPs with the longest session times
  Ans: The first line of file "totalSessionTimeAndCountPerUser"
  
/---------------------------------------/

/----- How to use & project structure -----/

*Version info:
    Spark: 1.6
    Scala: 2.10.7
    Java: JDK 1.7.0_80. 
    
*Development tools: 
    IntelliJ, 
    Sbt, Git, Github, 
    Virtual box, cloudera(quickstart-vm-5.13.0-0-virtualbox).

You can import the build.sbt into a sbt project.

/---------------------------------------/

/----- Project structure -----/

root dir-
  -src/ (source code)
    -main/
      -scala/ (Scala source code)
        -algorithm (Sessionize algorithm)
        -app (data transformation, main method)
        -etl (for data cleaning)
          -schema
            -csvColumns
              -cleandata
              -rawdata
        -utils
    -test/
  -data/ input and output data.
    -output
    
/---------------------------------------/

 /----- How to use & project structure -----/
 
 1. Pack hold project (I use IntelliJ to do that, Build->Build Artifacts->Build)
 2. submit a spark Job.
        spark-submit --class app.Test yourpath/Paypay.jar inputFilePath outputFilePath
            example:
                spark-submit --class app.Test ~/Paypay.jar "/tmp/james/rawdata/*" "/tmp/james/cleandata/"
    Note. You can add more option for spark running mode, e.g. cluster mode...
          I don't use here because here is only on machine to run spark.
 
 script FYI:
    hadoop fs -rm -r /tmp/james/cleandata/*
    spark-submit --class app.Test ~/Paypay.jar "/tmp/james/rawdata/*" "/tmp/james/cleandata/"
    
/---------------------------------------/

/----Illustration of Data format and transformation----/

* Output file format:
    1. sortByIpAndTime:
        temp result, sorted by both client's IP and request time. This file can be used to exam correctness.
            schema: client_IP, time, url
            example: ((117.241.247.142,2015-07-22T10:49:45.954291Z),https://paytm.com:443/shop/summary/1116596752)
    
    2. sessionizedData: (also as the answer of question 1, 3)
        Sessionized data, grouped by client IP. Record Each user's sessions, including startTime, endTime, duration, uniqueUrlCount and totalSessionCount. (in millisecond)
            schema: client_IP, sessionsInfo
            sessionInfo format: (IP,session1StartTime@session1EndTime@session1Duration?uniqueUrlCount#session2StartTime@session2EndTime@session2Duration??uniqueUrlCount#...$sessionCount)
            example: 117.241.247.142,2015-07-22T10:30:57.432Z@2015-07-22T10:48:15.706Z@1038274?10#2015-07-22T10:49:09.616Z@2015-07-22T11:04:48.508Z@938892?10$2
    3. avgSessionTime: 
        session statistic (also as the answer of question 2)
            example:
                Total session time: 110869458 seconds
                Total session count: 110838
                Avg session time: 1000.2838196286472 seconds

    4. totalSessionTimeAndCountPerUser:
        Count the session time and count by each user. And sort the result by totalSessionTime. (the first line is the answer of question 4)
            schema: client_IP, totalSessionTime(in millisecond) ,totalSessionCount 
            example: 117.241.247.142,1977166,2