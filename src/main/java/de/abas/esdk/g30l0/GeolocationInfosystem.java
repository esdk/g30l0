package de.abas.esdk.g30l0;

import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorContact;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

import java.util.ArrayList;
import java.util.List;

@EventHandler(head = GeoLocation.class, row = GeoLocation.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class GeolocationInfosystem {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(DbContext ctx, GeoLocation infosystem) {
		for (final TradingPartner tradingPartner : selectTradingPartners(ctx, infosystem.getCustomersel())) {
			GeoLocation.Row row = infosystem.table().appendRow();
			row.setCustomer(tradingPartner);
			row.setZipcode(tradingPartner.getZipCode());
			row.setTown(tradingPartner.getTown());
			row.setState(tradingPartner.getStateOfTaxOffice());
		}
	}

	private List<TradingPartner> selectTradingPartners(final DbContext ctx, final String swd) {
		List<TradingPartner> tradingPartners = new ArrayList<>();
		tradingPartners.addAll(selectFromTradingPartner(Customer.class, ctx, swd));
		tradingPartners.addAll(selectFromTradingPartner(CustomerContact.class, ctx, swd));
		tradingPartners.addAll(selectFromTradingPartner(Vendor.class, ctx, swd));
		tradingPartners.addAll(selectFromTradingPartner(VendorContact.class, ctx, swd));
		return tradingPartners;
	}

	private <T extends TradingPartner> List<T> selectFromTradingPartner(Class<T> clazz, final DbContext ctx, final String swd) {
		return ctx.createQuery(SelectionBuilder.create(clazz).add(Conditions.eq(T.META.swd, swd)).build()).execute();
	}

}
