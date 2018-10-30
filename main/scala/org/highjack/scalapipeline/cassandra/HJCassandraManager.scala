/*
package org.highjack.scalapipeline.cassandra
import com.datastax.driver.core.{ResultSet, Session}
import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.japi.{CassandraJavaUtil, CassandraRow}
import org.apache.spark.SparkConf
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.highjack.scalapipeline.utils.Java8Util._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HJCassandraManager @Autowired()(sparkConf: SparkConf, sparkContext: JavaSparkContext) {

    def _sparkConf: SparkConf = sparkConf
    def _sparkContext: JavaSparkContext = sparkContext

    def initRDD(_userId:String, _pipelineId:String, _tableId:String) :  JavaRDD[CassandraRow]= {

        //=> create table if not exists for that pipeline, else override ? or change pipelineId
        CassandraConnector(sparkConf).withSessionDo { session:Session =>
            //createKeyspace
            session.execute("CREATE KEYSPACE IF NOT EXISTS "+_userId+" WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };")
            //createTable
            session.execute("CREATE TABLE IF NOT EXISTS "+_userId+"."+_pipelineId+_tableId+"(key text PRIMARY KEY, value int);")
        }

        val rdd:JavaRDD[CassandraRow] = CassandraJavaUtil.javaFunctions(sparkContext).cassandraTable(_userId, _pipelineId+_tableId).toJavaRDD()
        rdd.cache()
        rdd
    }




    def insertWtQuery(map: Map[String, AnyRef], _userId:String, _pipelineId:String, _tableId:String): Unit = {
        CassandraConnector(sparkConf).withSessionDo { session:Session =>
            val queryBase = s"INSERT INTO ${_userId}.${_pipelineId+_tableId}(key, value) VALUES "
            map foreach((e:(String,AnyRef)) => {
                val query = s"$queryBase('${e._1}', ${e._2});"
                session.execute(query)
            }
                )
        }
    }

    def readWtQuery(key:String, _userId:String, _pipelineId:String, _tableId:String) : ResultSet = {
        CassandraConnector(sparkConf).withSessionDo { session:Session => {
            val query = s"SELECT '$key' FROM ${_userId}.${_pipelineId+_tableId};"
            session.execute(query)
        }
        }


    }
    def countRDD(rdd:JavaRDD[CassandraRow]): Long = {
        rdd.count()
    }
    def fillIntRDD(count:Int, rdd:JavaRDD[CassandraRow]) = {
        val moreData = for (i <- 100 until 200) yield ((s"addedKey$i"),i)
        val moreDataRdd = sparkContext.parallelize(moreData)
        rdd.map(new org.apache.spark.api.java.function.Function[CassandraRow, Int]() {
            override def call(row: CassandraRow): Int = row.getInt("value")
        }).take(count)
    }
    def visualizeIntRDD(maxCount:Int, rdd:JavaRDD[CassandraRow]) = {
        rdd.map(new org.apache.spark.api.java.function.Function[CassandraRow, Int]() {
            override def call(row: CassandraRow): Int = row.getInt("value")
        }).take(maxCount)
    }



}
*/
