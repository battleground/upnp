package demo.abooc.com.upnp;

import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;

import java.util.Map;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/9.
 */
public class PlayerSubscriptrion {

    public interface OnPlayerEvent {
        void noMedia();

        void onPrepare();

        void onPlaying();

        void onPaused();

        void onStopeed();

        void onSeeking();

        void onVolumeUp();

        void onVolumeDown();

    }

    public class SimpleOnPlayerEvent implements OnPlayerEvent {

        @Override
        public void noMedia() {

        }

        @Override
        public void onPrepare() {

        }

        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopeed() {

        }

        @Override
        public void onSeeking() {

        }

        @Override
        public void onVolumeUp() {

        }

        @Override
        public void onVolumeDown() {

        }
    }

    private OnPlayerEvent mOnPlayerEvent = new SimpleOnPlayerEvent();

    public void addCallback(OnPlayerEvent onPlayerEvent) {
        mOnPlayerEvent = onPlayerEvent;
    }

    public void listen(ControlPoint controlPoint, Service service) {
        SubscriptionCallback PlayerSubscription = new SubscriptionCallback(service) {

            public void established(GENASubscription sub) {
                Debug.anchor("Established: " + sub.getSubscriptionId());
            }

            @Override
            protected void failed(GENASubscription subscription, UpnpResponse response, Exception exception, String defaultMsg) {
                Debug.error(createDefaultFailureMessage(response, exception));
                Toast.show("远端没有响应");
            }

            @Override
            public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
                // Reason should be null, or it didn't end regularly
                Debug.anchor();
                Toast.show("订阅结束");
            }

            @Override
            public void eventReceived(GENASubscription sub) {
                System.out.println("Event: " + sub.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = sub.getCurrentValues();
                Debug.anchor(Tools.toString2(values));

                StateVariableValue variableValue = values.get("LastChange");

                try {
                    LastChange lastChange = new LastChange(new AVTransportLastChangeParser(),
                            variableValue.toString());

                    final AVTransportVariable.TransportState state = lastChange
                            .getEventedValue(0, AVTransportVariable.TransportState.class);

                    if (state != null) {
                        Debug.anchor(state);
                        switch (state.getValue()) {
                            case CUSTOM:
                                break;
                            case NO_MEDIA_PRESENT:
                                // 没有媒体在播放
                                mOnPlayerEvent.noMedia();
                                break;
                            case PLAYING:
                                mOnPlayerEvent.onPlaying();
                                break;
                            case TRANSITIONING:
//                                if (mRenderer.getState().getState() == PlayerInfo.State.NONE
//                                        || mRenderer.getState().getState() == PlayerInfo.State.STOPPED) {
//                                    mRendererPlayer.onPrepare();
//                                } else {
//                                    mRenderer.getState().update(PlayerInfo.State.TRANSITIONING);
//                                }
                                break;
                            case PAUSED_PLAYBACK:
                                mOnPlayerEvent.onPaused();
                                break;
                            case STOPPED:
                                mOnPlayerEvent.onStopeed();
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                Debug.anchor("Missed events: " + numberOfMissedEvents);
            }
        };

        controlPoint.execute(PlayerSubscription);
    }
}
