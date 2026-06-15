package com.edham.logistics.websocket;

import com.edham.logistics.dto.ShipmentResponse;
import com.edham.logistics.dto.NotificationMessage;
import com.edham.logistics.model.TrackingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

import java.util.HashMap;

import java.util.Map;



/**

 * WebSocket Service

 * Handles real-time messaging and notifications

 */

@Service

public class WebSocketService {



    @Autowired

    private SimpMessagingTemplate messagingTemplate;



    @Autowired

    private ObjectMapper objectMapper;



    // WebSocket destinations

    private static final String SHIPMENT_UPDATES = "/topic/shipments";

    private static final String TRACKING_UPDATES = "/topic/tracking";

    private static final String NOTIFICATIONS = "/topic/notifications";

    private static final String SYSTEM_ALERTS = "/topic/system-alerts";



    /**

     * Send shipment update to specific user

     */

    public void sendShipmentUpdate(Long userId, ShipmentResponse shipment) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "SHIPMENT_UPDATE");

            message.put("userId", userId);

            message.put("shipment", shipment);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                SHIPMENT_UPDATES + "/user/" + userId,

                message

            );

        } catch (Exception e) {

            // Log error but don't throw to avoid breaking main flow

            System.err.println("Failed to send shipment update: " + e.getMessage());

        }

    }



    /**

     * Send shipment update to organization

     */

    public void sendShipmentUpdateToOrganization(Long orgId, ShipmentResponse shipment) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "ORGANIZATION_SHIPMENT_UPDATE");

            message.put("orgId", orgId);

            message.put("shipment", shipment);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                SHIPMENT_UPDATES + "/org/" + orgId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send organization shipment update: " + e.getMessage());

        }

    }



    /**

     * Send tracking update to specific user

     */

    public void sendTrackingUpdate(Long userId, TrackingEvent trackingEvent) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "TRACKING_UPDATE");

            message.put("userId", userId);

            message.put("trackingEvent", trackingEvent);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                TRACKING_UPDATES + "/user/" + userId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send tracking update: " + e.getMessage());

        }

    }



    /**

     * Send tracking update to shipment subscribers

     */

    public void sendTrackingUpdateToShipment(Long shipmentId, TrackingEvent trackingEvent) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "SHIPMENT_TRACKING_UPDATE");

            message.put("shipmentId", shipmentId);

            message.put("trackingEvent", trackingEvent);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                TRACKING_UPDATES + "/shipment/" + shipmentId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send shipment tracking update: " + e.getMessage());

        }

    }



    /**

     * Send notification to specific user

     */

    public void sendNotification(Long userId, NotificationMessage notification) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "NOTIFICATION");

            message.put("userId", userId);

            message.put("notification", notification);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                NOTIFICATIONS + "/user/" + userId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send notification: " + e.getMessage());

        }

    }



    /**

     * Send notification to organization

     */

    public void sendNotificationToOrganization(Long orgId, NotificationMessage notification) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "ORGANIZATION_NOTIFICATION");

            message.put("orgId", orgId);

            message.put("notification", notification);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                NOTIFICATIONS + "/org/" + orgId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send organization notification: " + e.getMessage());

        }

    }



    /**

     * Send system alert

     */

    public void sendSystemAlert(String alertType, String message, Object data) {

        try {

            Map<String, Object> alert = new HashMap<>();

            alert.put("type", "SYSTEM_ALERT");

            alert.put("alertType", alertType);

            alert.put("message", message);

            alert.put("data", data);

            alert.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(SYSTEM_ALERTS, alert);

        } catch (Exception e) {

            System.err.println("Failed to send system alert: " + e.getMessage());

        }

    }



    /**

     * Send driver location update

     */

    public void sendDriverLocationUpdate(Long driverId, Double latitude, Double longitude) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "DRIVER_LOCATION_UPDATE");

            message.put("driverId", driverId);

            message.put("latitude", latitude);

            message.put("longitude", longitude);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                TRACKING_UPDATES + "/driver/" + driverId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send driver location update: " + e.getMessage());

        }

    }



    /**

     * Send payment status update

     */

    public void sendPaymentStatusUpdate(Long userId, Long invoiceId, String status) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "PAYMENT_STATUS_UPDATE");

            message.put("userId", userId);

            message.put("invoiceId", invoiceId);

            message.put("status", status);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                NOTIFICATIONS + "/user/" + userId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send payment status update: " + e.getMessage());

        }

    }



    /**

     * Send new shipment notification

     */

    public void sendNewShipmentNotification(Long userId, ShipmentResponse shipment) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("NEW_SHIPMENT");

        notification.setTitle("New Shipment Created");

        notification.setMessage("Shipment " + shipment.getTrackingNumber() + " has been created");

        notification.setData(shipment);

        notification.setTimestamp(LocalDateTime.now());



        sendNotification(userId, notification);

    }



    /**

     * Send shipment status change notification

     */

    public void sendShipmentStatusChangeNotification(Long userId, String trackingNumber, 

                                                   String oldStatus, String newStatus) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("SHIPMENT_STATUS_CHANGE");

        notification.setTitle("Shipment Status Updated");

        notification.setMessage("Shipment " + trackingNumber + " status changed from " + 

                              oldStatus + " to " + newStatus);

        notification.setData(Map.of(

            "trackingNumber", trackingNumber,

            "oldStatus", oldStatus,

            "newStatus", newStatus

        ));

        notification.setTimestamp(LocalDateTime.now());



        sendNotification(userId, notification);

    }



    /**

     * Send delivery confirmation notification

     */

    public void sendDeliveryConfirmationNotification(Long userId, String trackingNumber) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("DELIVERY_CONFIRMATION");

        notification.setTitle("Delivery Confirmed");

        notification.setMessage("Shipment " + trackingNumber + " has been delivered successfully");

        notification.setData(Map.of("trackingNumber", trackingNumber));

        notification.setTimestamp(LocalDateTime.now());



        sendNotification(userId, notification);

    }



    /**

     * Send payment reminder notification

     */

    public void sendPaymentReminderNotification(Long userId, Long invoiceId, double amount, 

                                            String dueDate) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("PAYMENT_REMINDER");

        notification.setTitle("Payment Reminder");

        notification.setMessage("Payment of $" + amount + " for invoice #" + invoiceId + 

                              " is due on " + dueDate);

        notification.setData(Map.of(

            "invoiceId", invoiceId,

            "amount", amount,

            "dueDate", dueDate

        ));

        notification.setTimestamp(LocalDateTime.now());



        sendNotification(userId, notification);

    }



    /**

     * Send driver assignment notification

     */

    public void sendDriverAssignmentNotification(Long driverId, String trackingNumber) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("DRIVER_ASSIGNMENT");

        notification.setTitle("New Assignment");

        notification.setMessage("You have been assigned to shipment " + trackingNumber);

        notification.setData(Map.of("trackingNumber", trackingNumber));

        notification.setTimestamp(LocalDateTime.now());



        sendNotification(driverId, notification);

    }



    /**

     * Send delay alert notification

     */

    public void sendDelayAlertNotification(Long userId, String trackingNumber, String reason) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("DELAY_ALERT");

        notification.setTitle("Shipment Delayed");

        notification.setMessage("Shipment " + trackingNumber + " is delayed: " + reason);

        notification.setData(Map.of(

            "trackingNumber", trackingNumber,

            "delayReason", reason

        ));

        notification.setTimestamp(LocalDateTime.now());



        sendNotification(userId, notification);

    }



    /**

     * Broadcast message to all connected users

     */

    public void broadcastMessage(String messageType, Object data) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", messageType);

            message.put("data", data);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend("/topic/broadcast", message);

        } catch (Exception e) {

            System.err.println("Failed to broadcast message: " + e.getMessage());

        }

    }



    /**

     * Send user activity update

     */

    public void sendUserActivityUpdate(Long userId, String activity, Object details) {

        try {

            Map<String, Object> message = new HashMap<>();

            message.put("type", "USER_ACTIVITY_UPDATE");

            message.put("userId", userId);

            message.put("activity", activity);

            message.put("details", details);

            message.put("timestamp", LocalDateTime.now());



            messagingTemplate.convertAndSend(

                "/topic/user-activity/" + userId,

                message

            );

        } catch (Exception e) {

            System.err.println("Failed to send user activity update: " + e.getMessage());

        }

    }



    /**

     * Send system maintenance notification

     */

    public void sendSystemMaintenanceNotification(String message, LocalDateTime startTime, 

                                              LocalDateTime endTime) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("SYSTEM_MAINTENANCE");

        notification.setTitle("System Maintenance");

        notification.setMessage(message);

        notification.setData(Map.of(

            "startTime", startTime,

            "endTime", endTime

        ));

        notification.setTimestamp(LocalDateTime.now());



        broadcastMessage("SYSTEM_MAINTENANCE", notification);

    }



    /**

     * Send emergency alert

     */

    public void sendEmergencyAlert(String alertMessage, String severity) {

        NotificationMessage notification = new NotificationMessage();

        notification.setType("EMERGENCY_ALERT");

        notification.setTitle("Emergency Alert");

        notification.setMessage(alertMessage);

        notification.setData(Map.of(

            "severity", severity,

            "alertMessage", alertMessage

        ));

        notification.setTimestamp(LocalDateTime.now());



        broadcastMessage("EMERGENCY_ALERT", notification);

    }

}

