package com.myBuddy.email.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
	private static Log log = LogFactory.getLog(EmailController.class.getName());

	private static Map<String, String> subjectMap = new HashMap<String, String>();
	static {
		subjectMap.put("new-project-added", "New Project created - Bidding open !!");
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
		subjectMap.put("new-bid-po", "New Bid");
		subjectMap.put("new-bid-buddy", "New Bid");
	}

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	VelocityEngine velocityEngine;

	ExecutorService executorService = Executors.newSingleThreadExecutor();

	@GetMapping("/testSendMail")
	public String testSendMail() throws MessagingException {

		EmailDetails emailBody = new EmailDetails();
		emailBody.setTemplate("new-project-added");

		List rec = new ArrayList<>();
		rec.add("agnihotri.paras@live.com");
		rec.add("agnihotri.paras@gmail.com");
		rec.add("sinhanil19@gmail.com");

		emailBody.setToList(rec);

		Map<String, String> paramMap = new HashMap<>();

		paramMap.put("projectName", " A Test Project");
		emailBody.setParamMap(paramMap);

		log.info("sending email .... " + emailBody);
		sendByTemplate(emailBody, true);
		return "Mail Sent Success!11";
	}

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
			helper.setSubject(stg + "My Buddy Email Service");
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
			helper.setText("<p>Hello,<br></p><p><br>" + details.body
					+ "<br></p><br><br><br><br>Sincerely,<br></p><p><br><i>My Buddy Team</i></p>", true);
			helper.setSubject(stg + details.subject);
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
					sendByTemplate(details, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return "new mail thread created";
	}

	@PostMapping("/sendTemplateMailBCC")
	public String addToBCCQueue(@RequestBody EmailDetails details) {

		executorService.execute(new Runnable() {
			public void run() {
				try {
					sendByTemplate(details, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return "new mail thread created";
	}

	// @PostMapping("/sendTemplateMail")
	public String sendByTemplate(@RequestBody EmailDetails details, boolean bcc) {

		if (details.template == null || details.template.trim().equals(""))
			return "Template name not specified";
		if (details.paramMap == null || details.paramMap.isEmpty())
			return "Parameters not specified";

		MimeMessagePreparator preparator = getMessagePreparator(details, bcc);

		try {
			mailSender.send(preparator);
			log.info("Message has been sent.............................");
		} catch (MailException ex) {
			System.err.println(ex.getMessage());
		}
		return "Mail Sent Success!";
	}

	private MimeMessagePreparator getMessagePreparator(final EmailDetails details, boolean bcc) {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

				helper.setSubject(stg + (String) subjectMap.get(details.getTemplate()));

				helper.setFrom("My-Buddy");
				if (bcc) {
					helper.setBcc(details.toList.toArray(new String[details.toList.size()]));
				} else
					helper.setTo(details.toList.toArray(new String[details.toList.size()]));

				Map<String, Object> model = new HashMap<String, Object>();

				model.put("details", details);

				String text = geVelocityTemplateContent(model, details.getTemplate());

				// use the true flag to indicate you need a multipart message
				helper.setText(text, true);

			}
		};
		return preparator;
	}

	public String geVelocityTemplateContent(Map<String, Object> model, String template) {
		StringBuffer content = new StringBuffer();
		try {
			content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template + ".vm", model));
			return content.toString();
		} catch (Exception e) {
			log.info("Exception occured while processing velocity template:" + e.getMessage());
		}
		return "";
	}

}
