package com.altarix_test_task_android;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public abstract class CategoryC {
    private static void info(){
        System.out.println("Данное приложение выполняет функции калькулятора. \n" +
                "На вход можно подать выражение, в котором можно использовать следующие операторы: \"+-/*\",\n" +
                ", а также \"^\" - возведение в степень, \"%\" - остаток от деления, выражение может иметь вложенные скобки - \"()\",\n" +
                "операндами выражения могут являться целые числа и десятичные (запись через точку).\n" +
                "Для получения информации о приложении введите 'info'\n" +
                "Для вывода истории операций введите 'memory'\n" +
                "Для ввода данных из файла 'Examples/CategoryC/C1.txt' введите 'file'\n" +
                "Для выхода введите 'q'\n"+
                "Для вычисления значения выражения введите его:\n\n");
    }
    private static void read_file_data(Calculator calculator){
        //Файл с примером
        String FILE_NAME = "Examples//CategoryC//C1.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
                try {
                    System.out.println(calculator.calculate(s));
                } catch (Calculator.CalculatorException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        }
    }
    public static void C1() {
        try (Calculator calculator = new Calculator()) {
            info();
            System.out.print("Ввод: ");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();
            while (s.compareTo("q")!=0){
                if (s.trim().compareTo("")!=0){
                    switch (s) {
                        case "info": info(); break;
                        case "memory": calculator.printMemory(); break;
                        case "file": read_file_data(calculator); break;
                        default:
                            try {
                                System.out.println(calculator.calculate(s));
                            } catch (Calculator.CalculatorException e) {
                                System.out.println(e.getMessage());
                            }
                            System.out.println();
                            break;
                    }
                }
                System.out.print("Ввод: ");
                s = scanner.nextLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
