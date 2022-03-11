package dcModelClasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Author:  Dequan Chen, Ph.D.;  CopyRight By: Mayo Clinic
 * Date: 05/24/2014  
 */


public class DayClock {
	private Calendar currentCalendar;
	private String currentDate = "";
	private String currentDateTime = "";
	private long cuurentDateTimeInMilliSeconds = 0L;
	private int currentTimeInSeconds = 0;
	private int CurrentTimeHourNumber = 0;
	
	private String current24HourClockTime = "";
	private String current12HourClockTime = "";
	
	private String yesterdayDate = "";	

	public DayClock() {
		this.currentCalendar  = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(("yyyy/MM/dd"));
		this.currentDate = sdf.format(this.currentCalendar.getTime());
		
		sdf = new SimpleDateFormat(("MM/dd/yyyy HH:mm:ss"));	
	    this.currentDateTime = sdf.format(this.currentCalendar.getTime());	    
	    this.cuurentDateTimeInMilliSeconds = this.currentCalendar.getTimeInMillis();
	    //System.out.println("*1* currTime is: " + this.currentDateTime + " with milliseconds value - \'" + this.cuurentDateTimeInMilliSeconds + "\'");
	    
	    //long currTimeMilliSecondsvalue = this.currentCalendar.getTime().getTime();
	    //System.out.println("*2* currTime is: " + this.currentDateTime + " with milliseconds value - \'" + currTimeMilliSecondsvalue + "\'");
	    
        int hrs = this.currentCalendar.get(Calendar.HOUR_OF_DAY);
        int min = this.currentCalendar.get(Calendar.MINUTE);
        int sec = this.currentCalendar.get(Calendar.SECOND);
        //System.out.println("*** hour:minute:second is: " + h + ":" + m + ":" + s  );        
        this.currentTimeInSeconds = (hrs*60 + min) *60 + sec;
        this.CurrentTimeHourNumber = hrs;
        //System.out.println("*** currentTimeInSeconds is: " + currentTimeInSeconds);  
        
        this.current24HourClockTime = hrs + ":" + min + ":" + sec;
        //System.out.println("*** current24HourClockTime is: " + current24HourClockTime); 
        
        if (hrs < 12){
    		this.current12HourClockTime = hrs + ":" + min + ":" + sec + " am";
    	} else if (hrs == 12){
    		this.current12HourClockTime = "12:00:00 pm";
    	} else {
    		this.current12HourClockTime = (hrs - 12) + ":" + min + ":" + sec + " pm";
    	}
        
        //this.currentCalendar.add(Calendar.DATE, -1);
		//sdf = new SimpleDateFormat(("yyyy/MM/dd"));
		this.yesterdayDate = getPastNDaysDate4CurrentDay(1);
        //System.out.println("*** current12HourClockTime is: " + current12HourClockTime);		
        
	}


	public String getYesterdayDate() {
		return yesterdayDate;
	}


	public void setYesterdayDate(String yesterdayDate) {
		this.yesterdayDate = yesterdayDate;
	}


	public String getCurrentDate() {
		return currentDate;
	}


	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}


	public String getCurrentDateTime() {
		return currentDateTime;
	}


	public void setCurrentDateTime(String currentDateTime) {
		this.currentDateTime = currentDateTime;
	}


	public long getCuurentDateTimeInMilliSeconds() {
		return cuurentDateTimeInMilliSeconds;
	}


	public void setCuurentDateTimeInMilliSeconds(long cuurentDateTimeInMilliSeconds) {
		this.cuurentDateTimeInMilliSeconds = cuurentDateTimeInMilliSeconds;
	}


	public int getCurrentTimeInSeconds() {
		return currentTimeInSeconds;
	}


	public void setCurrentTimeInSeconds(int currentTimeInSeconds) {
		this.currentTimeInSeconds = currentTimeInSeconds;
	}


	public int getCurrentTimeHourNumber() {
		return CurrentTimeHourNumber;
	}


	public void setCurrentTimeHourNumber(int currentTimeHourNumber) {
		CurrentTimeHourNumber = currentTimeHourNumber;
	}


	public String getCurrent24HourClockTime() {
		return current24HourClockTime;
	}


	public void setCurrent24HourClockTime(String current24HourClockTime) {
		this.current24HourClockTime = current24HourClockTime;
	}


	public String getCurrent12HourClockTime() {
		return current12HourClockTime;
	}


	public void setCurrent12HourClockTime(String current12HourClockTime) {
		this.current12HourClockTime = current12HourClockTime;
	}
	
	public static String getPastNDaysDate4CurrentDay (int pastNDaysNumber){
		Calendar currentCalendar  = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(("yyyy/MM/dd"));
		currentCalendar.add(Calendar.DATE, -pastNDaysNumber);
		String pastNDaysDate = sdf.format(currentCalendar.getTime());
		return pastNDaysDate;
	}

	public static String calculateTimeUsed(String startDateTime, String endDateTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		 
		Date d1 = null;
		Date d2 = null;
 
		d1 = format.parse(startDateTime);
		d2 = format.parse(endDateTime);

		//in milliseconds
		long diff = d2.getTime() - d1.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

//		System.out.print(diffDays + " days, ");
//		System.out.print(diffHours + " hours, ");
//		System.out.print(diffMinutes + " minutes, ");
//		System.out.print(diffSeconds + " seconds.");
		
		String timeUsed = "";
		if (diffDays != 0){
			timeUsed = timeUsed + diffDays + " days ";
		}
		if (diffHours != 0){
			timeUsed = timeUsed + diffHours + " hours ";
		}
		
		if (diffMinutes != 0){
			timeUsed = timeUsed + diffMinutes + " minutes ";
		}
		
		timeUsed = timeUsed + diffSeconds + " seconds ";
		
		
		return timeUsed;		
	}//end calculateTimeUsed

}//end class
