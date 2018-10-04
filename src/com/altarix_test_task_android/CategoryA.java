package com.altarix_test_task_android;

import java.io.*;
import java.util.*;

public abstract class CategoryA {

    public static void A1(){
        //Объявляем двумерный массив чисел с плавающей точкой, для хранения координат точек
        double[][] points = null;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryA//A1.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            points = new double[4][2];
            /*В файле 4 строки. Первые 3 строки - координаты вершин треугольника,
            четвертая строка - рассматриваемая точка.*/
            for (int i = 0; i < 4; i++) {
                //Раделяем строку по "пробелу"
                String[] sPoint = reader.readLine().split(" ");
                //В каждой строке по 2 координате, приводим string к double
                points[i][0] = Double.parseDouble(sPoint[0]);
                points[i][1] = Double.parseDouble(sPoint[1]);
            }
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (NumberFormatException e){
            System.out.println("Необходимо ввести числа - координаты трех точек!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (points != null) {
            //Проверка на треугольник - площадь трейгольника
            //if ((points[0][0]-points[2][0])*(points[1][1]-points[2][1])-(points[1][0]-points[2][0])*(points[0][1]-points[2][1])==0)
            //    System.out.println("Треугольник вырожденный!");
            //Определим, с какой стороны лежит точка относительно каждого вектора (AB,BC,CA)
            double[] a = new double[3];
            for (int i = 0; i < 3; i++) {
                //a[i] = (Dx - Ax) * (Ay - By) - (Dy - Ay) * (Ay - By) // для AB
                a[i] = (points[3][0]-points[i][0])*(points[i][1]-points[(i+1)%3][1])-(points[3][1]-points[i][1])*(points[i][0]-points[(i+1)%3][0]);
            }

            //Точка не может лежать одноврменно на всех сторонах трегольника
            //Если все точки треугольника совпадают, а рассматриваемая точка не совпадает с ними
            if (a[0]==0 && a[1]==0 && a[2]==0 &&
                    (points[0][0] != points[3][0] || points[0][1] != points[3][1])
                    )
                System.out.println("OUT");
            //если точка относительно всех векторов лежит с одной стороны (справа или слева), значит точка внутри треугольника
            else if (a[0]>=0 && a[1]>=0 && a[2]>=0 || a[0]<=0 && a[1]<=0 && a[2]<=0)
                System.out.println("IN");
            else
                System.out.println("OUT");
        }
    }

    public static void A2(){
        //Объявляем матрицу
        ArrayList<ArrayList<Double>> matrix = null;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryA//A2.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            matrix = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                //Раделяем строку по "пробелу"
                String[] lines = line.split(" ");
                //Каждый элемент строки приводим к integer и добавляем в лист
                ArrayList<Double> a = new ArrayList<>(lines.length);
                for (String s : lines)
                    a.add(Double.parseDouble(s));
                matrix.add(a);
            }
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (NumberFormatException e){
            System.out.println("Необходимо ввести матрицу из чисел!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (matrix != null){
            //Сумма двух диагоналей
            double d1 = 0, d2 = 0;
            //Индексы для проходу по диагоналям
            int s1 = 0, s2 = matrix.size()-1;
            for (ArrayList<Double> a: matrix){
                if (s1 != s2){
                    d1 += a.get(s1);
                    d2 += a.get(s2);
                }
                s1++;
                s2--;
            }
            System.out.println(Math.abs(d1-d2));
        }
    }

    public static void A3() {
        //Размер лестницы
        int n = -1;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryA//A3.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            n = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (NumberFormatException e){
            System.out.println("Необходимо ввести целое положительное число!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (n != -1) {
            for (int i = 1; i <= n; i++) {
                for (int j = 0; j < n - i; j++)
                    System.out.print(" ");
                for (int j = 0; j < i; j++)
                    System.out.print("#");
                System.out.println();
            }
        }
    }

    public static void A4(){
        //Объявляем матрицу
        int[] a = null;
        int k = -1;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryA//A4.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            a = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).sorted().toArray();
            k = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (NumberFormatException e){
            System.out.println("Необходимо ввести целые положительные числа!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (a != null && k != -1){
            int n = a.length;
            int res = 0;
            for (int i = 0; i <= n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if ((a[i] + a[j]) % k == 0)
                        res++;
                }
            }
            System.out.println(res);
        }
    }

    public static void A5(){
        ArrayList<String> matrix = null;
        ArrayList<String> pattern = null;
        //Файл с примером. Формат - сначала вводится матрица, потом паттерн.
        //Программа понимает когда в вводе появляется паттерн, т.к. он меньшего размера, чем матрица
        String FILE_NAME = "Examples//CategoryA//A5_2.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            matrix = new ArrayList<>();
            pattern = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                //до того момента пока не появятся строки меньшего размера, это матрица
                if (!pattern.isEmpty() ||
                        (!matrix.isEmpty() && matrix.get(matrix.size()-1).length() > line.length())
                        )
                    pattern.add(line);
                else
                    matrix.add(line);
            }
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует");
            e.printStackTrace();
        }
        if (matrix != null) {
            //Координаты для найденного окна
            int X=-1,Y=-1;
            //Счетчик совпавших подряд строк паттерна
            int count;
            //Проходим все возможные строки матрицы в поисках начала паттерна -
            // достаточно просмотреть первые matrix.size()-pattern.size()+1 строк
            // и пока окно не будет найдено (Y!=-1)
            for (int i = 0; i < matrix.size()-pattern.size()+1 && Y == -1; i++) {
                count = 0;
                //номер столбца, с которого преположительно начинается паттерн
                X = matrix.get(i).lastIndexOf(pattern.get(0));
                //пока есть совпадения и окно еще не найдено
                while (X != -1 && Y == -1) {
                    //если в следующей строке матрицы совпадение с соответствующей строкой паттерна в том же столбце, идем дальше
                    count++;
                    while (count!=0 && count != pattern.size()) {
                        if (matrix.get(i+count).lastIndexOf(pattern.get(count),X) == X)
                            count++;
                        else
                            count = 0;
                    }
                    if (count != pattern.size())
                        X = matrix.get(i).lastIndexOf(pattern.get(0),X-1);
                    else
                        Y = i;
                }
            }
            if (Y == -1)
                System.out.println("FAIL");
            else
                System.out.println("("+Y+", "+X+")");

        }
    }
}
