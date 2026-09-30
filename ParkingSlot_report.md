# ParkingSlot unit test report

## 0) Team members

**Name:** MD. SHAZAN MAHMUD ARPON

**Student ID:** 0112410351

Scope: `ParkingSlot`. The tests are in `src/test/java/parking/ParkingSlotTest.java`.

## A) Test case list

| Test ID | Class.Method under test | Why this test? | Verdict | Comments/observations |
| --- | --- | --- | --- | --- |
| PS01 | `ParkingSlot.ParkingSlot`, getters | A new slot must preserve its ID and type, start active, and contain an empty booking list and zero-balance wallet. | PASS | All constructor defaults and returned values matched the expected behavior. |
| PS02 | `ParkingSlot.getBalance` | The reported slot balance must reflect funds held in its wallet. | PASS | Adding `35.0` to the wallet made `getBalance()` return `35.0`. |
| PS03 | `ParkingSlot.isCompatible` | Every vehicle and slot-type combination must follow the compatibility matrix. | PASS | All 24 combinations, including unsupported `TRUCK` cases, returned the expected result. |
| PS04 | `ParkingSlot.deactivate`, `ParkingSlot.isCompatible` | An inactive slot must reject an otherwise compatible vehicle and valid time window. | PASS | The slot became inactive and compatibility returned `false`. |
| PS05 | `ParkingSlot.activate`, `ParkingSlot.isCompatible` | Reactivating a slot must make a compatible and available slot usable again. | PASS | The slot became active and accepted a car in a regular slot. |
| PS06 | `ParkingSlot.isAvailable` | A slot with no stored bookings must be available for a valid request window. | PASS | Availability returned `true`. |
| PS07 | `ParkingSlot.isAvailable`, `ParkingSlot.isCompatible` | Every positive-overlap arrangement with an active booking must block the requested interval. | PASS | All five overlap arrangements were rejected by both checks. |
| PS08 | `ParkingSlot.isCompatible` | Availability must be checked in every otherwise-compatible vehicle branch. | PASS | An active overlapping booking blocked motorcycle, car, bus, bicycle, and microcar cases. |
| PS09 | `ParkingSlot.isAvailable` | A booking ending exactly when the request starts is adjacent and must not overlap. | PASS | The slot remained available. |
| PS10 | `ParkingSlot.isAvailable` | A booking starting exactly when the request ends is adjacent and must not overlap. | PASS | The slot remained available. |
| PS11 | `ParkingSlot.isAvailable` | Availability must inspect every stored booking, rather than only the first entry. | PASS | A later active overlap made the slot unavailable after an earlier non-overlapping booking. |
| PS12 | `ParkingSlot.isAvailable` | A cancelled booking with the same valid time window must release the slot. | **FAIL** | The cancelled booking still made the slot unavailable. This exposes D-PS01. |
| PS13 | `ParkingSlot.isAvailable` | A cancelled booking whose old window contains the request must be ignored. | **FAIL** | Availability returned `false` because booking status was not checked. This confirms D-PS01 for an enclosing overlap. |
| PS14 | `ParkingSlot.isAvailable` | A cancelled booking whose old window is inside the request must be ignored. | **FAIL** | Availability returned `false` because booking status was not checked. This confirms D-PS01 for a contained overlap. |
| PS15 | `ParkingSlot.isCompatible` | A compatible vehicle must be accepted after the overlapping reservation is cancelled. | **FAIL** | `isCompatible()` delegated to the defective availability result and returned `false`. This exposes D-PS02. |

JUnit result: **46 test invocations, 4 failures, 0 errors, 0 skipped**. The parameterized tests account for 24 compatibility cases, five overlap cases, and five occupied compatibility cases. All four failing cases use valid time windows and a supported `CAR`/`REGULAR` combination.

## B) Defects list

| Defect ID | Class.Method | Description and suggested fix |
| --- | --- | --- |
| D-PS01 | `ParkingSlot.isAvailable` | The method treats cancelled bookings as active reservations, so a cancelled booking continues blocking every overlapping request. Ignore bookings whose status is not `ACTIVE`, or remove cancelled bookings from the slot before checking overlap. |
| D-PS02 | `ParkingSlot.isCompatible` | A compatible vehicle is rejected after cancellation because this method uses the incorrect result from `isAvailable`. Correcting the status-aware availability check fixes this observed compatibility failure. |

## C) Mutant analysis

The generated PIT report covers both assigned classes: **45 mutations generated, 45 killed, 0 surviving; mutation score 100%**. Line coverage for the mutated classes was **58/58 (100%)**. The `ParkingSlot` breakdown was **37/37 mutations killed** with **37/37 lines covered**.

- **Killed mutant:** PIT replaced a compatible branch return in `ParkingSlot.isCompatible()` with `true`. PS08 supplies an active overlapping booking and expects `false` for every supported vehicle branch, so the affected parameterized invocation kills the mutant.
- **Surviving mutant:** None. All 37 mutants generated for `ParkingSlot` were killed.

PIT requires every included test to pass before mutation analysis. Tests PS12-PS15 are tagged `known-defect` and excluded only from PIT through `pom.xml`; normal JUnit runs still execute them and report the four implementation failures. The HTML report is at `target/pit-reports/index.html` and was generated with:

```powershell
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage
```

## D) Individual contribution

MD. SHAZAN MAHMUD ARPON (0112410351) designed and implemented the ParkingSlot tests, identified the cancelled-booking availability defect, ran JUnit and PIT, analyzed the results, and prepared this report.
