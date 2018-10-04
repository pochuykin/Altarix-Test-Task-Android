package com.altarix_test_task_android;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс Калькулятор для вычисления значения выражения, в котором можно использовать следующие операторы: "+-/*",
 * а также "^" - возведение в степень, "%" - остаток от деления, выражение может иметь вложенные скобки - "()",
 * операндами выражения могут являться целые числа и десятичные (запись через точку).
 */

public class Calculator implements AutoCloseable, Serializable{
    //Максиальное число выражений в памяти
    private final Integer MEMORY = 10;
    private final String MEMORY_FILENAME = "calculator_memory.dat";
    //Перечисление операторов
    enum Operators {
        open_bracket,
        close_bracket,
        plus,
        minus,
        multiplication,
        division,
        exponentiation,
        mod
    }
    //Соответствие знака и оператора
    private final HashMap<String,Operators> map_operators;
    //Соответствие оператора и его приоритета
    private final HashMap<Operators,Integer> priority_operators;
    //Все операторы кроме скобок
    private String operators_str;

    //Инициаизация
    {
        map_operators = new HashMap<>(Operators.values().length);
        map_operators.put("(",Operators.open_bracket);
        map_operators.put(")",Operators.close_bracket);
        map_operators.put("+",Operators.plus);
        map_operators.put("-",Operators.minus);
        map_operators.put("*",Operators.multiplication);
        map_operators.put("/",Operators.division);
        map_operators.put("^",Operators.exponentiation);
        map_operators.put("%",Operators.mod);

        priority_operators = new HashMap<>(Operators.values().length);
        priority_operators.put(Operators.open_bracket,0);
        priority_operators.put(Operators.close_bracket,0);
        priority_operators.put(Operators.plus,1);
        priority_operators.put(Operators.minus,1);
        priority_operators.put(Operators.mod,1);
        priority_operators.put(Operators.multiplication,2);
        priority_operators.put(Operators.division,2);
        priority_operators.put(Operators.exponentiation,3);

        operators_str = "";
        //объединяем все операторы в одну строку
        for (String s : map_operators.keySet())
            if (priority_operators.get(map_operators.get(s)) != 0)
                operators_str = operators_str + s;
    }

    //Свой класс иссключений для калькулятора
    public class CalculatorException extends Exception{
        CalculatorException() {
            super("При вычислении значения выражения возникла неопределенная ошибка...");
        }
        CalculatorException(String s){
            super(s);
        }
    }
    //Неизвестный символ
    private class UnknownCharacter extends CalculatorException{
        UnknownCharacter(String s) {
            super(String.format("В выражении использован неподдерживаемый символ: %s", s));
        }
    }
    //Некорректный операнд
    private class InvalidOperand extends CalculatorException{
        InvalidOperand(String s) {
            super(String.format("Некорректное значение операнда: %s", s));
        }
    }
    //Некорректный оператор
    private class InvalidOperator extends CalculatorException{
        InvalidOperator(String s) {
            super(String.format("Некорректное значение оператора: %s", s));
        }
    }
    //Неразрешенная операция
    private class InvalidOperation extends CalculatorException{
        InvalidOperation(){
            super("Операция деления на ноль неразрешима.");
        }
    }
    //Несогласованы операторы
    private class UncoordinatedOperators extends CalculatorException{
        UncoordinatedOperators(String s) {
            super(String.format("В выражении не согласованы операторы: %s", s));
        }
    }
    //Несогласованы скобки
    private class UncoordinatedBrackets extends CalculatorException{
        UncoordinatedBrackets(){
            super("В выражении не согласованы скобки.");
        }
    }

    //Абстрактный класс, определяющий единицу выражения
    private abstract class UnitOfExpression<T> implements Serializable {
        private T value;
        UnitOfExpression(){}
        void setValue(T t) {
            this.value = t;
        }
        T getValue() {
            return value;
        }
    }
    //Класс операнда
    private class Operand extends UnitOfExpression<Double> implements Serializable {
        Operand(Double d){
            super();
            super.setValue(d);
        }
        Operand(String s) throws InvalidOperand{
            super();
            this.setValue(s);
        }
        void setValue(String s) throws InvalidOperand{
            try {
                super.value = Double.parseDouble(s);
            }
            catch (NumberFormatException e){
                throw new InvalidOperand(s);
            }
        }
    }
    //Класс оператора
    private class Operator extends UnitOfExpression<Operators> implements Serializable {
        private Integer priority;
        Operator(String s) throws InvalidOperator{
            this.setValue(s);
        }
        void setValue(String s) throws InvalidOperator{
            try {
                super.value = map_operators.get(s);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                throw new InvalidOperator(s);
            }
            priority = priority_operators.get(super.value);
        }
        Integer getPriority() {
            return priority;
        }
    }
    //Класс выражения
    private class Expression implements Serializable{
        private String raw_expression;
        private String expr;
        private Queue<UnitOfExpression> rpn;
        private Double result;
        private String result_str;
        Expression(String s){
            raw_expression = s;
        }
        //Преобразование выражение перед вычислением
        private void reformExpression(){
            expr = raw_expression.replace(" ","");
            //если выражение или содержимое скобки начинается с минуса, добавляем ноль
            if (expr.startsWith("-")) expr = "0" + expr;
            //если выражение внутри скобок начинается с минуса - подставляем ноль
            expr = expr.replaceAll("(\\(\\-)", "(0-");
            //Находим операнды являющиеся десятичными числами без указания целой части .4 == 0.4
            expr = expr.replaceAll("(?<=[^\\d\\.]|^)\\.(?=\\d)","0.");
            //Находим, где между операндом и скобкой нету оператора и ставим оператор умножения
            expr = expr.replaceAll("(?<=[\\d\\)])\\(","*(").replaceAll("\\)(?=\\d)",")*");
        }
        //Проверка выражения на валидность
        private void validate() throws CalculatorException {

            //Находим, где операторы идут друг за другом или когда оператор последний в выражении
            Pattern p = Pattern.compile(String.format("([\\Q%1$s\\E]){2,}|[\\Q%1$s\\E]$", operators_str));
            Matcher m = p.matcher(expr);
            if (m.find()){
                throw new UncoordinatedOperators(m.group());
            }

            //Находим незнакомые символы
            p = Pattern.compile(String.format("[^\\d\\Q%s\\E]",operators_str+".()"));
            m = p.matcher(expr);
            if (m.find()){
                throw new UnknownCharacter(m.group());
            }

            //Находим незнакомые символы
            p = Pattern.compile("[\\d]*\\.{2,}[\\d]*");
            m = p.matcher(expr);
            if (m.find()){
                throw new InvalidOperand(m.group());
            }

            //Проверяем все ли символы были распознаны регулярным выражением
            if (expr.length()!=
                    expr.replaceAll(String.format("(?<![\\Q%1$s\\E\\(])[\\Q%1$s\\E](?![\\Q%1$s\\E])|[()]",operators_str),"").length()
                            +expr.replaceAll("(?<![\\d\\)])(\\d+)(\\.\\d+)?","").length())
                throw new CalculatorException();
        }
        //Генерация Обратной польской записи
        private void generate_rpn() throws UncoordinatedBrackets, InvalidOperand, InvalidOperator {
            //Для всего выражения, в очереди будут хранится операнды и операторы в обратной польской записи
            Queue<UnitOfExpression> unitsOfExpression = new LinkedList<>();

            //Для операндов
            Queue<Operand> operands = new LinkedList<>();
            //Регулярное выражение находит все операторы
            //regex_str = "(?<![\\Q"+operators_str+"\\E\\(])[\\Q"+operators_str+"\\E](?![\\Q"+operators_str+"\\E])|[()]";

            /*Регулярное выражение находит все операнды
             (перед числом не должно быть другого числа (для разбиения) или закрытой скобки)...
             (последовательность цифр - число)...
             (после числа через точку могут идти еще одна последвательность цифр - дробная часть)*/
            String regex_str = "(?<![\\d\\)])(\\d+)(\\.\\d+)?";
            Pattern p = Pattern.compile(regex_str);
            Matcher m = p.matcher(expr);
            while (m.find()){
                operands.add(new Operand(m.group()));
            }

            /*Регулярное выражение находит все операнды и заменяет их на "a"*/
            regex_str = "(?<![\\d\\)])(\\d+)(\\.\\d+)?";
            for (String s : expr.replaceAll(regex_str,"a").split("")) {
                if (s.compareTo("a")==0)
                    unitsOfExpression.add(operands.poll());
                else
                    unitsOfExpression.add(new Operator(s));
            }

            //Очередь операнд/операторов
            rpn = new LinkedList<>();
            //Стек операторов
            Stack<Operator> stack_operators = new Stack<>();
            Operator operator;
            UnitOfExpression u;
            while (!unitsOfExpression.isEmpty()){
                u = unitsOfExpression.poll();
                //если операнд - помещаем в стек
                if (u.getClass() == Operand.class) {
                    rpn.add(u);
                }
                else {
                    //иначе - оператор
                    operator = (Operator)u;
                    switch (operator.getValue()) {
                        //если открытая скобка - помещаем стек
                        case open_bracket:
                            stack_operators.push(operator);
                            break;
                        //если закртыая скобка - берём операторы из стека в выходную строку, пока не встретиться открытая скобка
                        case close_bracket:
                            while (!stack_operators.empty() && stack_operators.peek().getValue() != Operators.open_bracket)
                                rpn.add(stack_operators.pop());
                            //если открытая скобка не встретилась
                            if(stack_operators.empty() || stack_operators.pop().getValue()!=Operators.open_bracket)
                                //в выражении не согласованы скобки
                                throw new UncoordinatedBrackets();
                            break;
                        //если другие операторы
                        default:
                            //пока в стеке есть операторы с приоритетом не ниже текущего, помещаем в выходную строку
                            while (!stack_operators.empty() && stack_operators.peek().getPriority() >= operator.getPriority())
                                rpn.add(stack_operators.pop());
                            //помещаем текущий оператор в стек
                            stack_operators.add(operator);
                            break;
                    }
                }
            }
            //выталкиваем оставшиеся операторы
            while (!stack_operators.empty()) {
                rpn.add(stack_operators.pop());
            }
        }
        //Разбор обратной польской записи и выдача результата
        private Double get_result() throws CalculatorException{
            Queue<UnitOfExpression> rpn1 = new LinkedList<>(rpn);
            //Применяем разбор обратной польской записи
            Stack<UnitOfExpression> stack = new Stack<>();
            Double res;
            while (rpn1.peek()!=null){
                if (rpn1.peek().getClass()==Operand.class) {
                    stack.push(rpn1.poll());
                }
                else {
                    try {
                        res = operation(((Operand) stack.pop()), ((Operand) stack.pop()), ((Operator) rpn1.poll()));
                        stack.push(new Operand(res));
                    }
                    catch (ArithmeticException e){
                        throw new InvalidOperation();
                    }
                    catch (EmptyStackException e){
                        throw new UncoordinatedBrackets();
                    }
                }
            }
            //В результате должен остаться один элемент в стеке, который и является ответом
            if (stack.size()==1 && stack.peek().getClass()==Operand.class)
                return ((Operand)stack.pop()).getValue();
            else
                throw new CalculatorException();
        }
        //Выполнение операции над операндами
        private Double operation(Operand operand2, Operand operand1, Operator operator) throws ArithmeticException, InvalidOperator {
            Double result;
            switch (operator.getValue()) {
                case plus:
                    result = operand1.getValue() + operand2.getValue();
                    break;
                case minus:
                    result = operand1.getValue() - operand2.getValue();
                    break;
                case multiplication:
                    result = operand1.getValue() * operand2.getValue();
                    break;
                case division:
                    if (operand2.getValue().compareTo(0.0)==0)
                        throw new ArithmeticException();
                    else
                        result = operand1.getValue() / operand2.getValue();
                    break;
                case mod:
                    result = operand1.getValue() % operand2.getValue();
                    break;
                case exponentiation:
                    result = Math.pow(operand1.getValue(),operand2.getValue());
                    break;
                default:
                    throw new InvalidOperator(operator.getValue().toString());
            }
            //System.out.println(operand1.getValue()+" "+operator.getValue()+" "+operand2.getValue()+" "+result);
            return result;
        }
        private String get_result_as_string(){
            Double res = (double)Math.round(result*1000000d)/1000000d;
            if (res.toString().endsWith(".0") || !res.toString().contains("."))
                return String.valueOf(Integer.parseInt(res.toString().substring(0,res.toString().length()-2)));
            else
                return String.valueOf(res);
        }
    }
    private LinkedList<Expression> expressions;
    public Calculator(){
        expressions = new LinkedList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MEMORY_FILENAME))){
            Object o;
            while ((o = ois.readObject()) != null) {
                if(o instanceof Expression)
                    expressions.add((Expression)o);
            }
        }
        catch(EOFException ex){
            //ok
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    @Override
    public void close() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MEMORY_FILENAME)))
        {
            for (Expression expr: expressions)
                oos.writeObject(expr);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    //метод для вывода на экран последних обработанных выражений и результатов вычислений
    public void printMemory(){
        System.out.println(String.format("Последние %d операций:",expressions.size()));
        for (Expression expr : expressions) {
            System.out.println(expr.raw_expression+" = "+expr.result_str);
        }
        System.out.println();
    }
    //Главный метод класса, возвращающий результат вычисления выражения
    public String calculate(String expression) throws CalculatorException {
        Expression expr = new Expression(expression);
        expr.reformExpression();
        expr.validate();
        expr.generate_rpn();
        expr.result = expr.get_result();
        expr.result_str = expr.get_result_as_string();
        expressions.add(expr);
        if (expressions.size() > MEMORY)
            expressions.poll();
        return expr.result_str;
    }
}
