package de.abas.esdk.g30l0;

import de.abas.erp.common.type.IdImpl;
import de.abas.erp.db.EditorAction;
import de.abas.erp.db.exception.CommandException;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerEditor;
import org.junit.Test;

import static de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CustomerCalculateGeolocationTest extends AbstractTest {

	@Test
	public void canFillGeolocation() {
		Customer customer = ctx.load(Customer.class, IdImpl.valueOf(CUSTOMER.tradingPartner.id().toString()));
		CustomerEditor customerEditor = customer.createEditor();
		try {
			customerEditor.open(EditorAction.UPDATE);
			customerEditor.invokeButton("yg30l0calcgeoloc");
			assertThat(customerEditor.getLatitude().doubleValue(), is(closeTo(49.3953008, 0.1)));
			assertThat(customerEditor.getLongitude().doubleValue(), is(closeTo(8.440276, 0.1)));
		} catch (CommandException e) {
			e.printStackTrace();
		} finally {
			if (customerEditor.active()) {
				customerEditor.abort();
			}
		}
	}

}
