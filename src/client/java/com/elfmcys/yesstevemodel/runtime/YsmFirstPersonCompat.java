package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class YsmFirstPersonCompat {
    private static boolean initialized;
    private static Class<?> apiClass;
    private static Method isEnabledMethod;
    private static Method isRenderingPlayerMethod;
    private static Method registerPlayerHandlerMethod;

    private YsmFirstPersonCompat() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            apiClass = Class.forName("dev.tr7zw.firstperson.api.FirstPersonAPI");
            isEnabledMethod = apiClass.getMethod("isEnabled");
            isRenderingPlayerMethod = apiClass.getMethod("isRenderingPlayer");
            registerPlayerHandlerMethod = apiClass.getMethod("registerPlayerHandler", Object.class);

            Class<?> offsetHandlerClass = Class.forName("dev.tr7zw.firstperson.api.PlayerOffsetHandler");
            Object offsetHandler = Proxy.newProxyInstance(
                offsetHandlerClass.getClassLoader(),
                new Class<?>[] {offsetHandlerClass},
                new OffsetHandlerInvocation()
            );
            registerPlayerHandlerMethod.invoke(null, offsetHandler);
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.debug("YSM First Person Model compat unavailable", throwable);
            apiClass = null;
            isEnabledMethod = null;
            isRenderingPlayerMethod = null;
            registerPlayerHandlerMethod = null;
        }

        initialized = true;
    }

    public static boolean isRenderingPlayer() {
        initialize();
        if (apiClass == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(isEnabledMethod.invoke(null)) && Boolean.TRUE.equals(isRenderingPlayerMethod.invoke(null));
        } catch (Throwable throwable) {
            return false;
        }
    }

    private static final class OffsetHandlerInvocation implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("applyOffset".equals(method.getName()) && args != null && args.length >= 4) {
                return args[3];
            }
            if ("toString".equals(method.getName())) {
                return "YsmFirstPersonCompatOffsetHandler";
            }
            return null;
        }
    }
}
