package com.coversal.plugin.wol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.RemoteException;

import com.coversal.ucl.api.BrowsableAPI;
import com.coversal.ucl.api.ControllerAPI;
import com.coversal.ucl.api.TextParameter;
import com.coversal.ucl.plugin.Controller;
import com.coversal.ucl.plugin.ProfileAnnouncer;
import com.coversal.ucl.plugin.UnconnectedProfile;

public class Wol extends UnconnectedProfile {

	static final String PROFILE_NAME = "Wake_On_Lan";
	private final String MAC = "MAC address";
	private final String IP = "Broadcast IP";

	WolController controller;
	
	public Wol(ProfileAnnouncer pa) {
		super(pa);

		controller = new WolController();
		
		defineParameter(MAC, new TextParameter("", true));
		defineParameter(IP, new TextParameter("", true));
	}

	@Override
	public BrowsableAPI getBrowser() {
		return null;
	}

	@Override
	public ControllerAPI getController() {
		return controller;
	}

	@Override
	public String getIconName() {
		return "wol";
	}

	@Override
	public String getProfileName() {
		return PROFILE_NAME;
	}

	@Override
	public String getTargetNameField() {
		return MAC;
	}

	@Override
	public void onConfigurationUpdate() {
		// TODO Auto-generated method stub
	}


	
	
	
	class WolController extends Controller {

		WolController() {
			super(Wol.this);
			defineCommand("Wake up!", "", false);
			this.defineKey(POWER, "Wake up!", false, "");
		}
		
		private String[] validateMac(String mac) throws IllegalArgumentException
		{
			//error handle semi colons
			mac = mac.replace(";", ":");
			
			//regexp pattern match a valid MAC address
			final Pattern pat = Pattern.compile("((([0-9a-fA-F]){2}[-:]){5}([0-9a-fA-F]){2})");
			final Matcher m = pat.matcher(mac);

			if(m.find()) {
				String result = m.group();
				return result.split("(\\:|\\-)");
			}else{
				throw new IllegalArgumentException("Invalid MAC address.");
			}
		}
		
		@Override
		public boolean execute(String arg0) throws RemoteException {
			//validate MAC and chop into array
			final String[] hex = validateMac(getValue(MAC));
			
			//convert to base16 bytes
			final byte[] macBytes = new byte[6];
			for(int i=0; i<6; i++) {
				macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
			
			final byte[] bytes = new byte[102];

			//fill first 6 bytes
			for(int i=0; i<6; i++) {
				bytes[i] = (byte) 0xff;
			}
			//fill remaining bytes with target MAC
			for(int i=6; i<bytes.length; i+=macBytes.length) {
				System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
			}

			try {
				//create socket to IP
				final InetAddress address = InetAddress.getByName(getValue(IP));
				final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
				final DatagramSocket socket = new DatagramSocket();
				
				socket.send(packet);
				socket.close();
				
				debug("WOL packet sent to: "+hex[0]+":"+hex[1]+":"+hex[2]+":"+hex[3]+":"+hex[4]+":"+hex[5]
						+" via broadcast address "+getValue(IP));

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}

		@Override
		public List<String> getContextMenuItems(int arg0)
				throws RemoteException {
			return null;
		}

		@Override
		public List<String> getDeviceList() throws RemoteException {
			return null;
		}

		@Override
		public String getLayoutName() throws RemoteException {
			return "sshmote";
		}

		@Override
		public int getMediaDuration() throws RemoteException {
			return 0;
		}

		@Override
		public int getMediaPosition() throws RemoteException {
			return 0;
		}

		@Override
		public String getPlayingMedia() throws RemoteException {
			return null;
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return false;
		}

		@Override
		public void onItemSelected(String arg0, String arg1)
				throws RemoteException {
		}
		
	}

}
