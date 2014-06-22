package com.example.ersinvaadinproject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;



import com.vaadin.ui.Notification;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

@SuppressWarnings("serial")
@Theme("ersinvaadinproject")
public class ErsinvaadinprojectUI extends UI {
	final String retrievalName = "malecustomers.pdf";
	private final static Action ACTION_DELETE = new Action("Delete");
	private final static Action ACTION_EDIT = new Action("Edit");
	private String status="a";
	TabSheet tabmenu;
	VerticalLayout tab1,tab2,tab3;
	HorizontalLayout buttonlayout;
	TextField name,surname;
	ComboBox gendercombo,citycombo,report;
	Button save,cancel,submit,generatereport;
	Table customertable;
	Customer customer;
	CheckBox active;
	DateField datefield;
	Date date;
	SimpleDateFormat formatter;
	String birthdate;
	Notification notification;
	BeanItemContainer<Customer> beancontainer;
	private FieldGroup fieldGroup;
	Item clickitem;
	JasperPrint jasperPrint;
	JasperViewer jasperviewer;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ErsinvaadinprojectUI.class)
	public static class Servlet extends VaadinServlet {
	}

	
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		buttonlayout=new HorizontalLayout();
		buttonlayout.setMargin(true);
		datefield=new DateField("BirthDate");
		
		ArrayList<String> reports=new ArrayList<String>();
		reports.add("Male Customers");reports.add("Customers born in Istanbul");
		report=new ComboBox("Report type:",reports);
		
		
		
		submit=new Button("Submit");
		
		active=new CheckBox("Active");
		
		customertable=new Table();
		customertable.setSizeFull();
		customertable.setSelectable(true);
		customertable.setMultiSelect(true);
		customertable.setImmediate(true);
		
		
		try{
		
			DatabaseHandler.connect();
			beancontainer=DatabaseHandler.getCustomers();
			customertable.setContainerDataSource(beancontainer);
			customertable.setVisibleColumns( new Object[] {"name","surname","gender","birthdate",
					"city","active"} );
			customertable.setColumnHeaders( new String[] {"Name","Surname","Gender","BýrthDate",
					"BýrthCýty","Actýve/Passýve"});
			
			customertable.addItemClickListener(new ItemClickListener() {
				
				@Override
				public void itemClick(ItemClickEvent event) {
					if(event.getButton()==event.BUTTON_RIGHT){
						/*openCustomerWindow(event.getItem(),"Edit Customer");*/
						clickitem=event.getItem();
					}
					
				}
			});
			customertable.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					if (ACTION_DELETE == action) {
						try{
							DatabaseHandler.connect();
							DatabaseHandler.deleteCustomer((Customer) target);
							beancontainer.removeItem(target);
							DatabaseHandler.getConn().close();
							
						}
						catch (Exception e) {
							 e.printStackTrace();
						}
					}
					
					if(ACTION_EDIT==action){
						try{
							
							openCustomerWindow(clickitem, "Edit Customer");
							
						}
						catch (Exception e) {
							 e.printStackTrace();
						}
					}
					
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					
					return new Action[]
							{ ACTION_DELETE,ACTION_EDIT };
				}
			});
			DatabaseHandler.getConn().close();
            DatabaseHandler.getStatement().close();
			}
			catch (Exception e) {
				 e.printStackTrace();
			}
		
		
		ArrayList<String> genders=new ArrayList<String>();
		genders.add("Male"); genders.add("Female");
		name=new TextField("Customer Name");
		surname=new TextField("Surname");
		gendercombo=new ComboBox("Gender", genders);
		ArrayList<String> city=new ArrayList<String>();
		city.add("Ýstanbul");city.add("Ankara");city.add("Ýzmir");city.add("Adana");city.add("Antalya");
		city.add("Trabzon");city.add("Kayseri");city.add("Gümüþhane");
		citycombo=new ComboBox("BirthCity",city);
		
		
		save = new Button("Save");
		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try{
					if(name.getValue().equals("")||surname.getValue().equals("")||
							gendercombo.getValue().equals("")||citycombo.getValue().equals("")||datefield.getValue().equals("")){
						Notification.show("Please fill in all the fields!",Notification.TYPE_WARNING_MESSAGE);
					}
					else{
						DatabaseHandler.connect();
						date=datefield.getValue();
						formatter = new SimpleDateFormat("yyyy-MM-dd");
						birthdate = formatter.format(date);
						if(active.getValue()==true)
						{
							status="Active";
						}
						else
							status="Passive";
						customer=new Customer(0, name.getValue().toString(), surname.getValue().toString(), 
							gendercombo.getValue().toString(),birthdate,citycombo.getValue().toString(),status);
						DatabaseHandler.Register(customer);
						beancontainer=DatabaseHandler.getCustomers();
						customertable.setContainerDataSource(beancontainer);
						customertable.setVisibleColumns( new Object[] {"name","surname","gender","birthdate",
								"city","active"} );
						customertable.setColumnHeaders( new String[] {"Name","Surname","Gender","BirthDate",
								"BirthCity","Active/Passive"});
						DatabaseHandler.getConn().close();
		                DatabaseHandler.getStatement().close();

						Notification.show("Successfully registered. Please refresh the page!",Notification.TYPE_WARNING_MESSAGE);
						
					}
				}
				catch(Exception e)
	            {
	                e.printStackTrace();
	                
	            }
				
		        
			}
		});
		
		cancel=new Button("Cancel");
		cancel.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				name.setValue("");surname.setValue("");gendercombo.setValue("");citycombo.setValue("");
				datefield.setData("");active.setValue(false);
				
			}
		});
		
		generatereport=new Button("Generate");
		generatereport.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				 
				try {
					DatabaseHandler.connect();
					if(report.getValue()=="Male Customers"){
					jasperPrint=JasperFillManager.fillReport("D:/ImonaCloud/workspace/ErsinVaadinProject/build/reports/malecustomers.jasper", null,DatabaseHandler.getConn());
					JasperViewer.viewReport(jasperPrint, false);
					}
					
					if(report.getValue()=="Customers born in Istanbul"){
						jasperPrint=JasperFillManager.fillReport("D:/ImonaCloud/workspace/ErsinVaadinProject/build/reports/Istanbul.jasper", null,DatabaseHandler.getConn());
						JasperViewer.viewReport(jasperPrint, false);
						
					}
					DatabaseHandler.getConn().close();
				} catch (JRException e) {
					
					e.printStackTrace();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
					
			}
			
		});
		
		
		tabmenu=new TabSheet();
		tabmenu.setWidth("1024px");
		
		
		buttonlayout.addComponent(save);
		buttonlayout.addComponent(cancel);
		
		tab1=new VerticalLayout();
		tab1.setMargin(true);
		tab1.addComponent(name);
		tab1.addComponent(surname);
		tab1.addComponent(gendercombo);
		tab1.addComponent(citycombo);
		tab1.addComponent(datefield);
		tab1.addComponent(active);
		tab1.addComponent(buttonlayout);
		
		tab2=new VerticalLayout();
		tab2.setImmediate(true);
		tab2.addComponent(customertable);
		
		tab3=new VerticalLayout();
		tab3.addComponent(report);
		tab3.addComponent(generatereport);
		tab3.setHeight("200px");
		
		tabmenu.addTab(tab1,"Customer Info");
		tabmenu.addTab(tab2, "List of Customers");
		tabmenu.addTab(tab3, "Reports");
		layout.addComponent(tabmenu);
	}

	private void openCustomerWindow(Item beanItem, String caption) {
		Window window = new Window(caption);
		window.setModal(true);
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		fieldGroup = new BeanFieldGroup<Customer>(Customer.class);
		fieldGroup.setItemDataSource(beanItem);
		for (Object propertyId : fieldGroup.getUnboundPropertyIds()) {
		layout.addComponent(fieldGroup.buildAndBind(propertyId));
		}
		layout.addComponent(submit);
		submit.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(clickitem.getItemPropertyIds().toString());
				
				
			}
		});
		getUI().addWindow(window);
		}
	
	
}