package com.altarix_test_task_android;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class CategoryB {
    public static void B1(){
        String[] brackets = null;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryB//B1.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String s = reader.readLine();
            brackets = s.replace(" ","").split("");
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (brackets != null){
            //Словарь соответствия открывающихся-закрывающихся скобок
            HashMap<String,String> map = new HashMap<>(3);
            map.put(")","(");
            map.put("}","{");
            map.put("]","[");
            Stack<String> stack = new Stack<>();
            String br2;
            //Последовательно помещаем в стек скобку, если на вершине стека ей не соответвует такая же открывающаяся
            for (String br : brackets){
                br2 = map.get(br);
                if (!stack.isEmpty() && br2!=null && stack.peek().compareTo(br2)==0)
                    stack.pop();
                else
                    stack.push(br);
                //если в начало стека попала закрывающаяся скобка, то скобочное выражение неверное
                if (stack.size()==1 && map.get(stack.peek()) != null)
                    break;
            }
            if (stack.isEmpty())
                System.out.println("SUCCESS");
            else
                System.out.println("FAIL");
        }
    }
    public static void B2(){
        int N = -1;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryB//B2.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            N = Integer.parseInt(reader.readLine());
            if (N<=0) throw new NumberFormatException();
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (NumberFormatException e){
            System.out.println("Необходимо ввести целое положительное число!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (N != -1) {
            Random r = new Random();
            int[] m = new int[N * N];
            for (int i = 0; i < N * N; i++)
                m[i] = r.nextInt(N * N * 2);
            Arrays.sort(m);
            //Задача А
            int[][] a = new int[N][N];
            int count = 0;
            int i_min = 0, i_max = N - 1, j_min = 0, j_max = N - 1;
            while (count < N * N) {
                for (int j = j_min; j <= j_max; j++)
                    a[i_min][j] = m[count++];
                i_min++;
                for (int i = i_min; i <= i_max; i++)
                    a[i][j_max] = m[count++];
                j_max--;
                for (int j = j_max; j >= j_min; j--)
                    a[i_max][j] = m[count++];
                i_max--;
                for (int i = i_max; i >= i_min; i--)
                    a[i][j_min] = m[count++];
                j_min++;
            }
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++)
                    System.out.print(a[i][j]+" ");
                System.out.println();
            }

            //Задача Б
            int[][] b = new int[N][N];
            count = 0;
            //начинаем с этих координат
            int x = -1, y = 1;
            while (count < N*N){
                //двигаемся вверх и вправо пока не упремся в стенку
                while (y > 0 && x < N-1) b[--y][++x] = m[count++];
                if (y == 0) {
                    if (x == N-1 && y < N-1) y++; //если уперлись в верхний правый угол, двигаемся вниз
                    else if (x < N-1) x++; //если уперлись вверх, двигаемся вправо
                }
                else if (y < N-1) y++; //если упёрлись вправо, двигаемся вниз
                if (count < N*N) b[y][x] = m[count++];

                //двигаемся вниз и влево пока не упремся в стенку
                while (y < N-1 && x > 0) b[++y][--x] = m[count++];
                if (x == 0) {
                    if (y == N-1 && x < N-1) x++;//если уперлись в нижний левый угол, двигаемся вправо
                    else if (x < N-1) y++; //если уперлись вверх, двигаемся вправо
                }
                else if (x < N-1) x++; //если упёрлись вниз, двигаемся впарво
                if (count < N*N) b[y][x] = m[count++];
            }
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++)
                    System.out.print(b[i][j]+" ");
                System.out.println();
            }
        }
    }
    public static void B3(){
        int N = -1;
        //Файл с примером
        String FILE_NAME = "Examples//CategoryB//B3.txt";
        //Пробуем открыть файл
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            N = Integer.parseInt(reader.readLine());
            if (N<=0) throw new NumberFormatException();
        } catch (IOException e) {
            System.out.println("Файла с именем " + FILE_NAME + " не существует!");
        } catch (NumberFormatException e){
            System.out.println("Необходимо ввести целое положительное число!");
        } catch (Exception e) {
            System.out.println("Что-то пошло не так...");
            e.printStackTrace();
        }
        if (N != -1) {
            Random r = new Random();
            //генерируемая матрица
            int[][] m = new int[N][N];
            //для хранения пути, в каждой ячейке массива будет хранится индекс ячейки, путь через которую обеспечивает минимальный путь
            int[] s = new int[N * N];
            Arrays.fill(s, -1);
            //заполняем рандомными значениями и выводим в консоль
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    m[i][j] = r.nextInt(10);
                    System.out.print(m[i][j] + " ");
                }
                System.out.println();
            }
            //рассчитываем кратчайший путь рекурсией
            sub_B3(m, 0, 0, 0, s);
            int k = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (i == 0 && j == 0) System.out.print("A ");
                    else if (i == N - 1 && j == N - 1) System.out.print("B");
                    else if (s[k] == i * N + j) {
                        System.out.print("* ");
                        k = s[k];
                    } else System.out.print(m[i][j] + " ");
                }
                System.out.println();
            }
        }
    }
    private static int sub_B3(int[][] m, int i, int j, int sum, int[] s) {
        sum += m[i][j];
        int min, min2;
        if (i < m.length - 1)
            if (j < m.length - 1) {
                //смотрим куда идти, вниз или вправо
                min = sub_B3(m, i + 1, j, sum, s);
                min2 = sub_B3(m, i, j + 1, sum, s);
                if (min2 < min) {
                    min = min2;
                    //запоминаем в каком направлении из этой точки минимальный путь
                    s[i * m.length + j] = i * m.length + (j + 1);
                } else {
                    s[i * m.length + j] = (i + 1) * m.length + j;
                }
            } else {
                min = sub_B3(m, i + 1, j, sum, s);
                s[i * m.length + j] = (i + 1) * m.length + j;
            }
        else if (j < m.length - 1) {
            min = sub_B3(m, i, j + 1, sum, s);
            s[i * m.length + j] = i * m.length + (j + 1);
        } else min = sum;
        return min;
    }
}
