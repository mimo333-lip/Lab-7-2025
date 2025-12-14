import functions.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Laboratory Work #7 ===");
        System.out.println("Iterators and Factory Methods\n");
        
        try {
            // Задание 1: Тестирование итераторов
            testIterators();
            
            // Задание 2: Тестирование фабричных методов
            testFactoryMethods();
            
            // Задание 3: Тестирование рефлексии
            testReflection();
            
        } catch (Exception e) {
            System.out.println("Error in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Задание 1: Тестирование итераторов
     */
    private static void testIterators() {
        System.out.println("1. Testing iterators:");
        
        // Создаем табулированные функции
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
        
        System.out.println("  a) ArrayTabulatedFunction iteration:");
        System.out.print("    Points: ");
        for (FunctionPoint point : arrayFunc) {
            System.out.print(point + " ");
        }
        System.out.println();
        
        System.out.println("  b) LinkedListTabulatedFunction iteration:");
        System.out.print("    Points: ");
        for (FunctionPoint point : listFunc) {
            System.out.print(point + " ");
        }
        System.out.println();
        
        // Тестирование исключений в итераторах
        System.out.println("  c) Testing iterator exceptions:");
        
        try {
            var iterator = arrayFunc.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
            iterator.next(); // Должно бросить NoSuchElementException
            System.out.println("    ERROR: Should have thrown NoSuchElementException!");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("    OK: NoSuchElementException caught: " + e.getMessage());
        }
        
        try {
            var iterator = listFunc.iterator();
            iterator.remove(); // Должно бросить UnsupportedOperationException
            System.out.println("    ERROR: Should have thrown UnsupportedOperationException!");
        } catch (UnsupportedOperationException e) {
            System.out.println("    OK: UnsupportedOperationException caught: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Задание 2: Тестирование фабричных методов
     */
    private static void testFactoryMethods() {
        System.out.println("2. Testing factory methods:");
        
        Function cosFunction = new CosFunction();
        TabulatedFunction tf;
        
        System.out.println("  a) Default factory (ArrayTabulatedFunction):");
        tf = TabulatedFunctions.tabulate(cosFunction, 0, Math.PI, 11);
        System.out.println("    Class: " + tf.getClass().getSimpleName());
        System.out.println("    First 3 points: ");
        int count = 0;
        for (FunctionPoint point : tf) {
            if (count++ >= 3) break;
            System.out.println("      " + point);
        }
        
        System.out.println("  b) Switching to LinkedListTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(cosFunction, 0, Math.PI, 11);
        System.out.println("    Class: " + tf.getClass().getSimpleName());
        System.out.println("    First 3 points: ");
        count = 0;
        for (FunctionPoint point : tf) {
            if (count++ >= 3) break;
            System.out.println("      " + point);
        }
        
        System.out.println("  c) Switching back to ArrayTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(cosFunction, 0, Math.PI, 11);
        System.out.println("    Class: " + tf.getClass().getSimpleName());
        System.out.println("    First 3 points: ");
        count = 0;
        for (FunctionPoint point : tf) {
            if (count++ >= 3) break;
            System.out.println("      " + point);
        }
        
        System.out.println();
    }
    
    /**
     * Задание 3: Тестирование рефлексии
     */
    private static void testReflection() {
        System.out.println("3. Testing reflection:");
        
        System.out.println("  a) Creating ArrayTabulatedFunction via reflection:");
        TabulatedFunction f1 = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 0.0, 10.0, 3);
        System.out.println("    Class: " + f1.getClass().getSimpleName());
        System.out.println("    Function: " + f1);
        
        System.out.println("  b) Creating ArrayTabulatedFunction via reflection (arrays):");
        TabulatedFunction f2 = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 0.0, 10.0, 3);
        System.out.println("    Class: " + f2.getClass().getSimpleName());
        System.out.println("    Function: " + f2);
        
        System.out.println("  c) Creating LinkedListTabulatedFunction via reflection (points):");
        FunctionPoint[] points = {
            new FunctionPoint(0, 0),
            new FunctionPoint(10, 10)
        };
        TabulatedFunction f3 = TabulatedFunctions.createTabulatedFunction(
            LinkedListTabulatedFunction.class, points);
        System.out.println("    Class: " + f3.getClass().getSimpleName());
        System.out.println("    Function: " + f3);
        
        System.out.println("  d) Tabulating via reflection:");
        TabulatedFunction f4 = TabulatedFunctions.tabulate(
            LinkedListTabulatedFunction.class, new SinFunction(), 0, Math.PI, 11);
        System.out.println("    Class: " + f4.getClass().getSimpleName());
        System.out.println("    First 5 points: ");
        int count = 0;
        for (FunctionPoint point : f4) {
            if (count++ >= 5) break;
            System.out.println("      " + point);
        }
        
        System.out.println("  e) Testing error handling:");
        try {
            TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, -10.0, -5.0, 2);
            System.out.println("    OK: Correct parameters handled.");
        } catch (IllegalArgumentException e) {
            System.out.println("    ERROR: Unexpected exception: " + e.getMessage());
        }
        
        try {
            TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0.0, 10.0, 5);
            System.out.println("    OK: Correct numeric parameters handled.");
        } catch (IllegalArgumentException e) {
            System.out.println("    ERROR: Unexpected exception: " + e.getMessage());
        }
        
        try {
            TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0.0, 10.0, 1);
            System.out.println("    ERROR: Should have thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            System.out.println("    OK: IllegalArgumentException caught for pointCount=1: " + e.getMessage());
        }
        
        System.out.println();
    }
}
