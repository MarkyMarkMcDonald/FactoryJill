import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static FactoryJill.FactoryJill.build;
import static FactoryJill.FactoryJill.factory;

public class FactoryTest {

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
    public void factory_setsUpReusableProperties() throws Exception {
        Car pickupTruck = build("truck");

        assert pickupTruck.getMake().equals("ford");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void factory_whenFactoryAttributeDoesNotExist() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Failed to set smokeSmell to mapleSyrup on class Car, check your factory configuration");

        factory("frutarom", Car.class, ImmutableMap.of("smokeSmell", "mapleSyrup"));
    }
}
