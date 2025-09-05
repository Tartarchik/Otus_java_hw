import framework.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("🚀 Starting Test Framework Demo");

        // Вариант 1: Запуск одного класса
        TestRunner runner = new TestRunner();
        runner.run(TestClass.class);

        logger.info("🎉 Test execution completed");
    }
}