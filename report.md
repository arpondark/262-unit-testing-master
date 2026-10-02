# Booking unit test report

## 0) Team members

**Name:** MD. SHAZAN MAHMUD ARPON

**Student ID:** 0112410351

Scope: `Booking`. The tests are in `src/test/java/parking/BookingTest.java`.

## A) Test case list

| Test ID | Class.Method under test | Why this test? | Verdict | Comments/observations |
| --- | --- | --- | --- | --- |
| B01 | `Booking.Booking`, getters, `getBookingStatus` | A valid new booking must preserve its ID, vehicle, slot, time window, and amount and start as `ACTIVE`. | PASS | All supplied values were returned unchanged and the initial status was `ACTIVE`. |
| B02 | `Booking.completeBooking` | Completing an active booking must change its status to `COMPLETED`. | PASS | The status changed from `ACTIVE` to `COMPLETED`. |
| B03 | `Booking.cancelBooking` | Cancelling an active booking must change its status to `CANCELLED`. | PASS | The status changed from `ACTIVE` to `CANCELLED`. |
| B04 | `Booking.completeBooking` | Repeating the same completion operation must be idempotent. | PASS | The booking remained `COMPLETED`. |
| B05 | `Booking.cancelBooking` | Repeating the same cancellation operation must be idempotent. | PASS | The booking remained `CANCELLED`. |
| B06 | `Booking.cancelBooking` | A completed booking is in a terminal state and must not later become cancelled. | **FAIL** | `cancelBooking()` overwrote `COMPLETED` with `CANCELLED`. This exposes D-B01. |
| B07 | `Booking.cancelBooking` | Repeated cancellation attempts must not overwrite an already completed booking. | **FAIL** | The first cancellation attempt changed the terminal status to `CANCELLED`. This confirms D-B01 over a repeated-call sequence. |
| B08 | `Booking.completeBooking` | A cancelled booking is in a terminal state and must not later become completed. | **FAIL** | `completeBooking()` overwrote `CANCELLED` with `COMPLETED`. This exposes D-B02. |
| B09 | `Booking.completeBooking` | Repeated completion attempts must not overwrite an already cancelled booking. | **FAIL** | The first completion attempt changed the terminal status to `COMPLETED`. This confirms D-B02 over a repeated-call sequence. |
| B10 | `Booking.toString` | The textual representation should include the booking ID, amount, and current status for inspection. | PASS | All three values appeared in the returned string. |

JUnit result: **10 tests, 4 failures, 0 errors, 0 skipped**. All test data uses a valid vehicle, compatible parking slot, positive amount, and an end time after the start time.

## B) Defects list

| Defect ID | Class.Method | Description and suggested fix |
| --- | --- | --- |
| D-B01 | `Booking.cancelBooking` | The method changes a `COMPLETED` booking to `CANCELLED`, allowing two mutually exclusive terminal outcomes for the same booking. Update the method only when `bookingStatus == ACTIVE`; otherwise retain the terminal state or throw a documented state-transition exception. |
| D-B02 | `Booking.completeBooking` | The method changes a `CANCELLED` booking to `COMPLETED`. Apply the same active-state guard so a cancelled booking cannot later be completed and settled. |

## C) Mutant analysis

The generated PIT report covers both assigned classes: **45 mutations generated, 45 killed, 0 surviving; mutation score 100%**. Line coverage for the mutated classes was **58/58 (100%)**. The `Booking` breakdown was **8/8 mutations killed** with **21/21 lines covered**.

- **Killed mutant:** PIT replaced the value returned by `Booking.getAmount()` with `0.0`. B01 expects the valid booking amount `20.0`, so that mutation is detected immediately.
- **Surviving mutant:** None. All eight mutants generated for `Booking` were killed.

PIT requires every included test to pass before mutation analysis. Tests B06-B09 are tagged `known-defect` and excluded only from PIT through `pom.xml`; normal JUnit runs still execute them and report the four implementation failures. The HTML report is at `target/pit-reports/index.html` and was generated with:

```powershell
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage
```

## D) Individual contribution

MD. SHAZAN MAHMUD ARPON (0112410351) designed and implemented the Booking tests, identified the invalid terminal-state transitions, ran JUnit and PIT, analyzed the results, and prepared this report.
