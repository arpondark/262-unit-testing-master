# ParkingSlot unit test report

## 0) Team members

NAME: MD SHZAN MAHMUD ARPON
ID: 0112410351

Scope: `ParkingSlot`. The tests are in
`src/test/java/parking/ParkingSlotTest.java`.

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

No confirmed defect was found in `ParkingSlot` against the documented behavior.
The compatibility matrix, active state, wallet balance, overlap rules, and adjacent
time-window boundaries behaved as documented.

## C) Mutant analysis

ParkingSlot-scoped PIT result: **37 generated, 37 killed, 0 surviving; mutation
score 100%**. Line coverage for `ParkingSlot` was **37/37 (100%)**.

- **Killed mutant:** PIT replaced the return from one compatible branch of
  `ParkingSlot.isCompatible()` with `true`. Test PS08 stores an overlapping booking
  and expects compatibility to be `false` for every supported vehicle branch, so
  the changed return value fails the relevant parameterized invocation.
- **Surviving mutant:** None. All 37 mutants generated for `ParkingSlot` were
  killed; this does not prove behavior outside the documented rules is defect-free.

The ParkingSlot-only mutation analysis can be generated with:

```powershell
mvn "-DtargetClasses=parking.ParkingSlot" "-DtargetTests=parking.ParkingSlotTest" org.pitest:pitest-maven:mutationCoverage
```

## D) Individual contribution

MD SHZAN MAHMUD ARPON (0112410351) created the ParkingSlot tests, ran JUnit and
PIT, analyzed the results, and prepared this report.
