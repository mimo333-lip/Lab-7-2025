package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 * Утилитарный класс для работы с табулированными функциями
 * Реализует паттерны "Фабричный метод" и рефлексию
 */
public class TabulatedFunctions {
    // Приватное статическое поле фабрики
    private static TabulatedFunctionFactory factory = 
        new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
    
    /**
     * Установка фабрики для создания табулированных функций
     */
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }
    
    /**
     * Получение текущей фабрики
     */
    public static TabulatedFunctionFactory getTabulatedFunctionFactory() {
        return factory;
    }
    
    // === Методы создания через фабрику ===
    
    /**
     * Создание табулированной функции через фабрику (по точкам)
     */
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    
    /**
     * Создание табулированной функции через фабрику (по границам и количеству точек)
     */
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    /**
     * Создание табулированной функции через фабрику (по массивам X и Y)
     */
    public static TabulatedFunction createTabulatedFunction(double[] xValues, double[] yValues) {
        return factory.createTabulatedFunction(xValues, yValues);
    }
    
    // === Методы создания через рефлексию ===
    
    /**
     * Создание табулированной функции через рефлексию (по точкам)
     */
    public static TabulatedFunction createTabulatedFunction(
            Class<? extends TabulatedFunction> functionClass, 
            FunctionPoint[] points) {
        try {
            // Получаем конструктор с параметром FunctionPoint[]
            Constructor<? extends TabulatedFunction> constructor = 
                functionClass.getConstructor(FunctionPoint[].class);
            
            // Создаем объект
            return constructor.newInstance((Object) points);
            
        } catch (NoSuchMethodException | SecurityException | 
                 InstantiationException | IllegalAccessException | 
                 IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to create function: " + e.getMessage(), e);
        }
    }
    
    /**
     * Создание табулированной функции через рефлексию (по границам и количеству точек)
     */
    public static TabulatedFunction createTabulatedFunction(
            Class<? extends TabulatedFunction> functionClass,
            double leftX, double rightX, int pointsCount) {
        try {
            // Получаем конструктор с параметрами double, double, int
            Constructor<? extends TabulatedFunction> constructor = 
                functionClass.getConstructor(double.class, double.class, int.class);
            
            // Создаем объект
            return constructor.newInstance(leftX, rightX, pointsCount);
            
        } catch (NoSuchMethodException | SecurityException | 
                 InstantiationException | IllegalAccessException | 
                 IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to create function: " + e.getMessage(), e);
        }
    }
    
    /**
     * Создание табулированной функции через рефлексию (по массивам X и Y)
     */
    public static TabulatedFunction createTabulatedFunction(
            Class<? extends TabulatedFunction> functionClass,
            double[] xValues, double[] yValues) {
        try {
            // Получаем конструктор с параметрами double[], double[]
            Constructor<? extends TabulatedFunction> constructor = 
                functionClass.getConstructor(double[].class, double[].class);
            
            // Создаем объект
            return constructor.newInstance(xValues, yValues);
            
        } catch (NoSuchMethodException | SecurityException | 
                 InstantiationException | IllegalAccessException | 
                 IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to create function: " + e.getMessage(), e);
        }
    }
    
    // === Методы табулирования функций ===
    
    /**
     * Табулирование функции через фабрику
     */
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        
        double step = (rightX - leftX) / (pointsCount - 1);
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            xValues[i] = x;
            yValues[i] = function.getFunctionValue(x);
        }
        
        return createTabulatedFunction(xValues, yValues);
    }
    
    /**
     * Табулирование функции через рефлексию
     */
    public static TabulatedFunction tabulate(
            Class<? extends TabulatedFunction> functionClass,
            Function function, double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        
        double step = (rightX - leftX) / (pointsCount - 1);
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            xValues[i] = x;
            yValues[i] = function.getFunctionValue(x);
        }
        
        return createTabulatedFunction(functionClass, xValues, yValues);
    }
    
    // === Методы чтения/записи (бинарные) ===
    
    /**
     * Запись табулированной функции в бинарный поток
     */
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeInt(function.getPointCount());
            
            for (int i = 0; i < function.getPointCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                dos.writeDouble(point.getX());
                dos.writeDouble(point.getY());
            }
        }
    }
    
    /**
     * Чтение табулированной функции из бинарного потока (через текущую фабрику)
     */
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointCount = dis.readInt();
            double[] xValues = new double[pointCount];
            double[] yValues = new double[pointCount];
            
            for (int i = 0; i < pointCount; i++) {
                xValues[i] = dis.readDouble();
                yValues[i] = dis.readDouble();
            }
            
            return createTabulatedFunction(xValues, yValues);
        }
    }
    
    /**
     * Чтение табулированной функции из бинарного потока (через рефлексию)
     */
    public static TabulatedFunction inputTabulatedFunction(
            Class<? extends TabulatedFunction> functionClass, InputStream in) throws IOException {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointCount = dis.readInt();
            double[] xValues = new double[pointCount];
            double[] yValues = new double[pointCount];
            
            for (int i = 0; i < pointCount; i++) {
                xValues[i] = dis.readDouble();
                yValues[i] = dis.readDouble();
            }
            
            return createTabulatedFunction(functionClass, xValues, yValues);
        }
    }
    
    // === Методы чтения/записи (текстовые) ===
    
    /**
     * Запись табулированной функции в текстовый поток
     */
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println(function.getPointCount());
            
            for (int i = 0; i < function.getPointCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                writer.printf(Locale.US, "%.10f %.10f%n", point.getX(), point.getY());
            }
        }
    }
    
    /**
     * Чтение табулированной функции из текстового потока (через текущую фабрику)
     * Использует StreamTokenizer
     */
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        
        // Читаем количество точек
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number of points");
        }
        int pointCount = (int) tokenizer.nval;
        
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];
        
        // Читаем точки
        for (int i = 0; i < pointCount; i++) {
            // Читаем x
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Expected x value at point " + i);
            }
            xValues[i] = tokenizer.nval;
            
            // Читаем y
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Expected y value at point " + i);
            }
            yValues[i] = tokenizer.nval;
        }
        
        return createTabulatedFunction(xValues, yValues);
    }
    
    /**
     * Чтение табулированной функции из текстового потока (через рефлексию)
     * Использует StreamTokenizer
     */
    public static TabulatedFunction readTabulatedFunction(
            Class<? extends TabulatedFunction> functionClass, Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        
        // Читаем количество точек
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number of points");
        }
        int pointCount = (int) tokenizer.nval;
        
        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];
        
        // Читаем точки
        for (int i = 0; i < pointCount; i++) {
            // Читаем x
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Expected x value at point " + i);
            }
            xValues[i] = tokenizer.nval;
            
            // Читаем y
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Expected y value at point " + i);
            }
            yValues[i] = tokenizer.nval;
        }
        
        return createTabulatedFunction(functionClass, xValues, yValues);
    }
    
    // === Дополнительные методы ===
    
    /**
     * Вычисление производной табулированной функции в точке
     */
    public static double derivative(TabulatedFunction function, double x) {
        int pointCount = function.getPointCount();
        
        if (x < function.getLeftDomainBorder() || x > function.getRightDomainBorder()) {
            return Double.NaN;
        }
        
        // Поиск индекса точки
        int index = -1;
        for (int i = 0; i < pointCount - 1; i++) {
            try {
                double x1 = function.getPointX(i);
                double x2 = function.getPointX(i + 1);
                if (x >= x1 && x <= x2) {
                    index = i;
                    break;
                }
            } catch (FunctionPointIndexOutOfBoundsException e) {
                return Double.NaN;
            }
        }
        
        if (index == -1) {
            return Double.NaN;
        }
        
        try {
            double x1 = function.getPointX(index);
            double y1 = function.getPointY(index);
            double x2 = function.getPointX(index + 1);
            double y2 = function.getPointY(index + 1);
            
            return (y2 - y1) / (x2 - x1);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }
}