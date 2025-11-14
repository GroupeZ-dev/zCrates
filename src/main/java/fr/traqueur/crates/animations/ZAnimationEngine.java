package fr.traqueur.crates.animations;

import fr.traqueur.crates.api.Logger;
import org.mozilla.javascript.*;

/**
 * Secure JavaScript engine for animation execution.
 * Provides a centralized, safe execution environment with security restrictions.
 */
public class ZAnimationEngine {

    private final Context rhinoContext;

    public ZAnimationEngine() {
        Logger.info("Initializing secure animation engine...");

        this.rhinoContext = Context.enter();
        rhinoContext.setOptimizationLevel(-1);
        rhinoContext.setLanguageVersion(Context.VERSION_ES6);

        // WrapFactory pour bloquer les méthodes Java dangereuses sur TOUS les objets
        // Bloquer les méthodes dangereuses
        WrapFactory secureWrapFactory = new WrapFactory() {
            @Override
            public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
                return new NativeJavaObject(scope, javaObject, staticType) {
                    @Override
                    public Object get(String name, Scriptable start) {
                        // Bloquer les méthodes dangereuses
                        if ("getClass".equals(name) || "class".equals(name) ||
                                "notify".equals(name) || "notifyAll".equals(name) ||
                                "wait".equals(name) || "clone".equals(name) ||
                                "finalize".equals(name) || "hashCode".equals(name)) {
                            return NOT_FOUND;
                        }
                        return super.get(name, start);
                    }
                };
            }
        };

        rhinoContext.setWrapFactory(secureWrapFactory);
    }

    public Scriptable createSecureScope() {
        Scriptable scope = rhinoContext.initSafeStandardObjects();
        scope.setParentScope(null);
        return scope;
    }

    /**
     * Executes a JavaScript function with arguments in a secure context.
     * This is used for callback execution during animations.
     *
     * @param jsFunction the JavaScript function to execute
     * @param args       the arguments to pass to the function
     */
    public void executeFunction(Function jsFunction, Object... args) {
        if (jsFunction == null) {
            return;
        }

        try {
            Scriptable scope = this.createSecureScope();
            Object[] jsArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                jsArgs[i] = Context.javaToJS(args[i], scope);
            }
            jsFunction.call(this.rhinoContext, scope, scope, jsArgs);

        } catch (Exception e) {
            Logger.warning("Error executing JavaScript callback: {}", e.getMessage());
            Logger.debug("Callback error:", e);
        }
    }

    /**
     * Evaluates a JavaScript file content and executes it in a secure scope.
     * Used for loading animation definition files.
     *
     * @param scriptContent the JavaScript code
     * @param fileName      the file name (for error messages)
     * @param scope         the scope to execute in
     */
    public void evaluateFile(String scriptContent, String fileName, Scriptable scope) {
        try {
            rhinoContext.evaluateString(scope, scriptContent, fileName, 1, null);
        } catch (Exception e) {
            Logger.warning("Error evaluating file {}: {}", fileName, e.getMessage());
        }
    }

    /**
     * Closes the engine and releases resources.
     * Should be called on plugin disable.
     */
    public void close() {
        if (rhinoContext != null) {
            Logger.info("Closing animation engine...");
            Context.exit();
        }
    }
}