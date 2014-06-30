package com.nttdata.exchangews;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import microsoft.exchange.webservices.data.EmailMessage;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.MessageBody;
import microsoft.exchange.webservices.data.WebCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nttdata.exchangews.utils.InstallCert;
import com.nttdata.exchangews.utils.TrustAllX509TrustManager;

/**
 * Main entry point
 * @author jj
 *
 */
@Controller
@EnableAutoConfiguration(exclude=TestExJello.class)
@ComponentScan
public class ExchangeWSTest implements InitializingBean {

	public static ExchangeWSTest hello;

	private Log logger = LogFactory.getLog(this.getClass());



	@Value("${app.version:UNKNOWN}")
	String version;
	@Value("${app.description:Unknown}")
	String appdesc="";

	@Value("${login}")
	String useremail="test-man@nttdata.com";

	@Value("${password}")
	String password="secret2keep";

	@Value("${domain}")
	String domain="youtDomain";

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return appdesc+"\n Version:"+version;
	}

	@RequestMapping("/about")
	@ResponseBody
	String about() {
		return "The Gradle+SpringBoot+SpringLoaded_2014 JJ's_Demo_is here. Demo_Version:"+version;
	}

	@RequestMapping("/test")
	@ResponseBody
	String testEWS() throws Exception {
		String r="Login test:";

		// TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP1); /*SP2 does not work */

		ExchangeCredentials credentials = new WebCredentials(useremail,
				password/*,domain*/);
		service.setTraceEnabled(true);
		service.setCredentials(credentials);		
		// ??? https://computer.domain.contoso.com/EWS/Exchange.asmx
		// Default url is something like
		// https://mail.domain.com/EWS/Exchange.asmx
		// https://owa.it.nttdata-emea.com/owa/
		// FAILED https://it.nttdata-emea.com/EWS/Exchange.asmx 
		service.setUrl(new URI("https://127.0.0.1/EWS/Exchange.asmx"));
		//service.autodiscoverUrl(useremail);

		try {
			EmailMessage msg= new EmailMessage(service);
			msg.setSubject("EWS Test"); 
			msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Managed API."));
			msg.getToRecipients().add("askbill@microsoft.com");			
			msg.save();			
			//msg.send();
			logger.info("SENT!");
			return r+"GZip?"+service.getAcceptGzipEncoding();
		}catch(Throwable t){
			logger.error("FATAL:",t);
			throw new RuntimeException("FAILED TEST SEND",t);
		}
	}


	public static void main(String[] args) throws Exception {   
		//-Dhttp.proxyHost=10.0.2.2 -Dhttp.proxyPort=808

		Log logger = LogFactory.getLog(ExchangeWSTest.class);

		InstallCert.loadTrustStore(new File("jssecacerts"));
		TrustAllX509TrustManager.trustEveryOne();

		logger.info("http.proxyHost="+System.getProperty("http.proxyHost"));

		//		System.setProperty("http.proxyHost", "10.0.2.2");
		//		System.setProperty("http.proxyPort", "808");
		//		logger.info("http.proxyHost="+System.getProperty("http.proxyHost"));

		// BUG YOU MUST GOT IT BEFORE RUNNING SPRING OR YOU WILL GET UNSUPPORTED ERROR:
		logger.info("Creating Tray ICON");
		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		//Schedule a job for the event-dispatching thread:
		//adding TrayIcon.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				createAndShowGUI();
			}
		});


		//		ApplicationListener<ApplicationPreparedEvent> sayHello = new ApplicationListener<ApplicationPreparedEvent>() {
		//
		//			@Override
		//			public void onApplicationEvent(ApplicationPreparedEvent event) {
		//				System.err.println("\n\n\t DEMO READY\n\n");				
		//			}
		//		};
		SpringApplicationBuilder ab=new SpringApplicationBuilder(ExchangeWSTest.class);
		//		ab.application().addListeners(sayHello);



		ab.run(args);

	}



	private  static void createAndShowGUI() {
		//Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon =
				new TrayIcon(createImage("tray.gif", "tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem aboutItem = new MenuItem("About");
		CheckboxMenuItem loggerCB = new CheckboxMenuItem("Enable Debugging",Logger.getRootLogger().isDebugEnabled());



		MenuItem exitItem = new MenuItem("Exit");

		//Add components to popup menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(loggerCB);

		popup.addSeparator();


		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"This dialog box is run from System Tray");
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						hello.appdesc+"\n Version:"+hello.version);
			}
		});


		loggerCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int cb1Id = e.getStateChange();
				if (cb1Id == ItemEvent.SELECTED){					
					Logger.getRootLogger().setLevel(Level.DEBUG);
					Logger.getRootLogger().debug("DEBUG ENABLED");
				} else {
					Logger.getRootLogger().setLevel(Level.INFO);
					Logger.getRootLogger().info("DEBUG DISABLED");
				}
			}
		});





		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				System.exit(0);
			}
		});
	}

	//Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = Thread.currentThread().getContextClassLoader().getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		hello=this;

	}
}
