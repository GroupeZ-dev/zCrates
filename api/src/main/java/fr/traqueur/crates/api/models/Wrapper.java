package fr.traqueur.crates.api.models;

public abstract class Wrapper<T> {

    protected final T delegate;

    public Wrapper(T delegate) {
        this.delegate = delegate;
    }

}
