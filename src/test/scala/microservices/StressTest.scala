package microservices

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class StressTest extends Simulation {

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


  val scn = scenario("Stress test for microservices architecture prototype")
    .exec(getAllSaleOffers())
    .exec(getChosenOfferDetails())
    .exec(buyItemFromChosenOffer())

  // 3 Load Scenario
  setUp(
    scn.inject(
      rampUsersPerSec(0).to(300).during(5.minutes)
    )
      .protocols(httpConf)
  )
}