package utilities.bill.datatypes;

import java.text.SimpleDateFormat;

public class ElectricityBillingDetails extends BillingDetails{

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		return "billType = "+ getBillType() +"\n"+
				"contractAccNo = "+ getContractAccNo() +"\n"+
				"issueDate = " + sdf.format(getIssueDate()) +"\n"+
				"dueDate = " + sdf.format(getDueDate()) +"\n"+
				"billAmount = Rs: "+getBillAmount() +"/-\n"+
				"paymentStatus =  "+getPaymentStatus() +"\n";
	}

	@Override
	public String getBillType() {
		return "Electricty Bill";
	}
}
