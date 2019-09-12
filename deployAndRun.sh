sudo mv /media/sf_VM_Shared_Folder/Paypay.jar ~/
sudo chmod 777  Paypay.jar
hadoop fs -rm -r /tmp/james/cleandata/*
spark-submit --class app.Test ~/Paypay.jar "/tmp/james/rawdata/*" "/tmp/james/cleandata/"
