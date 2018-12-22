package ru.parallels.mipt.immortal;

/**
 * Created by Damir Salakhutdinov on 09.11.2017.
 */


import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.os.Handler;
import android.os.AsyncTask;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	String rec = "examplemailadr@gmail.com";
	String subject = "current location";
	String textMessage = "lala";
	Session session = null;
	Properties props;

	public MailSender() {
		settingProps();
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public void setRec(String rec) {
		this.rec = rec;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void settingProps() {
		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
	}

	public void sendMail() {
		session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("examplemailadr@gmail.com", "1q2w3e2w");
			}
		});

		RetreiveFeedTask task = new RetreiveFeedTask();
		task.execute();
	}

	class RetreiveFeedTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try{
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("examplemailadr@gmail.com"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));
				message.setSubject(subject);
				message.setContent(textMessage, "text/html; charset=utf-8");
				Transport.send(message);
			} catch(MessagingException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {}
	}
}
