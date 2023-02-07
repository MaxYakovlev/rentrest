package ru.yakovlev.rentrest.security;

import ru.yakovlev.rentrest.model.context.UserContext;

public class SecurityContext {
    private static final ThreadLocal<UserContext> context = new ThreadLocal<>();

    public static void set(UserContext userContext){
        context.set(userContext);
    }

    public static UserContext get(){
        return context.get();
    }

    public static void clear(){
        context.remove();
    }
}
