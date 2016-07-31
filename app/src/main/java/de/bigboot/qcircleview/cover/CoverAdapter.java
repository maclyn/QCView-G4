package de.bigboot.qcircleview.cover;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.bigboot.qcircleview.NotificationService;
import de.bigboot.qcircleview.Preferences;

import static de.bigboot.qcircleview.NotificationService.Notification;

/**
 * Created by Marco Kirchner.
 */
public class CoverAdapter extends FragmentStatePagerAdapter {
    private final Fragment EMPTY_FRAGMENT = new Fragment();
    protected Activity context;
    private Preferences prefs;
    private ArrayList<NotificationService.Notification> notifications = new ArrayList<>();
    private SparseArray<Fragment> fragments = new SparseArray<>();
    private float slideValue = 0.5f;
    private List<MediaController> mediaControllers = new ArrayList<>();

    public CoverAdapter(Context context) {
        super(((Activity)context).getFragmentManager());
        prefs = new Preferences(context);
        this.context = (Activity) context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == getDefaultFragmentPosition()) {
            return EMPTY_FRAGMENT;
        } else if(position < getDefaultFragmentPosition()) {
            CoverMusicFragment fragment = CoverMusicFragment_.builder().build();
            fragment.setMediaController(mediaControllers.get(position));
            fragments.put(position, fragment);
            return fragment;
        } else {
            int index = position - (getDefaultFragmentPosition() + 1);
            NotificationService.Notification notification = notifications.get(index);
            CoverNotificationFragment fragment = CoverNotificationFragment_.builder()
                    .notification(notification).build();
            fragments.put(position, fragment);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return getDefaultFragmentPosition() + notifications.size() + 1;
    }

    public int getDefaultFragmentPosition() {
        return mediaControllers.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if(object instanceof Fragment) {
            for(int i = 0; i < fragments.size(); i++) {
                if(fragments.valueAt(i) == object) {
                    fragments.removeAt(i);
                    return;
                }
            }
        }
    }

    public void invalidate() {
        notifications.clear();
        context.sendBroadcast(new Intent(NotificationService.ACTION_COMMAND).putExtra(NotificationService.EXTRA_COMMAND, NotificationService.COMMAND_LIST));
    }

    private boolean validateNotification(NotificationService.Notification notification){
        //(1) Ensure it's not a media player
        String packageName = notification.getPackageName();
        for(MediaController mc : this.mediaControllers){
            if(mc.getPackageName().equals(packageName)) return false;
        }

        //(2) Ensure it's no a min-priority notification
        if(notification.getPriority() == android.app.Notification.PRIORITY_MIN)
            return false;

        return true;
    }

    public void onNotificationAdded(NotificationService.Notification notification) {
        if (validateNotification(notification)) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getKey().equals(notification.getKey())) {
                    notifications.set(i, notification);
                    notifyDataSetChanged();
                    return;
                }
            }
            notifications.add(notification);
        }
        notifyDataSetChanged();
    }

    public void onNotificationRemoved(NotificationService.Notification notification, int index) {
        for(int i = 0; i < notifications.size(); i++) {
            if(notifications.get(i).getKey().equals(notification.getKey())) {
                if (i != index) {
                    invalidate();
                    return;
                }
                notifications.remove(i);
                notifyDataSetChanged();
                return;
            }
        }
        notifyDataSetChanged();
    }

    public void onNotificationListReceived(ArrayList<NotificationService.Notification> notifications) {
        this.notifications.clear();
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

    public NotificationService.Notification getNotification(int index) {
        index -= (getDefaultFragmentPosition()+1);
        if (index < 0 || notifications.size() <= index) {
            return null;
        }
        return notifications.get(index);
    }

    public void onActiveSessionsChanged(List<MediaController> mediaControllers) {
        this.mediaControllers.clear();
        for (MediaController mediaController : mediaControllers) {
            if (mediaController.getPlaybackState() == null) {
                // We assume player is offline and don't add it to the list
                // This is needed for example for Eleven player
            } else {
                this.mediaControllers.add(mediaController);
            }
        }
        notifyDataSetChanged();
    }
}
