exchangews
==========

Proof-of-Concept of Exchange WebService API.
To be kind, it opens also a Tray icon (if possible).


Use 

	gradle bootRun
	
to start the demo then point your browser to

http://localhost:8080/test







# Test server image (Virtual box as IDE)

http://www.microsoft.com/en-us/download/details.aspx?id=5002

# Exchange API
http://msdn.microsoft.com/en-us/office/dn448484.aspx

UserName: contoso\Administrator
Password: pass@word1 

## How to send email using EWS on behalf of user


### Setup
Using the EMS issue a command like

	Set-Mailbox UserMailbox -GrantSendOnBehalfTo UserWhoSends

Then try code like

	var message = new EmailMessage(service);
	message.From = new EmailAddress("boss@exchangetest.com");  
....
See [http://social.technet.microsoft.com/Forums/exchange/en-US/a547a769-204b-4a6a-a62a-3c130345ba93/how-to-send-email-using-ews-on-behalf-of-user-?forum=exchangesvrdevelopment]
Che basti mettere il FROM giusto è confermato qui [http://social.technet.microsoft.com/Forums/exchange/en-US/ac83ed64-adc5-42a7-81c2-123f3ed7bbec/exchange-web-services-how-to-use-delegate-access-to-send-mail?forum=exchangesvrdevelopment]



### More info
http://www.thesoftwaregorilla.com/2010/06/exchange-web-services-example-part-3-exchange-impersonation/
https://www.google.at/search?q=java+exchange+ws+impersionation+token&oq=java+exchange+ws+impersionation+token&aqs=chrome..69i57.13167j0j7&sourceid=chrome&es_sm=93&ie=UTF-8

http://stackoverflow.com/questions/1126684/how-do-i-access-windows-credentials-from-java

### Extra sources
svn checkout svn://svn.code.sf.net/p/msgviewer/code/ msgviewer-code


## Resources

http://kb.mozillazine.org/Outlook_Web_Access

http://davmail.sourceforge.net/index.html

### FAILED: EXJELLO

Cannot get working with Exchange2010: it seems too old to save our day
https://code.google.com/p/exjello/

