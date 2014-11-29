import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static lib.FactoryJill.build;
import static lib.FactoryJill.factory;

public class SampleTest {

    @Before
    public void before() {
        factory("truck", Car.class, ImmutableMap.of(
                "make", "ford",
                "convertible", false,
                "yearsOwned", 5,
                "year", new Date()
        ));
    }

    @Test
    public void overrides_handlesMultipleTypes() throws Exception {
        Car ford = build("truck", ImmutableMap.of("make", "Ford"));
        assert ford.getMake().equals("Ford");

        Car convertible = build("truck", ImmutableMap.of("convertible", true));
        assert convertible.getConvertible().equals(true);

        Car wellLookedAfter = build("truck", ImmutableMap.of("yearsOwned", 13));
        assert wellLookedAfter.getYearsOwned() == 13;

        Date now = new Date();
        Car rightOffTheShelf = build("truck", ImmutableMap.of("year", now));
        assert rightOffTheShelf.getYear().equals(now);
    }

    @Test
    public void definedFactories_haveConfiguredProperties() throws Exception {
        Car pickupTruck = (Car) build("truck");

        assert pickupTruck.getMake().equals("ford");
    }
}
