import houseUtility.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Введите количество этажей: ");
        House house = new House(input.nextInt());

        System.out.println("Введите интервал между заявками в секундах: ");
        HouseThread applicationsGenerator = new HouseThread(house::generateApplication, input.nextInt() * 1000);

        System.out.println("Введите скорость перехода лифта на следующий этаж в секундах: ");
        HouseThread liftsManager = new HouseThread(house::manageLifts, input.nextInt() * 1000);

        System.out.println("Введите интервал между распределениями заявок по лифтам: ");
        HouseThread applicationManager = new HouseThread(house::manageApplications, input.nextInt() * 1000);

        liftsManager.start();
        applicationsGenerator.start();
        applicationManager.start();




    }
}