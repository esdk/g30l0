package de.abas.esdk.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = GeoLocation.class, row = GeoLocation.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class GeolocationInfosystem {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(DbContext ctx, GeoLocation infosystem) {
		for (final TradingPartner tradingPartner : new TradingPartnerSelector().selectTradingPartners(ctx, infosystem.getCustomersel(), infosystem.getZipcodesel())) {
			GeoLocation.Row row = infosystem.table().appendRow();
			row.setCustomer(tradingPartner);
			row.setZipcode(tradingPartner.getZipCode());
			row.setTown(tradingPartner.getTown());
			row.setState(tradingPartner.getStateOfTaxOffice());
			Geolocation geolocation = new OpenStreetMapGeolocationResolver().resolve(tradingPartner);
			row.setLatitude(geolocation.getLatitude().toString());
			row.setLongitude(geolocation.getLongitude().toString());
		}
	}

}
