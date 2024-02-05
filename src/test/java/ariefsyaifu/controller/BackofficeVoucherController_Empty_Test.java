package ariefsyaifu.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.model.CombinationUniqueCode;
import ariefsyaifu.model.Voucher;
import ariefsyaifu.model.VoucherHistory;
import ariefsyaifu.model.VoucherOutlet;
import ariefsyaifu.model.VoucherTag;
import ariefsyaifu.model.VoucherTier;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import jakarta.transaction.Transactional;

@QuarkusTest
class BackofficeVoucherController_Empty_Test {

  @BeforeEach
  @Transactional
  void beforeEach() {
    VoucherOutlet.deleteAll();
    VoucherTag.deleteAll();
    VoucherTier.deleteAll();
    VoucherHistory.deleteAll();
    Voucher.deleteAll();
    CombinationUniqueCode.deleteAll();
  }

  @AfterEach
  @Transactional
  void afterEach() {
    VoucherOutlet.deleteAll();
    VoucherTag.deleteAll();
    VoucherTier.deleteAll();
    VoucherHistory.deleteAll();
    Voucher.deleteAll();
    CombinationUniqueCode.deleteAll();
  }

  @Test
  void testCreate_EmptyCombinationUniqueCode() {
    Assertions.assertEquals(0, Voucher.count());
    Assertions.assertEquals(0, VoucherTier.count());
    Assertions.assertEquals(0, VoucherTag.count());
    Assertions.assertEquals(0, VoucherHistory.count());
    Assertions.assertEquals(0, VoucherOutlet.count());
    Assertions.assertEquals(0, CombinationUniqueCode.count());

    Response r = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .header("X-Consumer-Custom-ID", "{}")
        .body("""
            {
                "prefixCode": "HEMAT",
                "name": "voucherName",
                "type": "AMOUNT",
                "amount": 10,
                "transactionType": "FIRST_VOUCHER",
                "quota": 10,
                "maxDiscount": 10,
                "modeType": "DINE_IN",
                "minSubtotal": 0,
                "maxRedeemedCount": 1,
                "usedDayType": "EVERYDAY",
                "validFrom": "2022-03-10T12:15:50",
                "validTo": "2024-03-10T12:15:50",
                "imageUrl": "string",
                "detail": "string",
                "qtyClaim": 1,
                "qtyRedeem": 1,
                "extendValidToInDays": 0,
                "status": "ACTIVE",
                "outlets": [
                  {
                    "id": "string",
                    "name": "string"
                  }
                ],
                "tags": [
                  "string"
                ],
                "tiers": [
                  "string"
                ]
              }
                """)
        .post("/api/v1/backoffice/voucher")
        .andReturn();

    Assertions.assertEquals(400, r.getStatusCode());

    JsonObject ras = new JsonObject(r.asString());
    Assertions.assertEquals("COMBINATION_UNIQUE_CODE_LESS_THAN_QTY_CLAIM", ras.getString("message"));

  }

}
