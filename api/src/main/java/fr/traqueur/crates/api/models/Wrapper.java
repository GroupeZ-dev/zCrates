package fr.traqueur.crates.api.models;

/**
 * A generic wrapper class that holds a delegate object of type T.
 *
 * @param <T> the type of the delegate object
 */
public abstract class Wrapper<T> {

    /** The delegate object being wrapped */
    protected final T delegate;

    /**
     * Constructs a Wrapper with the specified delegate.
     *
     * @param delegate the object to be wrapped
     */
    public Wrapper(T delegate) {
        this.delegate = delegate;
    }

}
