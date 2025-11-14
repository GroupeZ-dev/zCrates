package fr.traqueur.crates.animations;

import fr.traqueur.crates.TestLoggerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de sécurité pour s'assurer que le scope JavaScript est correctement isolé
 * et ne donne pas accès à des APIs dangereuses.
 */
class ZAnimationEngineSecurityTest {

    private ZAnimationEngine engine;

    @BeforeEach
    void setUp() {
        // Initialiser le Logger pour les tests
        TestLoggerHelper.initLogger();
        engine = new ZAnimationEngine();
    }

    @AfterEach
    void tearDown() {
        if (engine != null) {
            engine.close();
        }
    }

    /**
     * Exécute un script dans un scope sécurisé avec un contexte
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

    @Test
    void testNoAccessToJavaPackages() {
        String script = "typeof java";
        Object result = executeScript(script, new Object());
        assertEquals("undefined", Context.toString(result),
                "Le script ne devrait pas avoir accès au package 'java'");
    }

    @Test
    void testNoAccessToJavaLangSystem() {
        assertThrows(Exception.class, () -> {
            String script = "java.lang.System.exit(0);";
            executeScript(script, new Object());
        }, "Le script ne devrait pas pouvoir appeler System.exit()");
    }

    @Test
    void testNoAccessToPackages() {
        String script = "typeof Packages";
        Object result = executeScript(script, new Object());
        assertEquals("undefined", Context.toString(result),
                "Le script ne devrait pas avoir accès à 'Packages'");
    }

    @Test
    void testNoAccessToJavaIO() {
        assertThrows(Exception.class, () -> {
            String script = "new java.io.File('/etc/passwd');";
            executeScript(script, new Object());
        }, "Le script ne devrait pas avoir accès à java.io.File");
    }

    @Test
    void testNoAccessToJavaNet() {
        assertThrows(Exception.class, () -> {
            String script = "new java.net.URL('http://evil.com');";
            executeScript(script, new Object());
        }, "Le script ne devrait pas avoir accès à java.net.URL");
    }

    @Test
    void testNoAccessToJavaLangRuntime() {
        assertThrows(Exception.class, () -> {
            String script = "java.lang.Runtime.getRuntime().exec('ls');";
            executeScript(script, new Object());
        }, "Le script ne devrait pas avoir accès à Runtime.exec()");
    }

    @Test
    void testNoAccessToJavaLangProcessBuilder() {
        assertThrows(Exception.class, () -> {
            String script = "new java.lang.ProcessBuilder('ls').start();";
            executeScript(script, new Object());
        }, "Le script ne devrait pas avoir accès à ProcessBuilder");
    }

    @Test
    void testNoAccessToJavaLangClassLoader() {
        assertThrows(Exception.class, () -> {
            String script = "java.lang.ClassLoader.getSystemClassLoader();";
            executeScript(script, new Object());
        }, "Le script ne devrait pas avoir accès à ClassLoader");
    }

    @Test
    void testNoAccessToJavaLangReflect() {
        assertThrows(Exception.class, () -> {
            String script = "java.lang.reflect.Method;";
            executeScript(script, new Object());
        }, "Le script ne devrait pas avoir accès à java.lang.reflect");
    }

    @Test
    void testHasAccessToStandardJavaScript() {
        // Test Array
        String script1 = "[1, 2, 3].length";
        Object result1 = executeScript(script1, new Object());
        assertEquals(3, Context.toNumber(result1), 0.001,
                "Le script devrait avoir accès à Array");

        // Test Object
        String script2 = "var obj = {a: 1, b: 2}; obj.a + obj.b";
        Object result2 = executeScript(script2, new Object());
        assertEquals(3, Context.toNumber(result2), 0.001,
                "Le script devrait avoir accès à Object");

        // Test Math
        String script3 = "Math.max(5, 10)";
        Object result3 = executeScript(script3, new Object());
        assertEquals(10, Context.toNumber(result3), 0.001,
                "Le script devrait avoir accès à Math");

        // Test String
        String script4 = "'hello'.toUpperCase()";
        Object result4 = executeScript(script4, new Object());
        assertEquals("HELLO", Context.toString(result4),
                "Le script devrait avoir accès aux méthodes String");
    }

    @Test
    void testHasAccessToContext() {
        TestContext testContext = new TestContext();
        testContext.value = 42;

        String script = "context.value";
        Object result = executeScript(script, testContext);
        assertEquals(42, Context.toNumber(result), 0.001,
                "Le script devrait avoir accès au context fourni");
    }

    @Test
    void testCanCallContextMethods() {
        TestContext testContext = new TestContext();

        String script = "context.getValue()";
        Object result = executeScript(script, testContext);
        assertEquals(100, Context.toNumber(result), 0.001,
                "Le script devrait pouvoir appeler les méthodes du context");
    }

    @Test
    void testCannotAccessContextPrivateMethods() {
        TestContext testContext = new TestContext();

        assertThrows(Exception.class, () -> {
            // Tente d'accéder à une méthode qui n'existe pas
            String script = "context.privateMethod()";
            executeScript(script, testContext);
        }, "Le script ne devrait pas pouvoir accéder aux méthodes privées");
    }

    @Test
    void testCannotModifyPrototype() {
        String script = """
            Object.prototype.dangerous = function() { return 'hacked'; };
            var obj = {};
            obj.dangerous();
        """;

        // Doit fonctionner mais ne doit pas affecter les autres scopes
        Object result = executeScript(script, new Object());
        assertEquals("hacked", Context.toString(result));

        // Vérifier que cela n'a pas affecté le scope partagé
        String script2 = "var obj = {}; typeof obj.dangerous";
        Object result2 = executeScript(script2, new Object());
        assertEquals("undefined", Context.toString(result2),
                "La modification du prototype ne devrait pas persister entre les exécutions");
    }

    @Test
    void testNoAccessToGetClass() {
        TestContext testContext = new TestContext();

        // Le WrapFactory devrait bloquer l'accès à getClass
        String script = "typeof context.getClass";
        Object result = executeScript(script, testContext);
        assertEquals("undefined", Context.toString(result),
                "Le script ne devrait pas avoir accès à getClass()");
    }

    @Test
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
                "Les scripts complexes devraient fonctionner correctement");
    }

    @Test
    void testArrayManipulation() {
        TestContext testContext = new TestContext();

        String script = """
            var arr = [1, 2, 3, 4, 5];
            var doubled = arr.map(function(x) { return x * 2; });
            var sum = doubled.reduce(function(a, b) { return a + b; }, 0);
            sum;
        """;

        Object result = executeScript(script, testContext);
        assertEquals(30, Context.toNumber(result), 0.001,
                "La manipulation de tableaux devrait fonctionner");
    }

    @Test
    void testExecuteFunction() {
        // Test de la méthode executeFunction
        Scriptable scope = engine.createSecureScope();
        Context cx = Context.enter();
        try {
            // Créer une fonction JavaScript
            String functionScript = "function testFunc(a, b) { return a + b; }; testFunc;";
            Object funcObj = cx.evaluateString(scope, functionScript, "test", 1, null);

            assertTrue(funcObj instanceof Function, "Le résultat devrait être une Function");

            Function func = (Function) funcObj;
            engine.executeFunction(func, 5, 10);

            // Si ça ne throw pas, c'est bon
            assertTrue(true, "executeFunction devrait fonctionner sans erreur");
        } finally {
            Context.exit();
        }
    }

    /**
     * Classe de test pour vérifier l'accès au context
     */
    public static class TestContext {
        public int value;

        public int getValue() {
            return 100;
        }

        @SuppressWarnings("unused")
        private void privateMethod() {
            // Cette méthode ne devrait pas être accessible depuis JS
        }
    }
}