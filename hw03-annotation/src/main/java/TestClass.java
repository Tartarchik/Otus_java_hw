import annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClass {
    private static final Logger logger = LoggerFactory.getLogger(TestClass.class);

    @BeforeClass
    public static void setUpClass() {
        logger.info("🏗️ BeforeClass - runs once before all tests");
    }

    @AfterClass
    public static void tearDownClass() {
        logger.info("🧹 AfterClass - runs once after all tests");
    }

    @Before
    public void setUp() {
        logger.info("🔧 Before - runs before each test");
    }

    @After
    public void tearDown() {
        logger.info("🧽 After - runs after each test");
    }

    @Test
    public void normalTest() {
        logger.info("Normal test passed");
    }

    @Test
    public void failingTest() {
        logger.info("This test will fail");
        throw new RuntimeException("Intentional failure");
    }

    @Skip
    @Test
    public void skippedTest() {
        logger.info("This test should be skipped");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    public void parametrizedTestWithInts(int number) {
        logger.info("Testing with number: {}", number);
        if (number % 2 == 0) {
            throw new RuntimeException("Failed on even number: " + number);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world", "test"})
    public void parametrizedTestWithStrings(String text) {
        logger.info("Testing with text: {}", text);
        if (text.equals("world")) {
            throw new RuntimeException("Failed on word: world");
        }
    }

    @Test
    public void anotherNormalTest() {
        logger.info("Another normal test passed");
    }
}