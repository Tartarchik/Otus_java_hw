package framework;

import annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public TestSummary run(Class<?> testClass) {
        TestSummary summary = new TestSummary();

        try {
            logger.info("Starting test execution for class: {}", testClass.getSimpleName());

            List<Method> beforeClassMethods = findMethodsByAnnotation(testClass, BeforeClass.class, true);
            List<Method> afterClassMethods = findMethodsByAnnotation(testClass, AfterClass.class, true);
            List<Method> beforeMethods = findMethodsByAnnotation(testClass, Before.class, false);
            List<Method> afterMethods = findMethodsByAnnotation(testClass, After.class, false);
            List<Method> testMethods = findTestMethods(testClass);

            invokeMethods(beforeClassMethods, null, "BeforeClass");

            Object testInstance = createInstance(testClass);

            for (Method testMethod : testMethods) {
                if (testMethod.isAnnotationPresent(Skip.class)) {
                    logger.info("🚫 TEST SKIPPED: {}", testMethod.getName());
                    summary.incrementSkipped();
                    continue;
                }

                TestResult result;
                if (testMethod.isAnnotationPresent(ParameterizedTest.class)) {
                    result = runParameterizedTest(testMethod, testInstance, beforeMethods, afterMethods);
                } else {
                    result = runSingleTest(testMethod, testInstance, beforeMethods, afterMethods);
                }

                summary.addSuccessful(result.getSuccessful());
                summary.addFailed(result.getFailed());
            }

            invokeMethods(afterClassMethods, null, "AfterClass");
            printSummary(summary);

        } catch (Exception e) {
            logger.error("❌ Failed to run tests: {}", e.getMessage(), e);
            summary.addFailed(1);
        }

        return summary;
    }

    private List<Method> findMethodsByAnnotation(Class<?> testClass, Class<? extends Annotation> annotation, boolean isStatic) {
        List<Method> methods = new ArrayList<>();
        for (Method method : testClass.getDeclaredMethods()) {
            boolean isStaticMethod = java.lang.reflect.Modifier.isStatic(method.getModifiers());
            if (method.isAnnotationPresent(annotation) && isStaticMethod == isStatic) {
                methods.add(method);
            }
        }
        return methods;
    }

    private List<Method> findTestMethods(Class<?> testClass) {
        List<Method> testMethods = new ArrayList<>();
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(ParameterizedTest.class)) {
                testMethods.add(method);
            }
        }
        return testMethods;
    }

private TestResult runSingleTest(Method testMethod, Object testInstance,
                                 List<Method> beforeMethods, List<Method> afterMethods) {
    TestResult result = new TestResult(testMethod.getName());

    try {
        invokeMethods(beforeMethods, testInstance, "Before");
        testMethod.invoke(testInstance);
        logger.info("✅ TEST PASSED: {}", testMethod.getName());
        result.addSuccessful();
    } catch (Exception e) {
        String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        logger.info("❌ TEST FAILED: {} - {}", testMethod.getName(), errorMessage);
        result.addFailed();
    } finally {
        invokeMethods(afterMethods, testInstance, "After");
    }

    return result;
}

    private TestResult runParameterizedTest(Method testMethod, Object testInstance,
                                            List<Method> beforeMethods, List<Method> afterMethods) {
        TestResult result = new TestResult(testMethod.getName());

        if (!testMethod.isAnnotationPresent(ValueSource.class)) {
            logger.error("Parameterized test {} missing @ValueSource", testMethod.getName());
            return result;
        }

        ValueSource valueSource = testMethod.getAnnotation(ValueSource.class);

        for (int value : valueSource.ints()) {
            try {
                invokeMethods(beforeMethods, testInstance, "Before");
                testMethod.invoke(testInstance, value);
                logger.info("✅ PARAMETRIZED TEST PASSED: {}({})", testMethod.getName(), value);
                result.addSuccessful();
            } catch (Exception e) {
                String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                logger.info("❌ PARAMETRIZED TEST FAILED: {}({}) - {}",
                        testMethod.getName(), value, errorMessage);
                result.addFailed();
            } finally {
                invokeMethods(afterMethods, testInstance, "After");
            }
        }

        for (String value : valueSource.strings()) {
            try {
                invokeMethods(beforeMethods, testInstance, "Before");
                testMethod.invoke(testInstance, value);
                logger.info("✅ PARAMETRIZED TEST PASSED: {}(\"{}\")", testMethod.getName(), value);
                result.addSuccessful();
            } catch (Exception e) {
                String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                logger.info("❌ PARAMETRIZED TEST FAILED: {}(\"{}\") - {}",
                        testMethod.getName(), value, errorMessage);
                result.addFailed();
            } finally {
                invokeMethods(afterMethods, testInstance, "After");
            }
        }

        return result;
    }

    private void invokeMethods(List<Method> methods, Object instance, String stage) {
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (Exception e) {
                String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                logger.error("{} method {} failed: {}", stage, method.getName(), errorMessage);
            }
        }
    }

    private Object createInstance(Class<?> testClass) {
        try {
            return testClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test instance", e);
        }
    }

    private void printSummary(TestSummary summary) {
        logger.info("\n📊 === TEST SUMMARY ===");
        logger.info("Total tests: {}", summary.getTotalTests());
        logger.info("Executed: {}", summary.getTotalExecuted());
        logger.info("Successful: {}", summary.getSuccessful());
        logger.info("Failed: {}", summary.getFailed());
        logger.info("Skipped: {}", summary.getSkipped());
        logger.info("Success rate: {:.1f}%", summary.getSuccessRate());
        logger.info("Overall success rate: {:.1f}%", summary.getOverallSuccessRate());
    }

    public static void runTests(Class<?>... testClasses) {
        TestRunner runner = new TestRunner();
        TestSummary totalSummary = new TestSummary();

        for (Class<?> testClass : testClasses) {
            TestSummary classSummary = runner.run(testClass);
            totalSummary.merge(classSummary);
        }

        logger.info("\n🎯 === GLOBAL SUMMARY ===");
        logger.info("Total classes: {}", testClasses.length);
        logger.info("Total tests: {}", totalSummary.getTotalTests());
        logger.info("Successful: {}", totalSummary.getSuccessful());
        logger.info("Failed: {}", totalSummary.getFailed());
        logger.info("Skipped: {}", totalSummary.getSkipped());
        logger.info("Overall success rate: {:.1f}%", totalSummary.getOverallSuccessRate());
    }
}