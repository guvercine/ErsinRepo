package com.example.ersinvaadinproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Table;

public class DatabaseHandler {
	
	private static Connection conn=null;
   

	private static Statement statement=null;
    private static ResultSet resultset;
    static BeanItemContainer<Customer> customers;
    private static PreparedStatement prstmt;
    public static Statement connect() throws Exception{
    	
    	
    	
    	Class.forName("com.mysql.jdbc.Driver"); 
        
    	conn=DriverManager.getConnection("jdbc:mysql://localhost/test","root","root");
             
        statement=conn.createStatement();
        
		return statement;
        
    }
    
    public static void Register(Customer c) throws Exception{
    	
    	statement.execute("insert into customer (Name,Surname,Gender,BirthDate,BirthCity,Active)"+
                "values ('"+c.getName()+"','"+c.getSurname()+"','"+c.getGender()
                +"','"+c.getBirthdate()+"','"+c.getCity()+"','"+c.getActive()+"')");
    	
    	
    	
    }
    
    public static BeanItemContainer<Customer> getCustomers() throws Exception{
    	customers=new BeanItemContainer<Customer>(Customer.class);
    	
    	resultset=statement.executeQuery("select * from customer");
    	while(resultset.next()){
    		customers.addItem(new Customer(resultset.getInt("CustomerID"),resultset.getString("Name"),
    				resultset.getString("Surname"),resultset.getString("Gender"),resultset.getString("BirthDate"),
    				resultset.getString("BirthCity"),resultset.getString("Active"))); 
    		
    		
    	}
    	
    	return customers;
    }
    
    public static void deleteCustomer(Customer c) throws Exception{
    	
    	statement.executeUpdate("delete from customer where CustomerID="+c.getId());
    }
    
    public static int getid() throws Exception{
    	resultset=statement.executeQuery("select * from customer");
    	resultset.first();
    		return resultset.getInt(1);
    	
    }
    
    public static void updateCustomer(Customer c) throws Exception{
    	
    	prstmt=conn.prepareStatement("update customer set Name=?,Surname=?,Gender=?,"+
                "BirthDate=?,BirthCity=?,Active=? where CustomerID=? ");
        prstmt.setString(1,c.getName());
        prstmt.setString(2,c.getSurname());
        prstmt.setString(3,c.getGender());
        prstmt.setString(4,c.getBirthdate());
        prstmt.setString(5,c.getCity());
        prstmt.setString(6,c.getActive()); 
        prstmt.setInt(7,c.getId());
        prstmt.executeUpdate();
    }

    public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		DatabaseHandler.conn = conn;
	}

	public static Statement getStatement() {
		return statement;
	}

	public static void setStatement(Statement statement) {
		DatabaseHandler.statement = statement;
	}
    

}
