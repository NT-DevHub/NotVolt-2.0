package dev.nottekk.notvolt.utils.apis;

import pw.aru.api.nekos4j.Nekos4J;

/**
 * Neko4JsAPI.
 */
public class Neko4JsAPI {

    /**
     * Constructor should not be called, since it is a utility class that doesn't need an instance.
     * @throws IllegalStateException it is a utility class.
     */
    private Neko4JsAPI() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get an Instance of the Neko4JsAPI.
     */
    public static final Nekos4J imageAPI = new Nekos4J.Builder().build();
}
