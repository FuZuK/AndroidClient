package ua.killer.milliynet.listeners;

public interface OnServerConnectListener {
	public void onStartConnecting();
	public void onFinishConnecting(String responseText);
}
