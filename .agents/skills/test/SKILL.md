---
name: test
description: Run tests with coverage and analyze results
---

Run tests following these steps:

## Steps

1. **Determine test scope**:
    - If specific test class mentioned: run that test
    - If specific module affected: run that module's tests
    - Otherwise: run all tests

2. **Run tests**:

    ```bash
    # Specific test class
    ./gradlew test --tests "team.themoment.readygsmserver.*{TestClassName}*"

    # All tests
    ./gradlew test
    ```

3. **Analyze results**:
    - Show test summary
    - If failures: show failure messages and suggest fixes
    - If success: confirm all tests passed

4. **Report**:
    - Total tests run
    - Pass/Fail count
    - Execution time

Do NOT proceed if tests fail - suggest fixes first.
