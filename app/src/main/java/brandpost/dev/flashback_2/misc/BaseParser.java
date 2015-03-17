package brandpost.dev.flashback_2.misc;

import org.jsoup.nodes.Document;

/**
 * Created by Viktor on 2014-12-10.
 */
public abstract class BaseParser<T> {
    /**
     * Interface to return some debug/logging info
     */
    public interface DebugCallback {
        public void returnExecutionTime(long executionTime);
    }

    /**
     * Implement this to get any errors from the parsing
     */
    public interface ErrorCallback {
        public void message(String message);
    }

    /**
     * Set the callback to use when returning logging info such as executiontime of parts of code.
     *
     * @param callback The callback to be used.
     */
    public void setDebugCallback(DebugCallback callback) {
        mLoggerCallback = callback;
    }

    /**
     * Set the callback which will return any errors which happened during parsing
     *
     * @param callback The callback to be used
     */
    public void setErrorCallback(ErrorCallback callback) { mErrorCallback = callback; }

    /**
     * Protected members
     */
    protected DebugCallback mLoggerCallback;
    protected ErrorCallback mErrorCallback;

    /**
     * Abstract methods
     */
    public abstract T parse(Document document);

}
