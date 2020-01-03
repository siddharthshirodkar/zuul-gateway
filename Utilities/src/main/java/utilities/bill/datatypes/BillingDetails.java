package utilities.bill.datatypes;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BillingDetails {

	private Date billingPeriodStartDate;
	private Date billingPeriodEndDate;
	
	private Date issueDate;
	private Date dueDate;
	private int billAmount;
	
	private String contractAccNo;
	private String paymentStatus;
	
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
	public String getContractAccNo() {
		return contractAccNo;
	}
	public void setContractAccNo(String contractAccNo) {
		this.contractAccNo = contractAccNo;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	public abstract String getBillType();
	
	public String getNotificationText() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		return getBillType() +" - "+
				"contractAccNo = "+ getContractAccNo() +","+
				"dueDate = " + sdf.format(getDueDate()) +","+
				"billAmount = Rs: "+getBillAmount() +"/-";
	}
}
