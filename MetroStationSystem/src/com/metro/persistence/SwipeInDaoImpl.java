package com.metro.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.Instant;



public class SwipeInDaoImpl implements SwipeInDao{
	private Connection connection;
	private UsersDaoImpl usersDao;
	
	public SwipeInDaoImpl() {
		try {
            String MySQLURL = "jdbc:mysql://localhost:3306/metro_system";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(MySQLURL, "root", "Rotten@32217");
        }
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean swipeIn(int metroCardId, int sourceStationId) {
		PreparedStatement preparedStatement = null;
		Timestamp current = Timestamp.from(Instant.now());
		double balance = usersDao.getCardBalance(metroCardId);
		if(balance >= 20.0){
			try{
				preparedStatement = connection.prepareStatement("INSERT INTO JOURNEY VALUES(?,?,?,null,null,null)");
				preparedStatement.setInt(1, metroCardId);
				preparedStatement.setInt(2, sourceStationId);
				preparedStatement.setTimestamp(3, current);
				int i;
				try {
					i = preparedStatement.executeUpdate();
					System.out.println(i+balance);
					return true;
				} 
				catch(SQLIntegrityConstraintViolationException e) {
					System.out.println(e);
					if(e.toString().contains("station")) {
						System.out.println("Invalid Station number");
					}
					else if(e.toString().contains("Duplicate")){
						System.out.println("Card already in use.");
					}
					else {
						System.out.println("Invalid Card number");
					}
				}
			}
			catch(SQLException e) {
				System.out.println(e);
			}
		}
		return false;	
	}
}
