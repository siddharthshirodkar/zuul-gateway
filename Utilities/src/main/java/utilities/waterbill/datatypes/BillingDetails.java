package utilities.waterbill.datatypes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BillingDetails {

	private Date billingPeriodStartDate;
	private Date billingPeriodEndDate;
	
	private Date issueDate;
	private Date dueDate;
	private int billAmount;
	
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
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public int getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(int billAmount) {
		this.billAmount = billAmount;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		return "billingPeriodStartDate = " + sdf.format(getBillingPeriodStartDate())+"\n"+
				"billingPeriodEndDate = " + sdf.format(getBillingPeriodEndDate())+"\n"+
				"issueDate = " + sdf.format(getIssueDate())+"\n"+
				"dueDate = " + sdf.format(getDueDate()) +"\n"+
				"billAmount = Rs: "+getBillAmount()+"/-\n";
	}
	
	
}
