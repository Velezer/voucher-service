package ariefsyaifu.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ariefsyaifu.dto.voucher.backoffice.ViewVoucherIdOas;
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
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class BackofficeVoucherController_Create_Test {

  @BeforeEach
  @Transactional
  void beforeEach() {
    CombinationUniqueCode cuc = new CombinationUniqueCode();
    cuc.code = RandomString.make(4).toUpperCase();
    cuc.persist();
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
  void testCreate() {
    Assertions.assertEquals(0, Voucher.count());
    Assertions.assertEquals(0, VoucherTier.count());
    Assertions.assertEquals(0, VoucherTag.count());
    Assertions.assertEquals(0, VoucherHistory.count());
    Assertions.assertEquals(0, VoucherOutlet.count());
    Assertions.assertEquals(1, CombinationUniqueCode.count());

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

    Assertions.assertEquals(200, r.getStatusCode());

    Assertions.assertEquals(1, Voucher.count());
    Assertions.assertEquals(1, VoucherTier.count());
    Assertions.assertEquals(1, VoucherTag.count());
    Assertions.assertEquals(1, VoucherHistory.count());
    Assertions.assertEquals(1, VoucherOutlet.count());

    ViewVoucherIdOas ras = r.as(ViewVoucherIdOas.class);

    Voucher v = Voucher.findAll().firstResult();
    Assertions.assertEquals(v.id, ras.id);
    Assertions.assertEquals(Voucher.Status.ACTIVE, v.status);
    Assertions.assertEquals(1, v.qtyClaim);
    Assertions.assertEquals(0, v.qtyClaimed);
    Assertions.assertEquals("HEMAT", v.prefixCode);

    VoucherHistory vh = VoucherHistory.findAll().firstResult();
    Assertions.assertEquals(VoucherHistory.Type.AVAILABLE, vh.type);
    Assertions.assertTrue(vh.voucherCode.startsWith(v.prefixCode));

  }

  @ParameterizedTest
  @ValueSource(strings = {
      """
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
            """
  })
  void testCreate_Duplicate(String body) {
    Assertions.assertEquals(0, Voucher.count());
    Assertions.assertEquals(0, VoucherTier.count());
    Assertions.assertEquals(0, VoucherTag.count());
    Assertions.assertEquals(0, VoucherHistory.count());
    Assertions.assertEquals(0, VoucherOutlet.count());
    Assertions.assertEquals(1, CombinationUniqueCode.count());

    Response r = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .header("X-Consumer-Custom-ID", "{}")
        .body(body)
        .post("/api/v1/backoffice/voucher")
        .andReturn();

    Assertions.assertEquals(200, r.getStatusCode());

    {

      Response rd = RestAssured
          .given()
          .contentType(ContentType.JSON)
          .header("X-Consumer-Custom-ID", "{}")
          .body(body)
          .post("/api/v1/backoffice/voucher")
          .andReturn();

      Assertions.assertEquals(409, rd.getStatusCode());
      JsonObject rdas = new JsonObject(rd.asString());
      Assertions.assertEquals("DUPLICATED", rdas.getString("message"));
    }

    Assertions.assertEquals(1, Voucher.count());
    Assertions.assertEquals(1, VoucherTier.count());
    Assertions.assertEquals(1, VoucherTag.count());
    Assertions.assertEquals(1, VoucherHistory.count());
    Assertions.assertEquals(1, VoucherOutlet.count());

    ViewVoucherIdOas ras = r.as(ViewVoucherIdOas.class);

    Voucher v = Voucher.findAll().firstResult();
    Assertions.assertEquals(v.id, ras.id);
    Assertions.assertEquals(Voucher.Status.ACTIVE, v.status);
    Assertions.assertEquals(1, v.qtyClaim);
    Assertions.assertEquals(0, v.qtyClaimed);
    Assertions.assertEquals("HEMAT", v.prefixCode);

    VoucherHistory vh = VoucherHistory.findAll().firstResult();
    Assertions.assertEquals(VoucherHistory.Type.AVAILABLE, vh.type);
    Assertions.assertTrue(vh.voucherCode.startsWith(v.prefixCode));

  }

}
