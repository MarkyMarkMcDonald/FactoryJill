import lib.FactoryJill;
import org.junit.Test;

import java.util.Date;

import static lib.FactoryJill.override;

public class SampleTest {

    @Test
    public void overrides_handleAllTypes() throws Exception {
        Car ford = FactoryJill.build(Car.class, override("make", "Ford"));
        assert ford.getMake().equals("Ford");

        Car convertible = FactoryJill.build(Car.class, override("convertible", true));
        assert convertible.getConvertible().equals(true);

        Car wellLookedAfter = FactoryJill.build(Car.class, override("yearsOwned", 13));
        assert wellLookedAfter.getYearsOwned() == 13;

        Date now = new Date();
        Car rightOffTheShelf = FactoryJill.build(Car.class, override("year", now));
        assert rightOffTheShelf.getYear().equals(now);
    }
}
