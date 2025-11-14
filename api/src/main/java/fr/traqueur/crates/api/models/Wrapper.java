package fr.traqueur.crates.api.models;

public abstract class Wrapper<T> {

    protected final T handle;

    public Wrapper(T handle) {
        this.handle = handle;
    }

}
