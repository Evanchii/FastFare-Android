package com.codexmeraki.fastfare.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.codexmeraki.fastfare.R;

public class PassengerPayment {

    private String _passenger, _price, _origin, _destination, _channelID = "DRIVER_PAYMENT";
    private NotificationManagerCompat mNotificationManager;
    private Context _context;

    public void generateNotification(String passenger, String price, String origin, String destination, Context context) {
        _passenger = passenger;
        _price = price;
        _destination = destination;
        _origin = origin;
        _context = context;

        createNotificationChannel();

        Notification builder = new NotificationCompat.Builder(_context, _channelID)
                .setSmallIcon(R.drawable.logo_app_color)
                .setContentTitle("Received Payment")
                .setContentText(_passenger + " has paid Php " + _price)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(_passenger + " has paid Php " + _price + "\nOrigin: " + _origin + "\nDestination:"+ _destination))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        mNotificationManager = NotificationManagerCompat.from(_context);

        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel paymentchannel = new NotificationChannel(
                    _channelID,
                    "Payment Received",
                    NotificationManager.IMPORTANCE_HIGH
            );
            paymentchannel.setDescription("Channel for Notifying driver if payment has been received");

            NotificationManager manager = _context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(paymentchannel);
        }
    }

}
