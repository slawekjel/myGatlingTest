package monolith

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DataFeeder extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8070/api/v1/")
    .header("Accept", "application/json")

  def createSaleOffer() = {
    repeat(5) {
      exec(
        http("Create one new sale offer for prototype in monolithic architecture")
          .post("offers/sales/")
          .body(StringBody(
            """{
          "name": "Selling new red hats",
          "description": "I would like to sell new unpacked, stylish red hats.",
          "price": 35,
          "quantity": 9999999,
          "status": "NEW",
          "shipmentNames": [ "Personal Pickup", "DPD Delivery", "UPS Delivery", "Paczkomat" ],
          "paymentNames": [ "Bank transfer", "Cash", "BLIK" ],
          "itemName": "Red Hat",
          "itemDescription": "Brand new red hat for men and women",
          "itemProducer": "Hat Hat Hat Company",
          "itemCategory": "Clothes",
          "new": true
           }"""
          )).asJson
          .check(status.is(201))
      )
    }
  }

  val scn = scenario("Post new sale offers")
    .exec(createSaleOffer())

  setUp(
    scn.inject(
      atOnceUsers(1))
  ).protocols(httpConf)

}
