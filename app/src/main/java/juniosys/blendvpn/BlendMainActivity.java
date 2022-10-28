package juniosys.blendvpn;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import juniosys.blendvpn.BlendMainActivity;
import juniosys.blendvpn.activities.BaseActivity;
import juniosys.blendvpn.activities.ConfigGeralActivity;
import juniosys.blendvpn.config.Settings;
import juniosys.blendvpn.fragments.ClearConfigDialogFragment;
import juniosys.blendvpn.logger.BlendStatus;
import juniosys.blendvpn.logger.ConnectionStatus;
import juniosys.blendvpn.tunnel.TunnelManagerHelper;
import juniosys.blendvpn.util.BlendProtect;
import juniosys.blendvpn.util.Utils;

/**
 * Activity Principal
 * @author SlipkHunter
 */

public class BlendMainActivity extends BaseActivity
		implements DrawerLayout.DrawerListener,
		View.OnClickListener, BlendStatus.StateListener
{
	private static final String TAG = BlendMainActivity.class.getSimpleName();
	private static final String UPDATE_VIEWS = "MainUpdate";
	public static final String OPEN_LOGS = "juniosys.blendvpn:openLogs";

	private DrawerLog mDrawer;
	private DrawerPanelMain mDrawerPanel;
	private Settings mConfig;
	private Toolbar toolbar_main;
	private Handler mHandler;
	private LinearLayout mainLayout;
	private LinearLayout loginLayout;
	private Button starterButton;
	private ImageButton inputPwShowPass;
	private TextInputEditText inputPwUser;
	private TextInputEditText inputPwPass;
	private Spinner servidores;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHandler = new Handler();
		mConfig = new Settings(this);
		mDrawer = new DrawerLog(this);
		mDrawerPanel = new DrawerPanelMain(this);

		SharedPreferences prefs = getSharedPreferences(BlendApp.PREFS_GERAL, Context.MODE_PRIVATE);

		boolean showFirstTime = prefs.getBoolean("connect_first_time", true);

		// se primeira vez
		if (showFirstTime)
		{
			SharedPreferences.Editor pEdit = prefs.edit();
			pEdit.putBoolean("connect_first_time", false);
			pEdit.apply();

			Settings.setDefaultConfig(this);

			showBoasVindas();
		}


		// set layout
		doLayout();

		// verifica se existe algum problema
		BlendProtect.CharlieProtect();

		// recebe local dados
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_VIEWS);
		filter.addAction(OPEN_LOGS);

		LocalBroadcastManager.getInstance(this)
				.registerReceiver(mActivityReceiver, filter);

		doUpdateLayout();
	}


	/**
	 * Layout
	 */

	private void doLayout() {
		setContentView(R.layout.activity_main_drawer);

		toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
		mDrawerPanel.setDrawer(toolbar_main);
		setSupportActionBar(toolbar_main);

		mDrawer.setDrawer(this);


		mainLayout = (LinearLayout) findViewById(R.id.activity_mainLinearLayout);
		loginLayout = (LinearLayout) findViewById(R.id.activity_mainInputPasswordLayout);
		starterButton = (Button) findViewById(R.id.activity_starterButtonMain);

		inputPwUser = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordUserEdit);
		inputPwPass = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordPassEdit);

		inputPwShowPass = (ImageButton) findViewById(R.id.activity_mainInputShowPassImageButton);

		((TextView) findViewById(R.id.activity_mainAutorText))
				.setOnClickListener(this);

		starterButton.setOnClickListener(this);
		
		
		// fix bugs
		if (mConfig.getPrefsPrivate().getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
			if (mConfig.getPrefsPrivate().getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				inputPwUser.setText(mConfig.getPrivString(Settings.USUARIO_KEY));
				inputPwPass.setText(mConfig.getPrivString(Settings.SENHA_KEY));
			}
		}

		inputPwShowPass.setOnClickListener(this);

        //Onde fica salvo as coisas no aparelho
		final SharedPreferences sPrefs = mConfig.getPrefsPrivate();

        //Declarando o xml do spinner
		servidores = (Spinner) findViewById(R.id.serverSpinner);

		// Aqui sao os nomes que vcs quiserem pro servidor, pode ser oq vc quiser(nao importa)
		List<String> ListaServidores = new ArrayList<String>();
		ListaServidores.add("VIVO EASY 1");
		ListaServidores.add("VIVO EASY 2");
        ListaServidores.add("VIVO EASY 3");
		ListaServidores.add("VIVO EASY 4");
        ListaServidores.add("TIM SALDO VALIDO 1");
		ListaServidores.add("TIM SALDO VALIDO 2");
		ListaServidores.add("TIM SALDO VALIDO 3");
		ListaServidores.add("TIM SALDO VALIDO 4");
		ListaServidores.add("TIM SALDO VALIDO 1");
		ListaServidores.add("TIM SALDO EXPIRADO 1");
		ListaServidores.add("OI");
        
		// Criando adaptador para receber os servidores
		ArrayAdapter<String> AdptadorServidores = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListaServidores);

		// Definindo layout para o Adaptador
		AdptadorServidores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Preenche o Spinner com a lista de servidores
		servidores.setAdapter(AdptadorServidores);


		//Carrega a posição salva do servidor selecionado
		servidores.setSelection(sPrefs.getInt("Servidor", 0));

		//Função ao clicar em 1 dos servidores da lista

		servidores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> p1, View p2, int position, long p4)
			{

				try
				{
					//Salva a posição do servidor para quando entrar no app ele continuar selecionado
					sPrefs.edit().putInt("Servidor", position).apply();

					//Usar payload costumizada
					sPrefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();

					//Define as configurações do servidor de acordo com o servidor selecionado
                    if(position == 0) {
                    	//Informações SSH
						sPrefs.edit().putString(Settings.SERVIDOR_KEY, "104.18.6.80").apply();
						sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "80").apply();

						//Payload
						sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET / HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();

						//Servidor 01 Modo proxy (exemplo)
						sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT).apply();

						//Informações Proxy
						//sPrefs.edit().putString(Settings.PROXY_IP_KEY, "PROXY IP").apply();
						//sPrefs.edit().putString(Settings.PROXY_PORTA_KEY, "PROXY PORTA").apply();
                        ////==========================================================================================////
					}else if(position == 1){
						//Informações SSH   SERVIDOR 2
						sPrefs.edit().putString(Settings.SERVIDOR_KEY, "104.18.7.80").apply();
						sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "80").apply();

						//Payload
						sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET / HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        //sPrefs.edit().putString(Settings.CUSTOM_SNI, "snow.vivo.com.br").apply();

						//Servidor 02 Modo direct (exemplo)
						sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT).apply();
                        ////==========================================================================================////
                    }else if(position == 2){
                        //Informações SSH   SERVIDOR 3
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "104.18.43.147").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "80").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET / HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        //sPrefs.edit().putString(Settings.CUSTOM_SNI, "SNI").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT).apply();
						////==========================================================================================////
                    }else if(position == 3){
                        //Informações SSH   SERVIDOR 4
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "172.64.144.109").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "80").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET / HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        //sPrefs.edit().putString(Settings.CUSTOM_SNI, "SNI").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT).apply();
                        ////==========================================================================================////
                    }else if(position == 4){
                        //Informações SSH   SERVIDOR 5
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "support.deezer.com").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "443").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET wss://[host]/ HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        sPrefs.edit().putString(Settings.CUSTOM_SNI, "support.deezer.com").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL_PAY).apply();
                        ////==========================================================================================////
                    }else if(position == 5){
                        //Informações SSH   SERVIDOR 6
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "mobile.c6bank.app").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "443").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET wss://[host]/ HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        sPrefs.edit().putString(Settings.CUSTOM_SNI, "mobile.c6bank.app").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL_PAY).apply();
                        ////==========================================================================================////
					}else if(position == 6){
                        //Informações SSH   SERVIDOR 7
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "api.onesignal.com").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "443").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET wss://[host]/ HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        sPrefs.edit().putString(Settings.CUSTOM_SNI, "api.onesignal.com").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL_PAY).apply();
                        ////==========================================================================================////
					}else if(position == 7){
                        //Informações SSH   SERVIDOR 8
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "workplaceservices.surveymonkey.com").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "443").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET wss://[host]/ HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        sPrefs.edit().putString(Settings.CUSTOM_SNI, "workplaceservices.surveymonkey.com").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL_PAY).apply();
                        ////==========================================================================================////
					}else if(position == 8){
                        //Informações SSH   SERVIDOR 9
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "duvidas.ampli.com.br").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "443").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET wss://[host]/ HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        sPrefs.edit().putString(Settings.CUSTOM_SNI, "duvidas.ampli.com.br").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL_PAY).apply();
                        ////==========================================================================================////
					}else if(position == 9){
                        //Informações SSH   SERVIDOR 10
                        sPrefs.edit().putString(Settings.SERVIDOR_KEY, "www.hbogo.com.br").apply();
                        sPrefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, "443").apply();

                        //Payload
                        sPrefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, "GET wss://[host]/ HTTP/1.1[crlf]Host: juniosys.tk[crlf]Upgrade: Websocket[crlf]Connection: Keep-Alive[crlf][crlf]").apply();
                        sPrefs.edit().putString(Settings.CUSTOM_SNI, "www.hbogo.com.br").apply();

                        //Servidor 02 Modo direct (exemplo)
                        sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL_PAY).apply();
                        ////==========================================================================================////
                        
				    }
                    //Atualiza informações
                    doUpdateLayout();
				}
				catch (Exception e)
				{}
			}

			@Override
			public void onNothingSelected(AdapterView<?> p1)
			{

			}
		});

		//Sistema de usuario e senha na tela inicial
		final SharedPreferences prefsTxt = mConfig.getPrefsPrivate();
		inputPwUser.setText(prefsTxt.getString(Settings.USUARIO_KEY, ""));
		inputPwPass.setText(prefsTxt.getString(Settings.SENHA_KEY, ""));
		inputPwUser.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				if(!s.toString().isEmpty()) {
					prefsTxt.edit().putString(Settings.USUARIO_KEY, s.toString()).apply();
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		inputPwPass.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				if(!s.toString().isEmpty()) {
					prefsTxt.edit().putString(Settings.SENHA_KEY, s.toString()).apply();
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
	}

	private void doUpdateLayout() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		boolean isRunning = BlendStatus.isTunnelActive();

		setStarterButton(starterButton, this);

		int loginVisibility = View.VISIBLE;

		if (prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {

			if (prefs.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				loginVisibility = View.VISIBLE;

				inputPwUser.setText(mConfig.getPrivString(Settings.USUARIO_KEY));
				inputPwPass.setText(mConfig.getPrivString(Settings.SENHA_KEY));

				inputPwUser.setEnabled(!isRunning);
				inputPwPass.setEnabled(!isRunning);
				inputPwShowPass.setEnabled(!isRunning);

				//inputPwPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}

		loginLayout.setVisibility(loginVisibility);
	}


	private synchronized void doSaveData() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		SharedPreferences.Editor edit = prefs.edit();

		if (mainLayout != null && !isFinishing())
			mainLayout.requestFocus();

		if (!prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
		}
		else {
			if (prefs.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				edit.putString(Settings.USUARIO_KEY, inputPwUser.getEditableText().toString());
				edit.putString(Settings.SENHA_KEY, inputPwPass.getEditableText().toString());
			}
		}

		edit.apply();
	}


	/**
	 * Tunnel SSH
	 */

	public void startOrStopTunnel(Activity activity) {
		if (BlendStatus.isTunnelActive()) {
			TunnelManagerHelper.stopBlend(activity);
		}
		else {
			// oculta teclado se vísivel, tá com bug, tela verde
			//Utils.hideKeyboard(activity);

			Settings config = new Settings(activity);

			if (config.getPrefsPrivate()
					.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				if (inputPwUser.getText().toString().isEmpty() ||
						inputPwPass.getText().toString().isEmpty()) {
					Toast.makeText(this, R.string.error_userpass_empty, Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}

			Intent intent = new Intent(activity, LaunchVpn.class);
			intent.setAction(Intent.ACTION_MAIN);

			if (config.getHideLog()) {
				intent.putExtra(LaunchVpn.EXTRA_HIDELOG, true);
			}

			activity.startActivity(intent);
		}
	}

	public void setStarterButton(Button starterButton, Activity activity) {
		String state = BlendStatus.getLastState();
		boolean isRunning = BlendStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;

			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();

			if (BlendStatus.SSH_INICIANDO.equals(state)) {
				resId = R.string.stop;
				starterButton.setEnabled(true);
			}
			else if (BlendStatus.SSH_PARANDO.equals(state)) {
				resId = R.string.state_stopping;
				starterButton.setEnabled(true);
			}
			else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
			}

			starterButton.setText(resId);
		}
	}



	@Override
	public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onPostCreate(savedInstanceState, persistentState);
		if (mDrawerPanel.getToogle() != null)
			mDrawerPanel.getToogle().syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mDrawerPanel.getToogle() != null)
			mDrawerPanel.getToogle().onConfigurationChanged(newConfig);
	}

	private boolean isMostrarSenha = false;

	@Override
	public void onClick(View p1)
	{
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		switch (p1.getId()) {
			case R.id.activity_starterButtonMain:
				doSaveData();
				startOrStopTunnel(this);
				break;

			case R.id.activity_mainAutorText:
				String url = "http://t.me/SlipkProjects";
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, getText(R.string.open_with)));
				break;

			case R.id.activity_mainInputShowPassImageButton:
				isMostrarSenha = !isMostrarSenha;
				if (isMostrarSenha) {
					inputPwPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					inputPwShowPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_visibility_black_24dp));
				}
				else {
					inputPwPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					inputPwShowPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_visibility_off_black_24dp));
				}
				break;
		}
	}

	protected void showBoasVindas() {
		new AlertDialog.Builder(this)
				. setTitle(R.string.attention)
				. setMessage(R.string.first_start_msg)
				. setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int p) {
						// ok
					}
				})
				. setCancelable(false)
				. show();
	}

	@Override
	public void updateState(final String state, String msg, int localizedResId, final ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				doUpdateLayout();
			}
		});

		switch (state) {
			case BlendStatus.SSH_CONECTADO:
				// carrega ads banner
				break;
		}
	}


	/**
	 * Recebe locais Broadcast
	 */

	private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == null)
				return;

			if (action.equals(UPDATE_VIEWS) && !isFinishing()) {
				doUpdateLayout();
			}
			else if (action.equals(OPEN_LOGS)) {
				if (mDrawer != null && !isFinishing()) {
					DrawerLayout drawerLayout = mDrawer.getDrawerLayout();

					if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
						drawerLayout.openDrawer(GravityCompat.END);
					}
				}
			}
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerPanel.getToogle() != null && mDrawerPanel.getToogle().onOptionsItemSelected(item)) {
			return true;
		}

		// Menu Itens
		switch (item.getItemId()) {

			case R.id.miLimparConfig:
				if (!BlendStatus.isTunnelActive()) {
					DialogFragment dialog = new ClearConfigDialogFragment();
					dialog.show(getSupportFragmentManager(), "alertClearConf");
				} else {
					Toast.makeText(this, R.string.error_tunnel_service_execution, Toast.LENGTH_SHORT)
							.show();
				}
				break;

			case R.id.miSettings:
				Intent intentSettings = new Intent(this, ConfigGeralActivity.class);
				//intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentSettings);
				break;


			// logs opções
			case R.id.miLimparLogs:
				mDrawer.clearLogs();
				break;

			case R.id.miExit:
				if (Build.VERSION.SDK_INT >= 16) {
					finishAffinity();
				}

				System.exit(0);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout layout = mDrawer.getDrawerLayout();

		if (mDrawerPanel.getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
			mDrawerPanel.getDrawerLayout().closeDrawers();
		}
		else if (layout.isDrawerOpen(GravityCompat.END)) {
			// fecha drawer
			layout.closeDrawers();
		}
		else {
			// mostra opção para sair
			showExitDialog();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		mDrawer.onResume();

		//doSaveData();
		//doUpdateLayout();

		BlendStatus.addStateListener(this);

	}

	@Override
	protected void onPause()
	{
		super.onPause();

		doSaveData();

		BlendStatus.removeStateListener(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		mDrawer.onDestroy();

		LocalBroadcastManager.getInstance(this)
				.unregisterReceiver(mActivityReceiver);
	}


	/**
	 * DrawerLayout Listener
	 */

	@Override
	public void onDrawerOpened(View view) {
		if (view.getId() == R.id.activity_mainLogsDrawerLinear) {
			toolbar_main.getMenu().clear();
			getMenuInflater().inflate(R.menu.logs_menu, toolbar_main.getMenu());
		}
	}

	@Override
	public void onDrawerClosed(View view) {
		if (view.getId() == R.id.activity_mainLogsDrawerLinear) {
			toolbar_main.getMenu().clear();
			getMenuInflater().inflate(R.menu.main_menu, toolbar_main.getMenu());
		}
	}

	@Override
	public void onDrawerStateChanged(int stateId) {}
	@Override
	public void onDrawerSlide(View view, float p2) {}


	/**
	 * Utils
	 */

	public static void updateMainViews(Context context) {
		Intent updateView = new Intent(UPDATE_VIEWS);
		LocalBroadcastManager.getInstance(context)
				.sendBroadcast(updateView);
	}

	public void showExitDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this).
				create();
		dialog.setTitle(getString(R.string.attention));
		dialog.setMessage(getString(R.string.alert_exit));

		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.
						string.exit),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Utils.exitAll(BlendMainActivity.this);
					}
				}
		);

		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.
						string.minimize),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// minimiza app
						Intent startMain = new Intent(Intent.ACTION_MAIN);
						startMain.addCategory(Intent.CATEGORY_HOME);
						startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(startMain);
					}
				}
		);

		dialog.show();
	}
}

