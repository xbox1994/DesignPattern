package Factory;

public class WtyCarFactory implements CarFactory {
    @Override
    public Car createCar() {
        Engine engine = new Engine();
        Underpan underpan = new Underpan();
        Wheel wheel = new Wheel();
        return new WtyCar(engine, underpan, wheel);
    }
}
