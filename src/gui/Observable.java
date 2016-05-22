package gui;

/**
 * Created by kutase123 on 20.05.2016.
 */
public interface Observable {
    void subscribe (Observer o);
    void dispose (Observer o); // unsubscribe
    void notifySubscribers ();
}
