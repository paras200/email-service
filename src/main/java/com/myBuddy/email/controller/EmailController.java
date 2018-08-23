package com.myBuddy.email.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myBuddy.email.model.EmailDetails;

@SuppressWarnings("deprecation")
@RestController
@CrossOrigin(origins = "*")
// @RequestMapping(path="/email")
public class EmailController {
	
	private String stg = "STG  -  ";

	private static Map<String,String> subjectMap = new HashMap<String,String>();
    static {
    	subjectMap.put("new-project-added", "New Project created - Enrollment open !!");
    	subjectMap.put("new-project-added-po", "New Projec created ");
    	subjectMap.put("payment-done", "Payment done successfully");
    	subjectMap.put("payment-failed", "Payment Failed");
    	subjectMap.put("add-credit", "Credits added to your profile");
    	
    	subjectMap.put("withdraw-credit", "Withdraw credit request - Successful");
    	subjectMap.put("txn-success", "Transaction - Successful");
    	subjectMap.put("txn-failed", "Transaction faliure");
    	subjectMap.put("add-credit-failed", "Credit addition faliure ");
    	subjectMap.put("req-rcv-direct-deposit", "Credit addition Request - Direct deposit ");
    	subjectMap.put("direct-deposit-success", "Direct deposit success");

    	subjectMap.put("new-user", "Welcome to My Buddy");
    	subjectMap.put("new-enrollment-po", "New enrollment");
    	subjectMap.put("new-enrollment-buddy", "New enrollment");
    }
    
    @Autowired
    JavaMailSender mailSender;
     
    @Autowired
    VelocityEngine velocityEngine;
    
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    
	@RequestMapping("/sendMail")
	public String sendMail() throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
		List<String> emailTo = new ArrayList<>();
		emailTo.add("agnihotri.paras@gmail.com");
		emailTo.add("sinhanil19@gmail.com");
		emailTo.add("er.mr.bhushan@gmail.com");
		try {
			helper.setTo(emailTo.toArray(new String[emailTo.size()]));
			helper.setFrom("My-Buddy");
			helper.setSubject(stg+"My Buddy Email Service");
			helper.setText(
					"<p>Dear Owners:<br></p><p><br>I am a test email to cirtify that email service is up<br></p><p><br> "
					+ "If you have any doubts solve it within yourself :).<br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
					true);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		mailSender.send(message);
		return "Mail Sent Success!";
	}

	
	@PostMapping("/sendMail")
	public String sendMail(@RequestBody EmailDetails details) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(details.toList.toArray(new String[details.toList.size()]));
			helper.setText("<p>Hello,<br></p><p><br>"+details.body
			+ "<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
			helper.setSubject(stg+details.subject);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		mailSender.send(message);
		return "Mail Sent Success!";
	}
	
	
   
    @PostMapping("/sendTemplateMail")
	public String addToQueue(@RequestBody EmailDetails details) {
    	
    	executorService.execute(new Runnable() {
    	    public void run() {
    	    	try {
    	    	sendByTemplate(details);
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    }
    	});
		return "new mail thread created";
	}
    
    
    //@PostMapping("/sendTemplateMail")
	public String sendByTemplate(@RequestBody EmailDetails details) {
    	
    	if (details.template == null || details.template.trim().equals(""))	return "Template name not specified";
    	if (details.paramMap == null || details.paramMap.isEmpty()) return "Parameters not specified";
        
        MimeMessagePreparator preparator = getMessagePreparator(details);
         
        try {
            mailSender.send(preparator);
            System.out.println("Message has been sent.............................");
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
        return "Mail Sent Success!";
	}
	private MimeMessagePreparator getMessagePreparator(final EmailDetails details){
        
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
 
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setSubject(stg+(String) subjectMap.get(details.getTemplate()));
             
                helper.setFrom("My-Buddy");
                helper.setTo(details.toList.toArray(new String[details.toList.size()]));
      
                Map<String, Object> model = new HashMap<String, Object>();
                
                model.put("details", details);
                 
                String text = geVelocityTemplateContent(model, details.getTemplate());
                System.out.println("Template content : "+text);
 
                // use the true flag to indicate you need a multipart message
                helper.setText(text, true);

 
            }
        };
        return preparator;
    }
     
     
    public String geVelocityTemplateContent(Map<String, Object> model, String template){
        StringBuffer content = new StringBuffer();
        try{
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template+".vm", model));
            return content.toString();
        }catch(Exception e){
            System.out.println("Exception occured while processing velocity template:"+e.getMessage());
        }
          return "";
    }
    
    
  
}
