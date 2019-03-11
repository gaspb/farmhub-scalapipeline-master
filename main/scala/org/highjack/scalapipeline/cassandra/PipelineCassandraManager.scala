
/*
package org.highjack.scalapipeline.cassandra

import com.datastax.driver.core.ResultSet
import com.datastax.spark.connector.japi.CassandraRow
import org.apache.spark.api.java.JavaRDD
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.immutable.HashMap

class PipelineCassandraManager (userId:String, pipelineId:String, tableId:String){

    def _userId: String = userId
    def _pipelineId: String = pipelineId
    def _tableId: String = tableId
    @Autowired()
    var hJCassandraManager: HJCassandraManager = _
    def manager:HJCassandraManager = hJCassandraManager

    def rdd :JavaRDD[CassandraRow]= manager.initRDD(_userId, _pipelineId, _tableId)


    def readKey(key:String): ResultSet = {
        manager.readWtQuery(key, _userId, _pipelineId, _tableId)
    }

    def insertAtKey(key:String, value:AnyRef): Unit = {
        val map:Map[String, AnyRef] = new HashMap[String, AnyRef].+((key, value))
        manager.insertWtQuery(map, _userId, _pipelineId, _tableId)
    }
    def insertMap(map:Map[String, AnyRef]): Unit = {
        manager.insertWtQuery(map, _userId, _pipelineId, _tableId)
    }

    def count(): Long = {
        manager.countRDD(rdd)
    }
}
*/
