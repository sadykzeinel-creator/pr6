import java.util.*;

// STRATEGY

interface ICostCalculationStrategy {
    double calculateCost(TravelRequest request);
}

// Data Model

enum ServiceClass {
    ECONOMY,
    BUSINESS
}

class TravelRequest {
    double distance;
    int passengers;
    ServiceClass serviceClass;
    boolean hasBaggage;
    boolean isChild;
    boolean isSenior;
    double regionalCoefficient;

    public TravelRequest(double distance, int passengers,
                         ServiceClass serviceClass,
                         boolean hasBaggage,
                         boolean isChild,
                         boolean isSenior,
                         double regionalCoefficient) {

        if (distance <= 0 || passengers <= 0)
            throw new IllegalArgumentException("Неверные входные данные!");

        this.distance = distance;
        this.passengers = passengers;
        this.serviceClass = serviceClass;
        this.hasBaggage = hasBaggage;
        this.isChild = isChild;
        this.isSenior = isSenior;
        this.regionalCoefficient = regionalCoefficient;
    }
}

// Concrete Strategies

class PlaneCostStrategy implements ICostCalculationStrategy {

    @Override
    public double calculateCost(TravelRequest r) {
        double base = r.distance * 0.5;

        if (r.serviceClass == ServiceClass.BUSINESS)
            base *= 1.8;

        if (r.hasBaggage)
            base += 50;

        base *= r.passengers;

        base *= r.regionalCoefficient;

        return applyDiscount(base, r);
    }

    private double applyDiscount(double total, TravelRequest r) {
        if (r.isChild) total *= 0.7;
        if (r.isSenior) total *= 0.8;
        if (r.passengers >= 5) total *= 0.9;
        return total;
    }
}

class TrainCostStrategy implements ICostCalculationStrategy {

    @Override
    public double calculateCost(TravelRequest r) {
        double base = r.distance * 0.3;

        if (r.serviceClass == ServiceClass.BUSINESS)
            base *= 1.5;

        base *= r.passengers;
        base *= r.regionalCoefficient;

        return applyDiscount(base, r);
    }

    private double applyDiscount(double total, TravelRequest r) {
        if (r.isChild) total *= 0.8;
        if (r.isSenior) total *= 0.85;
        return total;
    }
}

class BusCostStrategy implements ICostCalculationStrategy {

    @Override
    public double calculateCost(TravelRequest r) {
        double base = r.distance * 0.2;

        base *= r.passengers;
        base *= r.regionalCoefficient;

        return applyDiscount(base, r);
    }

    private double applyDiscount(double total, TravelRequest r) {
        if (r.passengers >= 10) total *= 0.85;
        return total;
    }
}

// Context

class TravelBookingContext {
    private ICostCalculationStrategy strategy;

    public void setStrategy(ICostCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(TravelRequest request) {
        if (strategy == null)
            throw new IllegalStateException("Стратегия не выбрана!");
        return strategy.calculateCost(request);
    }
}

// Client

class TravelBookingSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TravelBookingContext context = new TravelBookingContext();

        System.out.println("Выберите транспорт:");
        System.out.println("1 - Самолет");
        System.out.println("2 - Поезд");
        System.out.println("3 - Автобус");

        int choice = sc.nextInt();

        switch (choice) {
            case 1 -> context.setStrategy(new PlaneCostStrategy());
            case 2 -> context.setStrategy(new TrainCostStrategy());
            case 3 -> context.setStrategy(new BusCostStrategy());
            default -> {
                System.out.println("Ошибка выбора!");
                return;
            }
        }

        System.out.print("Введите расстояние: ");
        double distance = sc.nextDouble();

        System.out.print("Количество пассажиров: ");
        int passengers = sc.nextInt();

        System.out.print("Класс (1-эконом, 2-бизнес): ");
        int cls = sc.nextInt();

        ServiceClass serviceClass =
                (cls == 2) ? ServiceClass.BUSINESS : ServiceClass.ECONOMY;

        System.out.print("Есть багаж? (true/false): ");
        boolean baggage = sc.nextBoolean();

        System.out.print("Ребенок? (true/false): ");
        boolean child = sc.nextBoolean();

        System.out.print("Пенсионер? (true/false): ");
        boolean senior = sc.nextBoolean();

        TravelRequest request =
                new TravelRequest(distance, passengers,
                        serviceClass, baggage,
                        child, senior, 1.1);

        double result = context.calculate(request);

        System.out.println("Итоговая стоимость: " + result);
    }
}
