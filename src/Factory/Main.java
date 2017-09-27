package Factory;

public class Main {
    public static void main(String[] args) {
        CarFactory carFactory = new WtyCarFactory();
        Car car = carFactory.createCar();
        String info = car.getInfo();
        System.out.println(info);
    }
}
