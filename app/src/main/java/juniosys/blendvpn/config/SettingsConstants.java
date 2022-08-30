package juniosys.blendvpn.config;

public interface SettingsConstants {

	// Geral
	public static final String
    AUTO_CLEAR_LOGS_KEY = "autoClearLogs",
    HIDE_LOG_KEY = "hideLog",
    MODO_DEBUG_KEY = "modeDebug",
	COMPRESSION_SSH_KEY = "compressionSSH",
    MODO_NOTURNO_KEY = "modeNight",
    IDIOMA_KEY = "idioma",
    TETHERING_SUBNET = "tetherSubnet",
    DISABLE_DELAY_KEY = "disableDelaySSH",
    MAXIMO_THREADS_KEY = "numberMaxThreadSocks",
    FILTER_APPS = "filterApps",
    FILTER_BYPASS_MODE = "filterBypassMode",
    FILTER_APPS_LIST = "filterAppsList",
    PROXY_IP_KEY = "proxyRemoto",
    PROXY_PORTA_KEY = "proxyRemotoPorta",
	PAYLOAD_DEFAULT = "CONNECT [host_port] [protocol][crlf][crlf]",
    CUSTOM_PAYLOAD_KEY = "proxyPayload",
    CUSTOM_SNI = "sslSNI",
    PROXY_USAR_DEFAULT_PAYLOAD = "usarDefaultPayload",
    PROXY_USAR_AUTENTICACAO_KEY = "usarProxyAutenticacao",
    CONFIG_PROTEGER_KEY = "protegerConfig",
    CONFIG_INPUT_PASSWORD_KEY = "inputPassword",

	// Vpn
    UDPFORWARD_KEY = "udpForward",
	UDPRESOLVER_KEY = "udpResolver",
	DNSFORWARD_KEY = "dnsForward",
	DNSRESOLVER_KEY = "dnsResolver",

	// SSH
	SERVIDOR_KEY = "sshServer",
	SERVIDOR_PORTA_KEY = "sshPort",
	USUARIO_KEY = "sshUser",
	SENHA_KEY = "sshPass",
	KEYPATH_KEY = "keyPath",
	PORTA_LOCAL_KEY = "sshPortaLocal",
	PINGER_KEY = "pingerSSH",

	// Tunnel Type
    TUNNELTYPE_KEY = "tunnelType",
    TUNNEL_TYPE_SSH_DIRECT = "sshDirect",
    TUNNEL_TYPE_SSH_PROXY = "sshProxy",
    TUNNEL_TYPE_SSH_SSL = "sshSsl",
    TUNNEL_TYPE_SSH_SSL_PAY = "sshSslPay";
	;

	public static final int
    bTUNNEL_TYPE_SSH_DIRECT = 1,
    bTUNNEL_TYPE_SSH_PROXY = 2,
    bTUNNEL_TYPE_SSH_SSL = 3,
    bTUNNEL_TYPE_SSH_SSL_PAY = 4;
}
