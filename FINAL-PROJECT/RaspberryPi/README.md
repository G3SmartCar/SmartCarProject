#HOTSPOT CONNECTION

To connect the rp to hotspot follow these 4 steps. In terminal:

1- type:
sudo nano /etc/network/interfaces to edit the /etc/network/interfaces

2- Copy/paste this code:

auto lo

iface lo inet loopback
iface eth0 inet dhcp

auto wlan0
iface wlan0 inet dhcp
wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf


3- save the changes by presing CTR+X then Y and enter. Then type this:
sudo nano /etc/wpa_supplicant/wpa_supplicant.conf


4- In the new screen copy/paste this, with your hotspot name and its password:
ctrl_interface=/var/run/wpa_supplicant
ctrl_interface_group=0
update_config=1

network={
        ssid="your hotspot name"
        psk="your password"
        key_mgmt=WPA-PSK
}

#Run Command On Boot

Open local by typing:

sudo nano /etc/rc.local


Type commands you want to be run on boot. 
Note: command shoul be above exit command.

#Command

java -Djava.library.path=/usr/lib/jni -cp .:/usr/share/java/RXTXcomm.jar WifiServer
