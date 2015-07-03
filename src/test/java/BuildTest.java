import FactoryJill.FactoryJill;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.List;

import static FactoryJill.FactoryJill.build;
import static FactoryJill.FactoryJill.factory;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.containsString;

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
    public void buildMultiple() throws Exception {
        List<Car> cars = FactoryJill.buildMultiple("truck");
        assert cars.size() > 0;
        assert cars.get(0).getMake().equals("ford");
        assert cars.get(1).getMake().equals("ford");

        List<Car> lotsOfCars = FactoryJill.buildMultiple("truck", 20);
        assert lotsOfCars.size() == 20;
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
        factory("edwards", Driver.class, emptyMap());

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("No \"four_door_sedan\" factory has been defined. Defined factories: ["));
        expectedException.expectMessage(containsString("edwards"));
        expectedException.expectMessage(containsString("truck"));

        build("four_door_sedan");
    }

    @Test
    public void build_whenOverrideAttributeDoesNotExist() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Failed to build \"truck\", could not override \"yolokittens\" to \"Driver{name='Edwards'}\" on class \"Car\".");

        Driver driver = new Driver();
        driver.setName("Edwards");
        build("truck", ImmutableMap.of("yolokittens", driver));
    }
}
