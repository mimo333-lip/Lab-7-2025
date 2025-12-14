package functions;

public interface TabulatedFunctionFactory {
    // Три перегруженных метода для создания табулированных функций
    TabulatedFunction createTabulatedFunction(FunctionPoint[] points);
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount);
    TabulatedFunction createTabulatedFunction(double[] xValues, double[] yValues);
}