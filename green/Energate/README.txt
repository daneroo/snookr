
# To push and sunch to energate.mirawatt.com
rsync -n -avz --progress --exclude .svn --exclude nbproject --exclude README.txt --exclude .DS_Store ./ mirawatt@axial.imetrical.com:httpdocs/com/imetrical/energate/

Energate iPhone 2 -
New Gateway
  http://opa.myenergate.com/
  userid: smckenzie psswd: test-123

Reverse engineering:
main html has:
  <div id="silverlightControlHost" width="1210" height="750" >
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="1210" height="750" id="Energate">
       <param name="flashvars" value='HomeId=1&HomeZipId=K1V7P1&UserId=233&Path=http://opa.myenergate.com/svc/Energate.Core2.Reporting.Core2ReportService.svc?wsdl' />

in which we find UserId: 233 in this case

WSDL:
  http://172.23.1.4:8582/Energate.Core2.Reporting.Core2ReportService.svc?wsdl
or rather on a public network:
  curl "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc?wsdl"

First curl post command:
curl -i -X POST -H "Content-Type: text/xml; charset=utf-8" -H "soapaction: http://tempuri.org/ICCDRService/GetThermostatDetails" -d '@enersoap.xml' "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc/basic"; echo

-----------------------------------------------------

Energate iPhone 1 -
http://firstenergy-staging.getgreenbox.com/pages/homes/scottdesk/
