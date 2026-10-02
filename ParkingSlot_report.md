# ParkingSlot unit test report

## 0) Team members

**Name:** MD. SHAZAN MAHMUD ARPON

**Student ID:** 0112410351

Scope: `ParkingSlot`. The tests are in `src/test/java/parking/ParkingSlotTest.java`.

## A) Test case list

| Test ID | Class.Method under test | Why this test? | Verdict | Comments/observations |
| --- | --- | --- | --- | --- |
| PS01 | `ParkingSlot.ParkingSlot`, getters | A new slot must retain its ID and type, start active, and contain an empty booking list and zero-balance wallet. | PASS | All constructor defaults and returned values matched the documented behavior. |
| PS02 | `ParkingSlot.getBalance` | The reported slot balance must reflect funds held in its wallet. | PASS | Adding 35.0 to the wallet made `getBalance()` return 35.0. |
| PS03 | `ParkingSlot.isCompatible` | Every vehicle and slot type combination must follow the documented compatibility matrix. | PASS | All 24 combinations, including the unsupported `TRUCK` cases, returned the expected result. |
| PS04 | `ParkingSlot.deactivate`, `ParkingSlot.isCompatible` | An inactive slot must reject a vehicle even when its type and requested time are otherwise valid. | PASS | The slot became inactive and compatibility returned `false`. |
| PS05 | `ParkingSlot.activate`, `ParkingSlot.isCompatible` | Reactivating a slot must make a compatible, available slot usable again. | PASS | The slot became active and accepted a car in a regular slot. |
| PS06 | `ParkingSlot.isAvailable` | A slot with no stored bookings must be available. | PASS | Availability returned `true`. |
| PS07 | `ParkingSlot.isAvailable`, `ParkingSlot.isCompatible` | Any positive overlap with an existing booking must block the requested interval. | PASS | Five overlap arrangements were rejected by both availability and compatibility checks. |
| PS08 | `ParkingSlot.isCompatible` | Availability must be checked in every otherwise-compatible vehicle branch. | PASS | An overlapping booking blocked motorcycle, car, bus, bicycle, and microcar cases. |
| PS09 | `ParkingSlot.isAvailable` | A booking ending exactly when the request starts must not overlap. | PASS | The adjacent earlier booking left the slot available. |
| PS10 | `ParkingSlot.isAvailable` | A booking starting exactly when the request ends must not overlap. | PASS | The adjacent later booking left the slot available. |
| PS11 | `ParkingSlot.isAvailable` | Availability must inspect every stored booking rather than only the first one. | PASS | A later overlapping booking made the slot unavailable after an earlier non-overlapping booking. |

Run result: **42 test invocations, 0 failures, 0 errors, 0 skipped**.

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
