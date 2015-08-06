package Presentation;

import Business.Wiki;

public class TestScriptProcessing {
	
	public static void main(String[] args){
		Wiki wiki = new Wiki();
		wiki.extractFromDB();
		wiki.executeScript("script.txt", "log.txt");
		wiki.transferToDB();
		wiki.generateReport("report.html");
	}
}
