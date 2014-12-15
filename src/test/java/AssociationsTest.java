import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static FactoryJill.FactoryJill.build;
import static FactoryJill.FactoryJill.factory;

public class AssociationsTest {

    @Before
    public void before() throws Exception {
        factory("oldDriver", Driver.class, ImmutableMap.of("name", "Dale", "age", 25));

        factory("youngDriver", Driver.class, ImmutableMap.of("name", "BooBoo", "age", 8));

        factory("truck_with_oldDriver", Car.class,
                ImmutableMap.of("make", "ford"), ImmutableMap.of("driver", "oldDriver"));

        factory("truck_with_youngDriver", Car.class,
                ImmutableMap.of("make", "ford"), ImmutableMap.of("driver", "youngDriver"));
    }

    @Test
    public void associations() throws Exception {
        Car raceCar = build("truck_with_oldDriver");
        assert raceCar.getDriver().getName().equals("Dale");

        Car trike = build("truck_with_youngDriver");
        assert trike.getDriver().getName().equals("BooBoo");
    }
}
