package functions;

/**
 * Функция косинуса
 */
public class CosFunction implements Function {
    @Override
    public double getLeftDomainBorder() {
        return Double.NEGATIVE_INFINITY;
    }
    
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
    
    @Override
    public double getFunctionValue(double x) {
        return Math.cos(x);
    }
    
    @Override
    public String toString() {
        return "cos(x)";
    }
}