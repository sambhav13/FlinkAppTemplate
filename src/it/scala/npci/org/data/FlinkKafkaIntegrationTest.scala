package npci.org.data



import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec

class FlinkKafkaIntegrationTest  extends AnyFlatSpec with GivenWhenThen {

  "A Flink Pipeline " should "process records consumed from Kafka Topic" in {
    Given("A kafka Topic and Running Flink Pipeline ")
    //create kafka topic and run a Flink App

    When("a record is added to the kafka topic ")
    //produce record to kafka

    Then("Processed output should have two records written to the storage sink")
    //verify Inserts

    And("One Record should be updated")
    //Verify Update

    info("That's all Flink Pipeline seems to be running fine")
    }

}
