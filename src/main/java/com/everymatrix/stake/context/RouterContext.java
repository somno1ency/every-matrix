package com.everymatrix.stake.context;

import java.util.ServiceLoader;
import com.everymatrix.stake.strategy.RouterStrategy;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public class RouterContext {

    private static final ServiceLoader<RouterStrategy> routerServiceLoader = ServiceLoader.load(RouterStrategy.class);

    public static RouterStrategy getStrategy(String path, String method) {
        for (RouterStrategy strategy : routerServiceLoader) {
            if (strategy.isMatch(path, method)) {
                return strategy;
            }
        }

        return null;
    }
}
