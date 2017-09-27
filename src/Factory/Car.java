package Factory;

public abstract class Car {
    protected Engine engine;
    protected Underpan underpan;
    protected Wheel wheel;

    public Car(Engine engine, Underpan underpan, Wheel wheel) {
        this.engine = engine;
        this.underpan = underpan;
        this.wheel = wheel;
    }

    public abstract String getInfo();
}
