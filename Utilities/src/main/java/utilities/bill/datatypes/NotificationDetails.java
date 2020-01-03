package utilities.bill.datatypes;

import java.text.SimpleDateFormat;

public class NotificationDetails {

	public String notificationText;
	public String notificationSender;
	public String notificationReceiver;
	public String notificationTrigerrer;
	
	public String getNotificationText() {
		return notificationText;
	}
	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}
	public String getNotificationSender() {
		return notificationSender;
	}
	public void setNotificationSender(String notificationSender) {
		this.notificationSender = notificationSender;
	}
	public String getNotificationReceiver() {
		return notificationReceiver;
	}
	public void setNotificationReceiver(String notificationReceiver) {
		this.notificationReceiver = notificationReceiver;
	}
	public String getNotificationTrigerrer() {
		return notificationTrigerrer;
	}
	public void setNotificationTrigerrer(String notificationTrigerrer) {
		this.notificationTrigerrer = notificationTrigerrer;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		return "notificationText = "+ getNotificationText() +"\n"+
				"notificationSender = "+ getNotificationSender() +"\n"+
				"notificationReceiver = " + getNotificationReceiver() +"\n"+
				"notificationTrigerrer =  "+getNotificationTrigerrer() +"\n";
	}
}
