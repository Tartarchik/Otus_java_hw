package framework;

public class TestResult {
    private final String testName;
    private int total;
    private int successful;
    private int failed;

    public TestResult(String testName) {
        this.testName = testName;
        this.total = 0;
        this.successful = 0;
        this.failed = 0;
    }

    public TestResult(String testName, int total) {
        this.testName = testName;
        this.total = total;
    }

    public void addSuccessful() {
        this.successful++;
        this.total++;
    }

    public void addFailed() {
        this.failed++;
        this.total++;
    }

    public void addTotal(int count) {
        this.total += count;
    }

    public String getTestName() {
        return testName;
    }

    public int getTotal() {
        return total;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }

    public boolean isSuccessful() {
        return failed == 0;
    }

    public TestSummary toSummary() {
        TestSummary summary = new TestSummary();
        summary.addSuccessful(successful);
        summary.addFailed(failed);
        return summary;
    }

    @Override
    public String toString() {
        return String.format(
                "framework.TestResult{testName='%s', total=%d, successful=%d, failed=%d}",
                testName, total, successful, failed
        );
    }
}