package juniosys.blendvpn.model;

import android.support.v4.app.Fragment;

public abstract class ViewFragment extends Fragment
	implements OnUpdateLayout
{
	public void updateLayout()
	{
		updateLayout(null);
	}
}
