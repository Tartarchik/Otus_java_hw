package framework;

public class TestSummary {
    private int successful;
    private int failed;
    private int skipped;

    public TestSummary() {
        this.successful = 0;
        this.failed = 0;
        this.skipped = 0;
    }

    public void addSuccessful(int count) {
        this.successful += count;
    }

    public void addFailed(int count) {
        this.failed += count;
    }

    public void addSkipped(int count) {
        this.skipped += count;
    }

    public void incrementSuccessful() {
        this.successful++;
    }

    public void incrementFailed() {
        this.failed++;
    }

    public void incrementSkipped() {
        this.skipped++;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getTotalTests() {
        return successful + failed + skipped;
    }

    public int getTotalExecuted() {
        return successful + failed;
    }

    public double getSuccessRate() {
        int total = getTotalExecuted();
        return total > 0 ? (successful * 100.0) / total : 100.0;
    }

    public double getOverallSuccessRate() {
        int total = getTotalTests();
        return total > 0 ? (successful * 100.0) / total : 100.0;
    }

    public void merge(TestSummary other) {
        this.successful += other.successful;
        this.failed += other.failed;
        this.skipped += other.skipped;
    }

    @Override
    public String toString() {
        return String.format(
                "framework.TestSummary{total=%d, successful=%d, failed=%d, skipped=%d, successRate=%.1f%%}",
                getTotalTests(), successful, failed, skipped, getSuccessRate()
        );
    }
}