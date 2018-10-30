
package org.highjack.scalapipeline.config;
/*
import io.github.jhipster.config.JHipsterProperties;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@RefreshScope
@Configuration
public class SparkConfiguration {

    @Autowired
    private Environment env;

    @Value("${spark.appname}")
    private String appName;

    @Value("${spark.home}")
    private String sparkHome;

    @Value("${spark.masteruri:local}")
    private String masterUri;

    @Bean
    public SparkConf sparkConf() {
        SparkConf sparkConf = new SparkConf()
            .setAppName(appName)
            .setSparkHome(sparkHome)
            .setMaster(masterUri);
        return sparkConf;
    }

    @Bean
    public JavaSparkContext sparkContext() {
        return new JavaSparkContext(sparkConf());
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
            .builder()
            .sparkContext(sparkContext().sc())
            .appName(appName)
            .getOrCreate();
    }

}
*/
