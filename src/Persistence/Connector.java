package Persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;

import Business.Link;

public class Connector {
	private static Connection conn = null;
	
	public static Connection getConnection() {
		Properties prop = new Properties();
		try(InputStreamReader in = new InputStreamReader
				(new FileInputStream("DBProperties.txt"),"UTF-8");){
			prop.load(in);
			
			String connString = prop.getProperty("DBConnectionString");
			conn = DriverManager.getConnection(connString);
		}
		catch (SQLException se) {
			se.getMessage();
		}
		catch (IOException ie) {
			ie.getMessage();
		}
		
		return conn;
	}
	
	public static void transferToDB(HashSet<Link> links){
		Connection conn = getConnection();
		String sql = "INSERT INTO link (id, name, ref, priority, studyStatus, procStatus) " + 
				"values(?,?,?,?,?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			for(Link link: links){
				ps.setInt(1, link.getId());
				ps.setString(2, link.getName());
				ps.setString(3, link.getRef());
				ps.setInt(4, link.getPriority());
				ps.setInt(5, link.getStudyStatus().getValue());
				ps.setInt(6, link.getProcStatus().getValue());
				ps.executeUpdate();
			}
			conn.close();
		}
		catch (SQLException se) {
			se.getMessage();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException se) {
				se.getMessage();
			}
		}
	}
	
	public static HashSet<Link> extractFromDB(){
		Connection conn = getConnection();
		HashSet<Link> links = new HashSet<>();
		String sql = "SELECT id, name, ref, priority, studyStatus, procStatus " +
				"FROM link";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Link link = null;
			while(rs.next()){
				link = new Link(rs.getString(3));
				link.setId(rs.getInt(1));
				link.setName(rs.getString(2));
				link.setPriority(rs.getInt(4));
				link.setStudyStatus(Link.Status.values()[rs.getInt(5)]);
				link.setProcStatus(Link.Status.values()[rs.getInt(6)]);
				links.add(link);
			}
			
			sql = "DELETE FROM link";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			conn.close();
		}
		catch (SQLException se) {
			se.getMessage();
		}
		return links;
	}
}
