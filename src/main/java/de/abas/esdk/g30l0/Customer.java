package de.abas.esdk.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

import java.math.BigDecimal;

@EventHandler(head = CustomerEditor.class)
@RunFopWith(EventHandlerRunner.class)
public class Customer {

	@ButtonEventHandler(field = "yg30l0calcgeoloc", type = ButtonEventType.AFTER)
	public void calcgeolocAfter(CustomerEditor customerEditor) {
		Geolocation geolocation = new OpenStreetMapGeolocationResolver().resolve(customerEditor);
		customerEditor.setLatitude(new BigDecimal(geolocation.getLatitude()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		customerEditor.setLongitude(new BigDecimal(geolocation.getLongitude()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
	}

}
