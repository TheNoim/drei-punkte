package dreipunkte;

public class ThreadLock implements RunLoop {
    public Object mutex = new Object();
    static ThreadLock shared;

    @Override
    public void setup() {
        ThreadLock.shared = this;
    }

    @Override
    public void runLoop() {

    }

    @Override
    public void shutdown() {

    }
}
