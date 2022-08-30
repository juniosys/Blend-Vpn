package juniosys.blendvpn.util;

import android.content.Context;
import juniosys.blendvpn.R;

/**
 * @author Skank3r
 */
public class BlendProtect {

	private static final String TAG = BlendProtect.class.getSimpleName();
	
	private static final String APP_BASE = "juniosys.blendvpn";
	
	// Assinatura da Google Play
	//private static final String APP_SIGNATURE = "XbhYZ4Bz/9F4cWLIDMg0wl/+jl8=\n";

	private String simpleProtect = new String(new byte[]{98,108,101,110,100,32,118,112,110});



	private static BlendProtect mInstance;

	private Context mContext;
	
	public static void init(Context context) {
		if (mInstance == null) {
			mInstance = new BlendProtect(context);

			// This method will print your certificate signature to the logcat.
			//AndroidTamperingProtectionUtils.getCertificateSignature(context);
		}
	}

	private BlendProtect(Context context) {
		mContext = context;
	}
	
	/*public void tamperProtect() {
		AndroidTamperingProtection androidTamperingProtection = new AndroidTamperingProtection.Builder(mContext, APP_SIGNATURE)
			.installOnlyFromPlayStore(false) // By default is set to false.
			.build();

		if (!androidTamperingProtection.validate()) {
			throw new RuntimeException();
		}
	}*/

	public void simpleProtect() {

		if (!APP_BASE.equals(mContext.getPackageName().toLowerCase()) ||
			!mContext.getString(R.string.app_name).toLowerCase().equals(simpleProtect)) {
			throw new RuntimeException();
		}
	}

	public static void CharlieProtect() {
		if (mInstance == null) return;
			
		mInstance.simpleProtect();
		
		// ative apenas ao enviar pra PlayStore
		//mInstance.tamperProtect();
	}
}
