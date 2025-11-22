package fr.traqueur.crates.engine;

import fr.traqueur.crates.TestLoggerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive security tests for the unified ZScriptEngine.
 * Ensures JavaScript sandbox is properly isolated and prevents access to dangerous APIs.
 */
class ZScriptEngineSecurityTest {

    private ZScriptEngine engine;

    @BeforeEach
    void setUp() {
        TestLoggerHelper.initLogger();
        engine = new ZScriptEngine("test");
    }

    @AfterEach
    void tearDown() {
        if (engine != null) {
            engine.close();
        }
    }

    /**
     * Helper to execute scripts with a context object
     */
    private Object executeScript(String script, Object context) {
        Scriptable scope = engine.createSecureScope();
        ScriptableObject.putProperty(scope, "context", Context.javaToJS(context, scope));

        Context cx = Context.enter();
        try {
            Object result = cx.evaluateString(scope, script, "test", 1, null);
            if (result == null) {
                throw new RuntimeException("Script execution returned null");
            }
            return result;
        } finally {
            Context.exit();
        }
    }

    /**
     * Helper to execute scripts without context
     */
    private Object executeScript(String script) {
        return executeScript(script, new Object());
    }

    // ==================== JAVA PACKAGE ACCESS TESTS ====================

    @Nested
    @DisplayName("Java Package Access Restrictions")
    class JavaPackageAccessTests {

        @Test
        @DisplayName("Should not have access to 'java' package")
        void testNoAccessToJavaPackages() {
            String script = "typeof java";
            Object result = executeScript(script);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to 'java' package");
        }

        @Test
        @DisplayName("Should not have access to 'Packages' global")
        void testNoAccessToPackages() {
            String script = "typeof Packages";
            Object result = executeScript(script);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to 'Packages'");
        }

        @Test
        @DisplayName("Should not access java.lang.System")
        void testNoAccessToJavaLangSystem() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.System.exit(0);");
            }, "Script should not be able to call System.exit()");
        }

        @Test
        @DisplayName("Should not access java.lang.System.getProperty")
        void testNoAccessToSystemGetProperty() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.System.getProperty('user.home');");
            }, "Script should not access system properties");
        }

        @Test
        @DisplayName("Should not access java.lang.System environment")
        void testNoAccessToSystemEnv() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.System.getenv();");
            }, "Script should not access environment variables");
        }
    }

    // ==================== FILE SYSTEM ACCESS TESTS ====================

    @Nested
    @DisplayName("File System Access Restrictions")
    class FileSystemAccessTests {

        @Test
        @DisplayName("Should not access java.io.File")
        void testNoAccessToJavaIO() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.io.File('/etc/passwd');");
            }, "Script should not have access to java.io.File");
        }

        @Test
        @DisplayName("Should not access java.io.FileReader")
        void testNoAccessToFileReader() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.io.FileReader('/etc/passwd');");
            }, "Script should not have access to java.io.FileReader");
        }

        @Test
        @DisplayName("Should not access java.io.FileWriter")
        void testNoAccessToFileWriter() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.io.FileWriter('/tmp/test');");
            }, "Script should not have access to java.io.FileWriter");
        }

        @Test
        @DisplayName("Should not access java.nio.file.Files")
        void testNoAccessToNioFiles() {
            assertThrows(Exception.class, () -> {
                executeScript("java.nio.file.Files.readAllBytes(java.nio.file.Paths.get('/etc/passwd'));");
            }, "Script should not have access to java.nio.file.Files");
        }

        @Test
        @DisplayName("Should not access java.nio.file.Paths")
        void testNoAccessToNioPaths() {
            assertThrows(Exception.class, () -> {
                executeScript("java.nio.file.Paths.get('/etc/passwd');");
            }, "Script should not have access to java.nio.file.Paths");
        }
    }

    // ==================== NETWORK ACCESS TESTS ====================

    @Nested
    @DisplayName("Network Access Restrictions")
    class NetworkAccessTests {

        @Test
        @DisplayName("Should not access java.net.URL")
        void testNoAccessToJavaNet() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.net.URL('http://evil.com');");
            }, "Script should not have access to java.net.URL");
        }

        @Test
        @DisplayName("Should not access java.net.Socket")
        void testNoAccessToSocket() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.net.Socket('evil.com', 80);");
            }, "Script should not have access to java.net.Socket");
        }

        @Test
        @DisplayName("Should not access java.net.HttpURLConnection")
        void testNoAccessToHttpURLConnection() {
            assertThrows(Exception.class, () -> {
                executeScript("java.net.HttpURLConnection;");
            }, "Script should not have access to java.net.HttpURLConnection");
        }

        @Test
        @DisplayName("Should not access java.net.InetAddress")
        void testNoAccessToInetAddress() {
            assertThrows(Exception.class, () -> {
                executeScript("java.net.InetAddress.getLocalHost();");
            }, "Script should not have access to java.net.InetAddress");
        }
    }

    // ==================== PROCESS EXECUTION TESTS ====================

    @Nested
    @DisplayName("Process Execution Restrictions")
    class ProcessExecutionTests {

        @Test
        @DisplayName("Should not access Runtime.exec()")
        void testNoAccessToJavaLangRuntime() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.Runtime.getRuntime().exec('ls');");
            }, "Script should not have access to Runtime.exec()");
        }

        @Test
        @DisplayName("Should not access ProcessBuilder")
        void testNoAccessToJavaLangProcessBuilder() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.lang.ProcessBuilder('ls').start();");
            }, "Script should not have access to ProcessBuilder");
        }

        @Test
        @DisplayName("Should not access ProcessBuilder with array")
        void testNoAccessToProcessBuilderArray() {
            assertThrows(Exception.class, () -> {
                executeScript("new java.lang.ProcessBuilder(['ls', '-la']).start();");
            }, "Script should not have access to ProcessBuilder with array");
        }
    }

    // ==================== REFLECTION TESTS ====================

    @Nested
    @DisplayName("Reflection Access Restrictions")
    class ReflectionAccessTests {

        @Test
        @DisplayName("Should not access ClassLoader")
        void testNoAccessToJavaLangClassLoader() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.ClassLoader.getSystemClassLoader();");
            }, "Script should not have access to ClassLoader");
        }

        @Test
        @DisplayName("Should not access java.lang.reflect.Method")
        void testNoAccessToJavaLangReflect() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.reflect.Method;");
            }, "Script should not have access to java.lang.reflect");
        }

        @Test
        @DisplayName("Should not access java.lang.reflect.Field")
        void testNoAccessToReflectField() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.reflect.Field;");
            }, "Script should not have access to java.lang.reflect.Field");
        }

        @Test
        @DisplayName("Should not access java.lang.reflect.Constructor")
        void testNoAccessToReflectConstructor() {
            assertThrows(Exception.class, () -> {
                executeScript("java.lang.reflect.Constructor;");
            }, "Script should not have access to java.lang.reflect.Constructor");
        }

        @Test
        @DisplayName("Should not access getClass() on context objects")
        void testNoAccessToGetClass() {
            TestContext testContext = new TestContext();
            String script = "typeof context.getClass";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to getClass()");
        }

        @Test
        @DisplayName("Should not access 'class' property on context objects")
        void testNoAccessToClassProperty() {
            TestContext testContext = new TestContext();
            String script = "typeof context.class";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to 'class' property");
        }
    }

    // ==================== BLOCKED METHODS TESTS ====================

    @Nested
    @DisplayName("Blocked Object Methods")
    class BlockedMethodsTests {

        @Test
        @DisplayName("Should not access notify()")
        void testNoAccessToNotify() {
            TestContext testContext = new TestContext();
            String script = "typeof context.notify";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to notify()");
        }

        @Test
        @DisplayName("Should not access notifyAll()")
        void testNoAccessToNotifyAll() {
            TestContext testContext = new TestContext();
            String script = "typeof context.notifyAll";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to notifyAll()");
        }

        @Test
        @DisplayName("Should not access wait()")
        void testNoAccessToWait() {
            TestContext testContext = new TestContext();
            String script = "typeof context.wait";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to wait()");
        }

        @Test
        @DisplayName("Should not access clone()")
        void testNoAccessToClone() {
            TestContext testContext = new TestContext();
            String script = "typeof context.clone";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to clone()");
        }

        @Test
        @DisplayName("Should not access finalize()")
        void testNoAccessToFinalize() {
            TestContext testContext = new TestContext();
            String script = "typeof context.finalize";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to finalize()");
        }

        @Test
        @DisplayName("Should not access hashCode()")
        void testNoAccessToHashCode() {
            TestContext testContext = new TestContext();
            String script = "typeof context.hashCode";
            Object result = executeScript(script, testContext);
            assertEquals("undefined", Context.toString(result),
                    "Script should not have access to hashCode()");
        }
    }

    // ==================== STANDARD JAVASCRIPT ACCESS TESTS ====================

    @Nested
    @DisplayName("Standard JavaScript Access")
    class StandardJavaScriptTests {

        @Test
        @DisplayName("Should have access to Array")
        void testHasAccessToArray() {
            String script = "[1, 2, 3].length";
            Object result = executeScript(script);
            assertEquals(3, Context.toNumber(result), 0.001,
                    "Script should have access to Array");
        }

        @Test
        @DisplayName("Should have access to Object")
        void testHasAccessToObject() {
            String script = "var obj = {a: 1, b: 2}; obj.a + obj.b";
            Object result = executeScript(script);
            assertEquals(3, Context.toNumber(result), 0.001,
                    "Script should have access to Object");
        }

        @Test
        @DisplayName("Should have access to Math")
        void testHasAccessToMath() {
            String script = "Math.max(5, 10)";
            Object result = executeScript(script);
            assertEquals(10, Context.toNumber(result), 0.001,
                    "Script should have access to Math");
        }

        @Test
        @DisplayName("Should have access to String methods")
        void testHasAccessToString() {
            String script = "'hello'.toUpperCase()";
            Object result = executeScript(script);
            assertEquals("HELLO", Context.toString(result),
                    "Script should have access to String methods");
        }

        @Test
        @DisplayName("Should have access to JSON")
        void testHasAccessToJSON() {
            String script = "JSON.stringify({a: 1})";
            Object result = executeScript(script);
            assertEquals("{\"a\":1}", Context.toString(result),
                    "Script should have access to JSON");
        }

        @Test
        @DisplayName("Should have access to Date")
        void testHasAccessToDate() {
            String script = "typeof Date";
            Object result = executeScript(script);
            assertEquals("function", Context.toString(result),
                    "Script should have access to Date");
        }

        @Test
        @DisplayName("Should have access to RegExp")
        void testHasAccessToRegExp() {
            String script = "/test/.test('testing')";
            Object result = executeScript(script);
            assertTrue(Context.toBoolean(result),
                    "Script should have access to RegExp");
        }
    }

    // ==================== CONTEXT ACCESS TESTS ====================

    @Nested
    @DisplayName("Context Object Access")
    class ContextAccessTests {

        @Test
        @DisplayName("Should have access to context properties")
        void testHasAccessToContext() {
            TestContext testContext = new TestContext();
            testContext.value = 42;

            String script = "context.value";
            Object result = executeScript(script, testContext);
            assertEquals(42, Context.toNumber(result), 0.001,
                    "Script should have access to context provided");
        }

        @Test
        @DisplayName("Should be able to call context methods")
        void testCanCallContextMethods() {
            TestContext testContext = new TestContext();

            String script = "context.getValue()";
            Object result = executeScript(script, testContext);
            assertEquals(100, Context.toNumber(result), 0.001,
                    "Script should be able to call context methods");
        }

        @Test
        @DisplayName("Should not access private methods")
        void testCannotAccessContextPrivateMethods() {
            TestContext testContext = new TestContext();

            assertThrows(Exception.class, () -> {
                executeScript("context.privateMethod()", testContext);
            }, "Script should not access private methods");
        }
    }

    // ==================== SCOPE ISOLATION TESTS ====================

    @Nested
    @DisplayName("Scope Isolation")
    class ScopeIsolationTests {

        @Test
        @DisplayName("Prototype modifications should not persist between executions")
        void testCannotModifyPrototypePersistently() {
            String script = """
                Object.prototype.dangerous = function() { return 'hacked'; };
                var obj = {};
                obj.dangerous();
            """;

            Object result = executeScript(script);
            assertEquals("hacked", Context.toString(result));

            // Verify that modification doesn't persist
            String script2 = "var obj = {}; typeof obj.dangerous";
            Object result2 = executeScript(script2);
            assertEquals("undefined", Context.toString(result2),
                    "Prototype modification should not persist between executions");
        }

        @Test
        @DisplayName("Variables should not persist between executions")
        void testVariablesDoNotPersist() {
            executeScript("var testVar = 'secret';");

            String script2 = "typeof testVar";
            Object result2 = executeScript(script2);
            assertEquals("undefined", Context.toString(result2),
                    "Variables should not persist between executions");
        }

        @Test
        @DisplayName("Functions should not persist between executions")
        void testFunctionsDoNotPersist() {
            executeScript("function secretFunc() { return 42; }");

            String script2 = "typeof secretFunc";
            Object result2 = executeScript(script2);
            assertEquals("undefined", Context.toString(result2),
                    "Functions should not persist between executions");
        }
    }

    // ==================== COMPLEX SCRIPT TESTS ====================

    @Nested
    @DisplayName("Complex Script Execution")
    class ComplexScriptTests {

        @Test
        @DisplayName("Should execute loops correctly")
        void testComplexScriptExecution() {
            TestContext testContext = new TestContext();
            testContext.value = 10;

            String script = """
                var result = 0;
                for (var i = 0; i < context.value; i++) {
                    result += i;
                }
                result;
            """;

            Object result = executeScript(script, testContext);
            assertEquals(45, Context.toNumber(result), 0.001,
                    "Complex scripts should work correctly");
        }

        @Test
        @DisplayName("Should handle array manipulation")
        void testArrayManipulation() {
            String script = """
                var arr = [1, 2, 3, 4, 5];
                var doubled = arr.map(function(x) { return x * 2; });
                var sum = doubled.reduce(function(a, b) { return a + b; }, 0);
                sum;
            """;

            Object result = executeScript(script);
            assertEquals(30, Context.toNumber(result), 0.001,
                    "Array manipulation should work");
        }

        @Test
        @DisplayName("Should handle closures")
        void testClosures() {
            String script = """
                function createCounter() {
                    var count = 0;
                    return function() {
                        count++;
                        return count;
                    };
                }
                var counter = createCounter();
                counter(); counter(); counter();
            """;

            Object result = executeScript(script);
            assertEquals(3, Context.toNumber(result), 0.001,
                    "Closures should work correctly");
        }

        @Test
        @DisplayName("Should handle recursion")
        void testRecursion() {
            String script = """
                function factorial(n) {
                    if (n <= 1) return 1;
                    return n * factorial(n - 1);
                }
                factorial(5);
            """;

            Object result = executeScript(script);
            assertEquals(120, Context.toNumber(result), 0.001,
                    "Recursion should work correctly");
        }

        @Test
        @DisplayName("Should handle ES6 arrow functions")
        void testArrowFunctions() {
            String script = """
                var arr = [1, 2, 3];
                var doubled = arr.map(x => x * 2);
                doubled.reduce((a, b) => a + b, 0);
            """;

            Object result = executeScript(script);
            assertEquals(12, Context.toNumber(result), 0.001,
                    "ES6 arrow functions should work");
        }

        @Test
        @DisplayName("Should handle template literals")
        void testTemplateLiterals() {
            String script = """
                var name = 'World';
                `Hello ${name}!`;
            """;

            Object result = executeScript(script);
            assertEquals("Hello World!", Context.toString(result),
                    "Template literals should work");
        }

        @Test
        @DisplayName("Should handle destructuring")
        void testDestructuring() {
            String script = """
                var obj = {a: 1, b: 2};
                var {a, b} = obj;
                a + b;
            """;

            Object result = executeScript(script);
            assertEquals(3, Context.toNumber(result), 0.001,
                    "Destructuring should work");
        }
    }

    // ==================== FUNCTION EXECUTION TESTS ====================

    @Nested
    @DisplayName("Function Execution")
    class FunctionExecutionTests {

        @Test
        @DisplayName("executeFunction should work with valid function")
        void testExecuteFunction() {
            Scriptable scope = engine.createSecureScope();
            Context cx = Context.enter();
            try {
                String functionScript = "function testFunc(a, b) { return a + b; }; testFunc;";
                Object funcObj = cx.evaluateString(scope, functionScript, "test", 1, null);

                assertTrue(funcObj instanceof Function, "Result should be a Function");

                Function func = (Function) funcObj;
                engine.executeFunction(func, 5, 10);

                // If no exception, test passes
                assertTrue(true, "executeFunction should work without error");
            } finally {
                Context.exit();
            }
        }

        @Test
        @DisplayName("executeFunction should return null for null function")
        void testExecuteFunctionWithNull() {
            Object result = engine.executeFunctionWithResult(null);
            assertNull(result, "executeFunction with null should return null");
        }

        @Test
        @DisplayName("executeFunctionWithResult should return correct value")
        void testExecuteFunctionWithResult() {
            Scriptable scope = engine.createSecureScope();
            Context cx = Context.enter();
            try {
                String functionScript = "function add(a, b) { return a + b; }; add;";
                Object funcObj = cx.evaluateString(scope, functionScript, "test", 1, null);
                Function func = (Function) funcObj;

                Object result = engine.executeFunctionWithResult(func, 5, 10);

                assertEquals(15.0, Context.toNumber(result), 0.001,
                        "executeFunctionWithResult should return correct value");
            } finally {
                Context.exit();
            }
        }
    }

    // ==================== ENGINE PROPERTIES TESTS ====================

    @Nested
    @DisplayName("Engine Properties")
    class EnginePropertiesTests {

        @Test
        @DisplayName("Should return correct engine name")
        void testGetEngineName() {
            assertEquals("test", engine.getEngineName(),
                    "Engine name should be 'test'");
        }

        @Test
        @DisplayName("Should return non-null context")
        void testGetContext() {
            assertNotNull(engine.getContext(),
                    "Context should not be null");
        }

        @Test
        @DisplayName("Should create new secure scope each time")
        void testCreateSecureScopeUniqueness() {
            Scriptable scope1 = engine.createSecureScope();
            Scriptable scope2 = engine.createSecureScope();

            assertNotSame(scope1, scope2,
                    "Each call to createSecureScope should return a new scope");
        }
    }

    /**
     * Test context class for verifying Java object access from JavaScript
     */
    public static class TestContext {
        public int value;

        public int getValue() {
            return 100;
        }

        @SuppressWarnings("unused")
        private void privateMethod() {
            // This method should not be accessible from JavaScript
        }
    }
}