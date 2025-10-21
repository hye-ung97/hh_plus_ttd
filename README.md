# hhplus week 1 TDD - 포인트 관리 시스템

## 🔄 TDD 사이클 적용 방식

[![](https://mermaid.ink/img/pako:eNqlU8uO2yAU_RVENq3kpOC3mWqkSWbZ2UwXlVp3gc3FtkKMhbGSaZRvmVVH6t_1E0ocuxPNsmXFgfO4wOWISy0AMyyV3pc1NxZ9esxb5Mbdu285_v38_BN9MY0FxD8W5sOt5I1q2gpZ6G2Ov7-_cNcT9xd64FtAtoaRfCahjvf9K3MzMV_QI0heWm0ue1MmWi5v0XoyHcFm0o3gbiaWyrneg0QGBJKNUmwhZRInkddbo7fAFiIOSEAnuNw3wtbM7w5eqZU2bOGLIAxiT-rWLvfQVLVlhVbi5o19ZQDaKSCKQJbh3wBCijQL_zegUANM_klYZFK--mdpCME_-F8luAt1F3S9sL4c6XppMxYx69zrbj_bJwWIeNTz0VxO4RdQvikn6A432MOVaQRm1gzg4R2YHT9DfDz75dj1wg5yzNxUuBcflGubvD05Wcfbr1rvZqXRQ1XPYOgEt3Df8Mpwx5Bc9WcKtALMRg-txYySKB5NMDvig8NJvEoyP4nilGaUevgJMz_0V1GUkjSOKMkykpw8_GPMJKs4oDFNkjSMCKG-7wQgGtePD5cPMf6L0x-x7v3s?type=png)](https://mermaid.live/edit#pako:eNqlU8uO2yAU_RVENq3kpOC3mWqkSWbZ2UwXlVp3gc3FtkKMhbGSaZRvmVVH6t_1E0ocuxPNsmXFgfO4wOWISy0AMyyV3pg1NxZ9esxb5Mbdu285_v38_BN9MY0FxD8W5sOt5I1q2gpZ6G2Ov7-_cNcT9xd64FtAtoaRfCahjvf9K3MzMV_QI0heWm0ue1MmWi5v0XoyHcFm0o3gbiaWyrneg0QGBJKNUmwhZRInkddbo7fAFiIOSEAnuNw3wtbM7w5eqZU2bOGLIAxiT-rWLvfQVLVlhVbi5o19ZQDaKSCKQJbh3wBCijQL_zegUANM_klYZFK--mdpCME_-F8luAt1F3S9sL4c6XppMxYx69zrbj_bJwWIeNTz0VxO4RdQvikn6A432MOVaQRm1gzg4R2YHT9DfDz75dj1wg5yzNxUuBcflGubvD05Wcfbr1rvZqXRQ1XPYOgEt3Df8Mpwx5Bc9WcKtALMRg-txYySKB5NMDvig8NJvEoyP4nilGaUevgJMz_0V1GUkjSOKMkykpw8_GPMJKs4oDFNkjSMCKG-7wQgGtePD5cPMf6L0x-x7v3s)

### Red-Green-Refactor 사이클

1. **🔴 Red (실패하는 테스트 작성)**
   - 테스트를 먼저 작성하여 컴파일 에러를 발생시킨다
   - 테스트가 실패하는 것을 확인한다

2. **🟢 Green (테스트 통과시키기)**
   - 해당 테스트를 통과하기 위한 최소한의 코드를 작성한다
   - 테스트가 통과하는 것을 확인한다

3. **🔵 Refactor (리팩토링)**
   - 중복되는 코드 제거 및 리팩토링을 진행한다
   - 코드 품질을 개선한다

4. **🔄 반복**
   - 위 사이클을 반복하며 기능을 확장해 나간다

## ✏️ 테스트 구현 내용

### 단위 테스트 (PointServiceTest)
- **포인트 조회**: 사용자 포인트 조회 기능 테스트
- **포인트 충전**: 포인트 충전 및 내역 저장 테스트
- **포인트 사용**: 포인트 사용 및 내역 저장 테스트
- **포인트 내역 조회**: 거래 내역 조회 기능 테스트

## 🤖 Claude Code 활용

### TDD 검증 커맨드
- 테스트 코드가 TDD 사이클에 맞는 테스트인지 확인하는 커맨드 생성
- 각 테스트마다 커맨드를 이용하여 TDD 사이클에 맞는지 검증

## 📜 비즈니스 정책

### 포인트 충전 정책
- **최소 충전 금액**: 100원 이상
- **최대 보유 한도**: 100,000원
- **충전 금액 제한**: 0원 및 음수 금액 불가

### 포인트 사용 정책
- **사용 단위**: 100원 단위로만 사용 가능
- **잔액 확인**: 사용 금액이 보유 포인트를 초과할 수 없음
- **사용 금액 제한**: 0원 및 음수 금액 사용 불가
