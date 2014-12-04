import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static FactoryJill.FactoryJill.build;
import static FactoryJill.FactoryJill.factory;

public class BuildTest {

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
    public void definedFactories_haveConfiguredProperties() throws Exception {
        Car pickupTruck = (Car) build("truck");

        assert pickupTruck.getMake().equals("ford");
    }

    @Test
    public void overrideDefaults() throws Exception {
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
    public void dynamicOverrides() throws Exception {
        Map<String, Object> dynamicAttributes = new HashMap<>();

        Function<Car, String> lambda = (Car car) -> {
            Random random = new Random();
            Double decision = random.nextDouble();
            if (decision > .5) {
                return "Low Rider";
            } else {
                return "High Rider";
            }
        };

        dynamicAttributes.put("make", lambda);

        Car randomFord = build("truck", dynamicAttributes);

        assert randomFord.getMake().equals("Low Rider") || randomFord.getMake().equals("High Rider");
        assert randomFord.getYearsOwned() == 5;
    }
}
