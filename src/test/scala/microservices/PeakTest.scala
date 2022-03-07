package microservices

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class PeakTest extends Simulation {

  // 1 Http Conf
  val httpConf = http.baseUrl("http://localhost:8080/api/v1/")
    .header("Accept", "application/json")

  // 2 Scenario Definition
  def getAllSaleOffers() = {
    exec(
      http("Get all sale offers")
        .get("offers/sales/")
        .check(status.is(200), status.not(500), status.not(204))
    )
  }


  def getChosenOfferDetails() = {
    exec(
      http("Get one chosen sale offer with details")
        .get("offers/sales/1/details")
        .check(status.is(200), status.not(500), status.not(204))
    )
  }


  def buyItemFromChosenOffer() = {
    exec(
      http("Buy one item from chosen offer")
        .post("orders/")
        .body(StringBody(
          """{
          "saleOfferId": 1,
          "quantity": 1,
          "pricePerItem": 35,
          "shipmentMethod": "Personal Pickup",
          "paymentMethod": "Cash"
            }"""
        )).asJson
        .check(status.is(201), status.not(500), status.not(204))
    )
  }


  val scn = scenario("Peak test for microservices architecture prototype")
    .exec(getAllSaleOffers())
    .pause(2)
    .exec(getChosenOfferDetails())
    .pause(2)
    .exec(buyItemFromChosenOffer())

  // 3 Load Scenario
  setUp(
    scn.inject(
      nothingFor(5.seconds),
      constantUsersPerSec(150).during(5.seconds),
      constantUsersPerSec(250).during(5.seconds),
      constantUsersPerSec(100).during(10.seconds),
      constantUsersPerSec(350).during(5.seconds),
      constantUsersPerSec(100).during(10.seconds),
      constantUsersPerSec(250).during(5.seconds),
      constantUsersPerSec(150).during(5.seconds),
      nothingFor(5.seconds)
    )
      .protocols(httpConf)
  )
}