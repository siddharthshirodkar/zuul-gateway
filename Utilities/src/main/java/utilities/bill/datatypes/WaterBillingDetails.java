package utilities.bill.datatypes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WaterBillingDetails extends BillingDetails{

	private Date billingPeriodStartDate;
	private Date billingPeriodEndDate;
	
	public Date getBillingPeriodStartDate() {
		return billingPeriodStartDate;
	}
	public void setBillingPeriodStartDate(Date billingPeriodStartDate) {
		this.billingPeriodStartDate = billingPeriodStartDate;
	}
	public Date getBillingPeriodEndDate() {
		return billingPeriodEndDate;
	}
	public void setBillingPeriodEndDate(Date billingPeriodEndDate) {
		this.billingPeriodEndDate = billingPeriodEndDate;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		return  "billType = "+ getBillType() +"\n"+
				"contractAccNo = "+ getContractAccNo() +"\n"+
				"billingPeriodStartDate = " + sdf.format(getBillingPeriodStartDate())+"\n"+
				"billingPeriodEndDate = " + sdf.format(getBillingPeriodEndDate())+"\n"+
				"issueDate = " + sdf.format(getIssueDate())+"\n"+
				"dueDate = " + sdf.format(getDueDate()) +"\n"+
				"billAmount = Rs: "+getBillAmount()+"/-\n"+
				"paymentStatus =  "+getPaymentStatus();
	}
	
	@Override
	public String getBillType() {
		return "Water Bill";
	}
}
