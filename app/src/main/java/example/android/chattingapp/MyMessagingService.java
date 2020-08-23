package example.android.chattingapp;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String notification_Title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();
        String click_action  = remoteMessage.getNotification().getClickAction();

        String from_user_id = remoteMessage.getData().get("from_user_id");



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNotifications").
                setContentTitle(notification_Title).
                setSmallIcon(R.drawable.ic_notifications_black_24dp).
                setAutoCancel(true).
                setContentText(notification_message);


        Intent result_intent = new Intent(click_action);
        result_intent.putExtra("user_id",from_user_id);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, result_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(99, builder.build());

    }


}
