package com.nttdata.exchangews;

import java.net.URI;

import microsoft.exchange.webservices.data.EmailMessage;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.MessageBody;
import microsoft.exchange.webservices.data.WebCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Main entry point
 * @author jj
 *
 */
@Controller
@EnableAutoConfiguration()
@ComponentScan
public class HelloWorld {

	private Log logger = LogFactory.getLog(this.getClass());

	@Value("${app.version:UNKNOWN}")
	String version;

	String useremail="test-man@nttdata.com";
	String password="secret2keep";
	String domain="youtDomain";

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
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

		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);

		ExchangeCredentials credentials = new WebCredentials(useremail,
				password,domain);
		service.setTraceEnabled(true);
		service.setCredentials(credentials);		
		// ??? https://computer.domain.contoso.com/EWS/Exchange.asmx
		// https://owa.it.nttdata-emea.com/owa/
		service.setUrl(new URI("https://owa.it.nttdata-emea.com/EWS/Exchange.asmx"));
		//service.autodiscoverUrl(useremail);

		EmailMessage msg= new EmailMessage(service);
		msg.setSubject("EWS Test"); 
		msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Managed API."));
		msg.getToRecipients().add("jj@gioorgi.com");
		msg.send();

		return r+"GZip?"+service.getAcceptGzipEncoding();
	}


	public static void main(String[] args) throws Exception {   
		//-Dhttp.proxyHost=10.0.2.2 -Dhttp.proxyPort=808

		Log logger = LogFactory.getLog(HelloWorld.class);

		logger.info("http.proxyHost="+System.getProperty("http.proxyHost"));

		System.setProperty("http.proxyHost", "10.0.2.2");
		System.setProperty("http.proxyPort", "808");

		logger.info("http.proxyHost="+System.getProperty("http.proxyHost"));

		//		ApplicationListener<ApplicationPreparedEvent> sayHello = new ApplicationListener<ApplicationPreparedEvent>() {
		//
		//			@Override
		//			public void onApplicationEvent(ApplicationPreparedEvent event) {
		//				System.err.println("\n\n\t DEMO READY\n\n");				
		//			}
		//		};
		SpringApplicationBuilder ab=new SpringApplicationBuilder(HelloWorld.class);
		//		ab.application().addListeners(sayHello);



		ab.run(args);

	}
}
