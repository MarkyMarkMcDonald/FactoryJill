import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static FactoryJill.FactoryJill.build;
import static FactoryJill.FactoryJill.factory;

public class BuildTest {

    @Before
    public void before() throws Exception {
        factory("truck", Car.class, ImmutableMap.of(
                "make", "ford",
                "convertible", false,
                "yearsOwned", 5,
                "releaseDate", new Date()
        ));
    }

    @Test
    public void build_allowsOverriddenDefaults() throws Exception {
        Car ford = build("truck", ImmutableMap.of("make", "Ford"));
        assert ford.getMake().equals("Ford");

        Car convertible = build("truck", ImmutableMap.of("convertible", true));
        assert convertible.getConvertible().equals(true);

        Car wellLookedAfter = build("truck", ImmutableMap.of("yearsOwned", 13));
        assert wellLookedAfter.getYearsOwned() == 13;

        Date now = new Date();
        Car rightOffTheShelf = build("truck", ImmutableMap.of("releaseDate", now));
        assert rightOffTheShelf.getReleaseDate().equals(now);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void build_whenFactoryIsNotDefined() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("There is no factory defined for four_door_sedan.");

        build("four_door_sedan");
    }

    @Test
    public void build_whenOverrideAttributeDoesNotExist() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Failed to set yolokittens to Car{make='Chevy'} on class Car, check your override configuration");

        build("truck", ImmutableMap.of("yolokittens", new Car("Chevy")));
    }
}
