package com.github.sats17.api;

import com.github.sats17.configuration.Enums;
import com.github.sats17.model.NotificationMsResponse;
import com.github.sats17.model.PushNotificationRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1/api/notification")
public class NotificationApi {
	
    @POST
    @Path("/push/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NotificationMsResponse pushNotificationByUserId(@PathParam("userId") String id, PushNotificationRequest request) {
    	request.getNotificationType().forEach(type -> {
    		if(type == Enums.NotificationType.Mail) {
    			System.out.println("Sending mail notification");
    		}
    		if(type == Enums.NotificationType.PushNotification) {
    			System.out.println("Sending Push notification");
    		}
    		if(type == Enums.NotificationType.SMS) {
    			System.out.println("Sending SMS notification");
    		}
    	});
        return new NotificationMsResponse(200, "Notification sent successfully");
    }

}
