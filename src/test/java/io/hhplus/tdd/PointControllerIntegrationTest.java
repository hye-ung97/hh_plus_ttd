package io.hhplus.tdd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("PointController 통합 테스트")
class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final long USER_ID = 1L;

    @Test
    @DisplayName("포인트 조회 성공")
    void getPoint_success() throws Exception {
        // when & then
        mockMvc.perform(get("/point/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.point").exists())
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void chargePoint_success() throws Exception {
        // given
        long chargeAmount = 1000L;

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(chargeAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.point").value(1000));
    }

    @Test
    @DisplayName("포인트 충전 실패 - 최소 금액(100원) 미만")
    void chargePoint_fail_minimumAmount() throws Exception {
        // given
        long invalidAmount = 50L;

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(invalidAmount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_002"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("포인트 충전 실패 - 한도 초과")
    void chargePoint_fail_exceedLimit() throws Exception {
        //given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("99000"));

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_003"));
    }

    @Test
    @DisplayName("포인트 충전 실패 - 0원 충전")
    void chargePoint_fail_zeroAmount() throws Exception {
        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_002"));
    }

    @Test
    @DisplayName("포인트 충전 실패 - 음수 금액")
    void chargePoint_fail_negativeAmount() throws Exception {
        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("-100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_002"));
    }

    @Test
    @DisplayName("포인트 충전 성공 - 최소 금액 (100원)")
    void chargePoint_boundary_minimum() throws Exception {
        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(100));
    }

    @Test
    @DisplayName("포인트 충전 성공 - 최대 한도 (100,000원)")
    void chargePoint_boundary_maximum() throws Exception {
        // when & then
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("100000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(100000));
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void usePoint_success() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"));

        // when & then - 300원 사용
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(700));
    }

    @Test
    @DisplayName("포인트 사용 실패 - 잔액 부족")
    void usePoint_fail_insufficientBalance() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("500"));

        // when & then
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_001"));
    }

    @Test
    @DisplayName("포인트 사용 실패 - 100원 단위 아님")
    void usePoint_fail_invalidUnit() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"));

        // when & then
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("150"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_004"));
    }

    @Test
    @DisplayName("포인트 사용 성공 - 전액 사용")
    void usePoint_boundary_allAmount() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"));

        // when & then
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(0));
    }

    @Test
    @DisplayName("포인트 사용 성공 - 100원 단위 (경계값)")
    void usePoint_boundary_hundredUnit() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"));

        // when & then - 100원 사용
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(900));
    }

    @Test
    @DisplayName("포인트 내역 조회 성공 - 200 OK")
    void getPointHistory_success() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"));
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("300"));

        // when & then
        mockMvc.perform(get("/point/{id}/histories", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(USER_ID))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[0].amount").value(1000))
                .andExpect(jsonPath("$[1].userId").value(USER_ID))
                .andExpect(jsonPath("$[1].type").value("USE"))
                .andExpect(jsonPath("$[1].amount").value(300));
    }

    @Test
    @DisplayName("포인트 내역 조회 - 내역 없음 (빈 배열)")
    void getPointHistory_empty() throws Exception {
        // when & then
        mockMvc.perform(get("/point/{id}/histories", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("포인트 내역 조회 - 다중 트랜잭션")
    void getPointHistory_multipleTransactions() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"));
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("2000"));
        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("500"));

        // when & then
        mockMvc.perform(get("/point/{id}/histories", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("E2E - 충전 → 사용 → 조회 전체 플로우")
    void endToEnd_chargeUseAndCheck() throws Exception {
        // given
        mockMvc.perform(get("/point/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(0));

        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(5000));

        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(3800));

        mockMvc.perform(get("/point/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(3800));

        mockMvc.perform(get("/point/{id}/histories", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("E2E - 다중 충전 후 한도 초과 확인")
    void endToEnd_multipleChargesThenExceedLimit() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("50000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(50000));

        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("40000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(90000));

        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("20000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_003"));

        mockMvc.perform(get("/point/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(90000));
    }

    @Test
    @DisplayName("E2E - 충전 후 부분 사용, 잔액 부족 확인")
    void endToEnd_partialUseAndInsufficientBalance() throws Exception {
        // given
        mockMvc.perform(patch("/point/{id}/charge", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1000"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("600"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(400));

        mockMvc.perform(patch("/point/{id}/use", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("POINT_001"));

        mockMvc.perform(get("/point/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(400));

        mockMvc.perform(get("/point/{id}/histories", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
