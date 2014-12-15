import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static FactoryJill.FactoryJill.build;
import static FactoryJill.FactoryJill.factory;

public class BuildTest {

    @Before
    public void before() throws Exception {
        factory("truck", Car.class, ImmutableMap.of(
                "make", "ford",
                "convertible", false,
                "yearsOwned", 5,
                "year", new Date()
        ));
    }

    @Test
    public void factory_setsUpReuseableProperties() throws Exception {
        Car pickupTruck = build("truck");

        assert pickupTruck.getMake().equals("ford");
    }

    @Test
    public void build_allowsOverridenDefaults() throws Exception {
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
    public void build_withDynamicOverrides() throws Exception {
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void build_whenOverrideAttributeDoesNotExist() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Failed to set yolokittens to Car{make='Chevy'} on class Car, check your override configuration");

        build("truck", ImmutableMap.of("yolokittens", new Car("Chevy")));
    }

    @Test
    public void factory_whenFactoryAttributeDoesNotExist() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Failed to set smokeSmell to mapleSyrup on class Car, check your factory configuration");

        factory("frutarom", Car.class, ImmutableMap.of("smokeSmell", "mapleSyrup"));
    }
}
