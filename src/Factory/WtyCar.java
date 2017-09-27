package Factory;

public class WtyCar extends Car {
    public WtyCar(Engine engine, Underpan underpan, Wheel wheel) {
        super(engine, underpan, wheel);
    }

    @Override
    public String getInfo() {
        return "wty de car";
    }
}
