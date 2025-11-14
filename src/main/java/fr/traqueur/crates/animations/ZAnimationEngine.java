package fr.traqueur.crates.animations;

import fr.traqueur.crates.api.Logger;
import org.mozilla.javascript.*;

public class ZAnimationEngine {

    private final Context rhinoContext;

    public ZAnimationEngine() {
        Logger.info("Initializing minimal animation engine...");

        this.rhinoContext = Context.enter();
        rhinoContext.setOptimizationLevel(-1);
        rhinoContext.setLanguageVersion(Context.VERSION_ES6);

        // WrapFactory pour bloquer les méthodes Java dangereuses sur TOUS les objets
        rhinoContext.setWrapFactory(new WrapFactory() {
            @Override
            public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
                return new NativeJavaObject(scope, javaObject, staticType) {
                    @Override
                    public Object get(String name, Scriptable start) {
                        // Bloquer les méthodes dangereuses
                        if ("getClass".equals(name) || "class".equals(name) ||
                                "notify".equals(name) || "notifyAll".equals(name) ||
                                "wait".equals(name) || "clone".equals(name)) {
                            return NOT_FOUND;
                        }
                        return super.get(name, start);
                    }
                };
            }
        });

        if (testEngine()) {
            Logger.info("Minimal animation engine ready");
        }
    }

    public Object executeAnimation(String script, Object animationContext) {
        try {
            Scriptable isolatedScope = rhinoContext.initSafeStandardObjects();
            isolatedScope.setParentScope(null);
            isolatedScope.put("context", isolatedScope, Context.javaToJS(animationContext, isolatedScope));
            return rhinoContext.evaluateString(isolatedScope, script, "animation", 1, null);
        } catch (Exception e) {
            Logger.warning("Animation script error: {}", e.getMessage());
            return null;
        }
    }

    public boolean testEngine() {
        try {
            String testScript = """
                context !== undefined;
            """;
            Object result = executeAnimation(testScript, new Object());
            return Context.toBoolean(result);
        } catch (Exception e) {
            return false;
        }
    }

    public void close() {
        if (rhinoContext != null) {
            Context.exit();
        }
    }


}
