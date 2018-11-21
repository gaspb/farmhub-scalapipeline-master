package org.highjack.scalapipeline.pipeline.transformations

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.ListMap
import cats.implicits._
import org.slf4j.{Logger, LoggerFactory}

object StringSourceToStatsMethods {

    val uniqueBuckets = 500 //max number of elements to keep in memory
    val logger : Logger = LoggerFactory.getLogger(this.getClass)
    def STRING_SOURCE_TO_WORD_OCCURRENCE_STAT_SOURCE(excludeCommonWords:Boolean, take:Option[Long]) : Flow[String, Map[String, Long], NotUsed]  = {
       var lgth = 0
        logger.info("STRING_SOURCE_TO_WORD_OCCURRENCE_STAT_SOURCE - take is defined="+( if (take.isDefined) take.get else false))
        val flow = Flow[String].fold(Map.empty[String, Long]) {
            (acc, text) => {
                lgth+= text.length
               /* logger.info(">>>>>> ---------     Counting occurrences  ------------"+text.length+"/"+lgth)*/
                val wc: Map[String, Long] = countWordsOccurences(text, if(excludeCommonWords) commonWordsToExclude else Set.empty[String])
                // then use Monoid append operation to update our statistics in 'acc'
                // with what is just calculated for current text message
                ListMap((acc |+| wc).toSeq.sortBy(-_._2).take(uniqueBuckets): _*)
            }
        }
        if (take.isDefined) flow.map(m=> m.take(take.get.toInt)) else flow
    }

    // Map of [String and Int] is our statistics data structure, where String is
    // particular word and Int is number/count of its occurences.
    def countWordsOccurences(text: String, wordsToExclude : Set[String]): Map[String, Long] = {
        text.split(" ")
            .filter(s => s.trim.nonEmpty && s.matches("\\w+"))
            .map(_.toLowerCase.trim)
            // Set of String with words which I do want to appear in the statistics
            .filterNot(wordsToExclude.contains)
            .foldLeft(Map.empty[String, Long]) {
                // using Cats combine operation to calculate current text message word counts
                (count, word) => count |+| Map(word -> 1)
            }
    }




       //current size : 186
    val commonWordsToExclude:Set[String] = Set[String]("i", "the", "a", "is", "you", "our", "your", "us", "to", "and", "of", "that", "this",
        "are", "my", "in", "for", "with", "as", "but", "by", "so", "it", "have", "we", "not", "his", "her", "it", "its", "me", "thou", "be",
        "he", "she", "me", "will", "thy", "what", "who", "which", "shall", "should", "would", "could", "can", "him", "all", "do", "if", "or",
        "no", "from", "yes", "on", "good", "well", "at", "let", "they", "their", "them", "am", "was", "how", "many", "when", "where", "than",
        "an", "any", "more", "may", "upon", "over", "away", "here", "thee", "such", "make", "made", "did", "like", "now", "then", "enter",
        "give", "must", "one", "two", "three", "five", "ten", "come", "came", "know", "knew", "had", "some", "few", "yet", "there", "these",
        "those", "take", "took", "were", "most", "say", "said", "see", "tell", "first", "second", "time", "times", "day", "days", "out", "man",
        "never", "go", "went", "mine", "think", "great", "big", "long", "ago", "nor", "much", "woman", "guy", "girl", "boy", "very", "cannot",
        "don't", "too", "up", "down", "right", "left", "hear", "heard", "saw", "own", "being", "before", "been", "after", "while", "during",
        "why", "into", "though", "other", "even", "ever", "every", "call", "men", "women", "put", "use", "both", "whose", "comes", "might",
        "only", "has", "about", "same", "under", "without", "still", "through", "also", "way", "just", "again", "back", "because", "always",
        "having"
    )
}
