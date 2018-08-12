package com.myBuddy.email.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RestController
@CrossOrigin(origins = "*")
// @RequestMapping(path="/email")
public class EmailController {
	@Autowired
	private JavaMailSender sender;

	@RequestMapping("/sendMail")
	public String sendMail() throws MessagingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
		List<String> emailTo = new ArrayList<>();
		emailTo.add("agnihotri.paras@gmail.com");
		emailTo.add("sinhanil19@gmail.com");
		emailTo.add("er.mr.bhushan@gmail.com");
		try {
			helper.setTo(emailTo.toArray(new String[emailTo.size()]));
			helper.setFrom("My-Buddy");
			helper.setSubject("My Buddy Email Service");
			helper.setText(
					"<p>Dear Sir or Madam:<br></p><p><br>I have recently ordered a new pair of soccer cleats (item #6542951) from your website on June 21. I received the order on June 26. Unfortunately, when I opened it, I saw that the cleats were used. The cleats had dirt all over it and there was a small tear in front of the part where the left toe would go.  My order number is AF26168156.<br></p><p><br>To resolve the problem, I would like you to credit my account for the amount charged for my cleats; I have already went out and bought a new pair of cleats at my local sporting goods store so sending another would result in me having two pairs of the same cleats.<br></p><p><br>Than you for taking the time to read this letter.  I have been a satisfied customer of your company for many years and this is the first time I have encountered a problem.  If you need to contact me, you can reach me at (555) 555-5555.<br><br><br><br>Sincerely,<br></p><p><br><i>Signature</i></p>",
					true);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		sender.send(message);
		return "Mail Sent Success!";
	}

	@PostMapping("/sendTemplateMail")
	public String sendMailfromTemplate(@RequestBody EmailDetails details) throws MessagingException {
		if (details.template == null || details.template.trim().equals(""))
			return "Template name not specified";
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
		try {
			if (details.template.equalsIgnoreCase("new-project-added")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>New project has been created "
						+ details.paramMap.get("projectName")
						+ " Please enroll to start working on this project. <br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("New Projec created - enrollment open !!");

			}if (details.template.equalsIgnoreCase("new-project-added-po")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>New project has been created by user id "
						+ details.paramMap.get("fromUser")
						+ " We will notify you if there are any new enrollments, please check Project activities page for any updates. <br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("New Projec created - "+details.paramMap.get("projectName"));

			} else if (details.template.equalsIgnoreCase("payment-done")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>" + details.paramMap.get("credit")
						+ " Credits has been transferred from user - " + details.paramMap.get("fromUser")
						+ "  to user - " + details.paramMap.get("toUser")
						+ "<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
				helper.setSubject("Payment done successfully");

			} else if (details.template.equalsIgnoreCase("payment-failed")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>Payment of " + details.paramMap.get("amount")
						+ "  "+details.paramMap.get("cry")+" has failed - "
						+ " Please retry the transaction <br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
				helper.setSubject("Payment Failed");

			} else if (details.template.equalsIgnoreCase("add-credit")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>" + details.paramMap.get("credit")
						+ " added to user with email - " + details.paramMap.get("email")
						+ ", same will reflect in your home page in the Credit section<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("Credits added to your profile");

			} else if (details.template.equalsIgnoreCase("withdraw-credit")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>Request recieved for withdrawal of "
					//	+ details.paramMap.get("amount") + "  " + details.paramMap.get("ccy")
						+ " credit : " + details.paramMap.get("credit")
						+ " for you account linked with email -  " + details.paramMap.get("email")
						+ "  . We will process this request within 5-7 business working days and the amount will be credited to your account. Please write to us in case of any doubts.<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("Withdraw credit request - Successful");

			} else if (details.template.equalsIgnoreCase("txn-success")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>" + details.paramMap.get("credit")
						+ " Credits has been transferred from user - " + details.paramMap.get("fromUser")
						+ "  to user - " + details.paramMap.get("toUser")
						+ ". A transaction charge of "+details.paramMap.get("txnCharge")+" has beein charged<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
				helper.setSubject("Transaction successfull");

			} else if (details.template.equalsIgnoreCase("txn-failed")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>Your transaction to transfer funds to "
						+ details.paramMap.get("toUser") + " For " + details.paramMap.get("nosOfcredit")
						+ " credits has failed, please retry the transaction. <br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("Transaction faliure");

			} else if (details.template.equalsIgnoreCase("add-credit-failed")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>Your transaction to add " + details.paramMap.get("credit")
						+ " credits has failed, please retry the transaction or get in touch with us using our contact us page. <br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("Credit addition faliure ");

			} else if (details.template.equalsIgnoreCase("new-user")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>New user with email - " + details.paramMap.get("email")
						+ " has been added to My Buddy platform, Welcome Aboard. <br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>",
						true);
				helper.setSubject("Welcome to My Buddy");

			} else if (details.template.equalsIgnoreCase("new-enrollment-po")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>New Enrollement - " + details.paramMap.get("email")
						+ " with skills as " + details.paramMap.get("skills")
						+ "<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
				helper.setSubject("New enrollment");

			} else if (details.template.equalsIgnoreCase("new-enrollment-buddy")) {
				if (details.paramMap == null || details.paramMap.isEmpty())
					return "Parameters not specified";
				helper.setTo(details.toList.toArray(new String[details.toList.size()]));
				helper.setText("<p>Hello,<br></p><p><br>New Enrollement - " + details.paramMap.get("email")
						+ " with skills as " + details.paramMap.get("skills")
						+ "<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
				helper.setSubject("New enrollment");

			} else {
				return "Not a recognized template";
			}
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		sender.send(message);
		return "Mail Sent Success!";
	}
	
	
	@PostMapping("/sendMail")
	public String sendMail(@RequestBody EmailDetails details) {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(details.toList.toArray(new String[details.toList.size()]));
			helper.setText("<p>Hello,<br></p><p><br>"+details.body
			+ "<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
			helper.setSubject(details.subject);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "Error while sending mail ..";
		}
		sender.send(message);
		return "Mail Sent Success!";
	}
	
	@Autowired
    JavaMailSender mailSender;
     
    @Autowired
    VelocityEngine velocityEngine;
   
    @PostMapping("/sendVelocityMail")
	public String sendVelocityMail(@RequestBody EmailDetails details) {
        
        MimeMessagePreparator preparator = getMessagePreparator(details);
         
        try {
            mailSender.send(preparator);
            System.out.println("Message has been sent.............................");
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
		return "Mail sent success!!";
	}
	private MimeMessagePreparator getMessagePreparator(final EmailDetails details){
        
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
 
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
  
                helper.setSubject("This is Subject");
                helper.setFrom("My-Buddy");
                helper.setTo(details.toList.toArray(new String[details.toList.size()]));
      
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("EmailDetails", details);
                 
                String text = geVelocityTemplateContent(model);//Use Freemarker or Velocity
                System.out.println("Template content : "+text);
 
                // use the true flag to indicate you need a multipart message
                helper.setText(text, true);

 
            }
        };
        return preparator;
    }
     
     
    public String geVelocityTemplateContent(Map<String, Object> model){
        StringBuffer content = new StringBuffer();
        try{
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "src/main/resources/MailTemplate.vm/MailTemplate.vm", model));
            return content.toString();
        }catch(Exception e){
            System.out.println("Exception occured while processing velocity template:"+e.getMessage());
        }
          return "";
    }
}
