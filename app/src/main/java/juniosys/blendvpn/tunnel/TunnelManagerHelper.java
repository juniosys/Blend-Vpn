package juniosys.blendvpn.tunnel;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import juniosys.blendvpn.BlendService;

public class TunnelManagerHelper
{
	public static void startBlend(Context context) {
        Intent startVPN = new Intent(context, BlendService.class);
		
		if (startVPN != null) {
			TunnelUtils.restartRotateAndRandom();
			
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			//noinspection NewApi
                context.startForegroundService(startVPN);
            else
                context.startService(startVPN);
        }
    }
	
	public static void stopBlend(Context context) {
		Intent stopTunnel = new Intent(BlendService.TUNNEL_SSH_STOP_SERVICE);
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(stopTunnel);
	}
}
