package dataexchange;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CalculatorTest  {
    @Test
    public void evaluatesExpression() {
        Calculator calc = new Calculator();
        assertEquals(calc.evaluate("3+2"), 5);
    }
}