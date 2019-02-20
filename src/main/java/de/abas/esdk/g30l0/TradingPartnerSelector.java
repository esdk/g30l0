package de.abas.esdk.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorContact;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;

import java.util.ArrayList;
import java.util.List;

public class TradingPartnerSelector {

	public List<TradingPartner> selectTradingPartners(final DbContext ctx, final String swd, final String zipCode) {
		List<TradingPartner> tradingPartners = new ArrayList<>();
		tradingPartners.addAll(selectFromTradingPartner(Customer.class, ctx, swd, zipCode));
		tradingPartners.addAll(selectFromTradingPartner(CustomerContact.class, ctx, swd, zipCode));
		tradingPartners.addAll(selectFromTradingPartner(Vendor.class, ctx, swd, zipCode));
		tradingPartners.addAll(selectFromTradingPartner(VendorContact.class, ctx, swd, zipCode));
		return tradingPartners;
	}

	private <T extends TradingPartner> List<T> selectFromTradingPartner(Class<T> clazz, final DbContext ctx, final String swd, final String zipCode) {
		return ctx.createQuery(SelectionBuilder.create(clazz).add(Conditions.eq(T.META.swd, swd)).add(Conditions.eq(T.META.zipCode, zipCode)).setTermConjunction(SelectionBuilder.Conjunction.OR).build()).execute();
	}

}
