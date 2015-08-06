package Business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Persistence.Connector;

public class Wiki{
	HashSet<Link> links = new HashSet<>();
	private static final int PRIO_MIN = 0;
	private static final int PRIO_MAX = 10;
	
	public HashSet<Link> getLinks() {
		return links;
	}

	public void addLink(String ref){
		String name = "";
		try {
			URL article = new URL(ref);
			BufferedReader in = new BufferedReader(new InputStreamReader(article.openStream(),"UTF-8"));
			String line;
			Pattern title = Pattern.compile("<title>(.+?) [—-] (.+?)</title>");
			while ((line = in.readLine()) != null) {
				Matcher m = title.matcher(line);
				if (m.find()) {
					name = m.group(1);
					break;
				}
			}
			Link link = new Link(ref);
			link.setName(name);
			links.add(link);
		}
		catch (MalformedURLException e) {
			e.getMessage();
		}
		catch (IOException e) {
			e.getMessage();
		}
	}
	
	public Link getById(int id){
		for(Link link: links)
			if (link.getId() == id)
				return link;
		return null;
	}
	
	public Link getByRef(String ref){
		for(Link link: links)
			if (link.getRef() == ref)
				return link;
		return null;
	}
	
	public void updateLink(Link l){
		Link link;
		if ((link = getById(l.getId())) != null) {
			link.setName(l.getName());
			link.setPriority(l.getPriority());
			link.setProcStatus(l.getProcStatus());
			link.setRef(l.getRef());
			link.setStudyStatus(l.getStudyStatus());
		}
		else {
			links.add(link);
		}
	}
	
	public void deleteLink(int id){
		links.remove(getById(id));
	}
	
	public void setPriority(int id, int priority){
		Link link;
		if ((link = getById(id)) != null && PRIO_MIN <= priority && priority <= PRIO_MAX) {
			link.setPriority(priority);
		}
		return;
	}
	
	public void setProcStatus(int id, Link.Status status){
		Link link;
		if ((link = getById(id)) != null) {
			link.setProcStatus(status);
		}
		return;
	}
	
	public void setStudyStatus(int id, Link.Status status){
		Link link;
		if ((link = getById(id)) != null) {
			link.setStudyStatus(status);
		}
		return;
	}
	
	public void transferToDB(){
		Connector.transferToDB(links);
	}
	
	public void extractFromDB(){
		links = Connector.extractFromDB();
		for(Link link: links)
			if (link.getId() > Link.getNewId())
				Link.setNewId(link.getId());
	}
	
	public void executeScript(String script, String log){
		try(BufferedReader in = new BufferedReader(new FileReader(script));
			BufferedWriter out = new BufferedWriter(new FileWriter(log));){
			String line = "";
			String command = "";
			while((line = in.readLine()) != null) {
				out.write("command " + line + " was read \n");
				Pattern keyword = Pattern.compile("^(.+?)\\s");
				Matcher m = keyword.matcher(line);
				if (m.find()) command = m.group(1).toUpperCase();
				switch(command) {
					case "ADD": scrAdd(line, out); break;			
					case "READ": scrRead(line, out); break;
					case "UPDATE": scrUpdate(line, out); break;
					case "DELETE": scrDelete(line, out); break;
					case "GETID": scrGetId(line, out); break;
					case "LIST": scrList(line, out); break;
					default: scrDefault(line, out);
				} 
			}
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
	}
	
	private void scrAdd(String line, Writer out) throws IOException{
		String ref = "";
		Pattern keyword = Pattern.compile("Ref\\s*=\\s*\\'([^\\']*?)\\'");
		Matcher m = keyword.matcher(line);
		if (m.find()) {
			ref = m.group(1);
			addLink(ref);
			Link l = getByRef(ref);
			if (l != null)
				out.write("Reference " + ref + " was added" + " , Id: " + l.getId()+"\n");
		}
	}
	
	private void scrRead(String line, Writer out) throws IOException{
		int id = parseIntParameter(line, "Id\\s*=\\s*'([^']*?)'");

		Link link = getById(id);
		if (link == null)
			return;
		System.out.println(link);
		out.write("Link with id " + link.getId() + " was printed"+"\n");
	}
	
	private void scrUpdate(String line, Writer out) throws IOException{
		int id = parseIntParameter(line, "Id\\s*=\\s*'([^']*?)'");
		int priority = parseIntParameter(line, "Priority\\s*=\\s*'([^']*?)'");
		Link.Status studyStatus = parseStatusParameter(line, "StudyStatus\\s*=\\s*'([^']*?)'");
		Link.Status procStatus = parseStatusParameter(line, "ProcStatus\\s*=\\s*'([^']*?)'");
		
		if (PRIO_MIN <= priority && priority <= PRIO_MAX) setPriority(id, priority);
		if (studyStatus != null) setStudyStatus(id, studyStatus);
		if (procStatus != null) setProcStatus(id, procStatus);
		if (getById(id) != null)
			out.write("Link with id" + getById(id).getId() + " was updated"+"\n");
	}
	
	private void scrDelete(String line, Writer out) throws IOException{
		int id = parseIntParameter(line, "Id\\s*=\\s*'([^']*?)'");
		deleteLink(id);
		out.write("Link with id" + getById(id).getId() + " was deleted"+"\n");
	}
	
	private void scrGetId(String line, Writer out) throws IOException{
		String ref = parseStringParameter(line, "Ref\\s*=\\s*'([^']*?)'");
		if (getByRef(ref) != null) {
			System.out.println("Id for the reference - " + ref + " is: " + getByRef(ref).getId());
			out.write("The id for link with reference" + ref + " was obtained"+"\n");
		}
	}
	
	private void scrList(String line, Writer out) throws IOException{
		for(Link link: links) {
			System.out.println("Id = " + link.getId() + 
							   " | Name = " + link.getName() + 
							   " | Priority = " + link.getPriority() + 
							   " | StudyStatus = " + link.getStudyStatus() + 
							   " | ProcessStatus = " + link.getProcStatus() +
							   " | Refernce = " + link.getRef());
		}
		out.write("The list of all references was written"+"\n");
			
	}
	
	private void scrDefault(String line, Writer out) throws IOException{
		out.write("Error! Wrong command!"+"\n");
	}
	
	private int parseIntParameter(String line, String pattern){
		Pattern keyword = Pattern.compile(pattern);
		Matcher m = keyword.matcher(line);
		if (m.find()) try{
			return Integer.parseInt(m.group(1));
			}
			catch (NumberFormatException ne) {
				ne.getMessage();
			}
		return -1;
	}
	
	private String parseStringParameter(String line, String pattern){
		Pattern keyword = Pattern.compile(pattern);
		Matcher m = keyword.matcher(line);
		if (m.find()) try{
			return m.group(1);
			}
			catch (NumberFormatException ne) {
				ne.getMessage();
			}
		return null;
	}
	
	private Link.Status parseStatusParameter(String line, String pattern){
		Pattern keyword = Pattern.compile(pattern);
		Matcher m = keyword.matcher(line);
		if (m.find()) try {
			return Link.Status.valueOf(m.group(1).toUpperCase());
		} 
		catch (IllegalArgumentException ae) {
			ae.getMessage();
		}
		return null;
	}
	
	public void generateReport(String report) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><tmlbody><head>Links</head><body><ul>");
		for(Link link: links)
			sb.append("<li><a href=\"" + link.getRef() + "\">" + link.getName() +"</a></li>");
		sb.append("</body></html>");
		try(BufferedWriter out = new BufferedWriter(new FileWriter(report));) {
			out.write(sb.toString());
		}
		catch (IOException ie) {
			ie.getMessage();
		}
	}
}
