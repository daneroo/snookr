From this directory:
main WSDL:
curl "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc?wsdl"

how about a post : not working yet!
Still Need: cookie, and hiddens
Cookie	ASP.NET_SessionId=3df3hjwlnkfcymd1yq4hoenv

__EVENTARGUMENT
__EVENTTARGET
__EVENTVALIDATION /wEWBwLWyYq/BgL9kpmqCgKQ9M+wAwKG87kGAp37urgHAoLch8sJAqC3sMIII1m4w1nbXxTuMksBUCoe+5cojBOl0Xi+OrAjPA4PMPQ=
__VIEWSTATE	/wEPDwUKMTc5NDYzNDQ2NQ9kFgICAw9kFgICCQ9kFgJmD2QWBAIBDxYCHgNzcmMFCkltYWdlLmFzcHhkAg4PDxYCHgRUZXh0BRdQaG9uZSBObyAtIDg2Ni05OTktNTAwNmRkZKNlHhBEEe1xXTSlshkyvwi0SOwZGk56dx6wiTqvm5Cz
btnlogin Login
txtlogid smckenzie
txtpwd	XXXXXX


curl -i -X POST -F "btnlogin=Login" -F "txtlogid=smckenzie" -F "txtpwd=test-123" "http://opa.myenergate.com/ccdr/webpages/Main_Login.aspx"

From the main (authenticated redirection page (needs a cookie)) we get the url:
http://opa.myenergate.com/ccdr/webpages/EnergateFlex.aspx?H=1&Z=K1V7P1&P=
and the body contains: (also in the URL)
    flashvars.HomeId ="1";
    flashvars.HomeZipId ="K1V7P1";

GetThermostats:
Magic params: <tns:nHomeID>1</tns:nHomeID>
curl -i -X POST -H "Content-Type: text/xml; charset=utf-8" -H "soapaction: http://tempuri.org/ICCDRService/GetThermostats" -d '@GetThermostatsRequest.xml' "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc/basic"; echo

GetThermostatDetails:
Magic params: <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
curl -i -X POST -H "Content-Type: text/xml; charset=utf-8" -H "soapaction: http://tempuri.org/ICCDRService/GetThermostatDetails" -d '@GetThermostatDetailsRequest.xml' "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc/basic"; echo

Sniffing:

tcpdump -Al -vvv -s 1516 -i br0|grep soapaction

root@openwrt-rd:~# tcpdump -Al -vvv -s 1516 -i br0|grep soapaction
tcpdump: listening on br0, link-type EN10MB (Ethernet), capture size 1516 bytes
soapaction: "http://tempuri.org/ICCDRService/SLDelEditSchedules"
soapaction: "http://tempuri.org/ICCDRService/SLGetEditScheduleDetails"
soapaction: "http://tempuri.org/ICCDRService/GetThermostats"
soapaction: "http://tempuri.org/ICCDRService/GetUserScaleNTime"
soapaction: "http://tempuri.org/ICCDRService/SLGetAllSetpoint"
soapaction: "http://tempuri.org/ICCDRService/GetThermostatDetails"
soapaction: "http://tempuri.org/ICCDRService/SLGetUserConfig"
soapaction: "http://tempuri.org/ICCDRService/GetConsumerThermDetails"
soapaction: "http://tempuri.org/ICCDRService/GetDRLCMode"
soapaction: "http://tempuri.org/ICCDRService/GetWeatherFeed"
soapaction: "http://tempuri.org/ICCDRService/GetEventNMessageDetails"
soapaction: "http://tempuri.org/ICCDRService/GetFixedSetPoint"
soapaction: "http://tempuri.org/ICCDRService/GetFixedSetPoint"



tcpdump -Al -vvv -s 1516 -i br0|grep -A 4 "<SOAP-ENV:Body"

root@openwrt-rd:~# tcpdump -Al -vvv -s 1516 -i br0|grep -A 4 "<SOAP-ENV:Body"
tcpdump: listening on br0, link-type EN10MB (Ethernet), capture size 1516 bytes
  <SOAP-ENV:Body>
    <tns:SLDelEditSchedules xmlns:tns="http://tempuri.org/">
      <tns:HomeID>1</tns:HomeID>
    </tns:SLDelEditSchedules>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:SLGetEditScheduleDetails xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>1</tns:strMacAddr>
    </tns:SLGetEditScheduleDetails>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetThermostats xmlns:tns="http://tempuri.org/">
      <tns:nHomeID>1</tns:nHomeID>
    </tns:GetThermostats>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetUserScaleNTime xmlns:tns="http://tempuri.org/">
      <tns:nUserID>233</tns:nUserID>
    </tns:GetUserScaleNTime>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:SLGetAllSetpoint xmlns:tns="http://tempuri.org/">
      <tns:strMAcAddr>001BC500B00015DB</tns:strMAcAddr>
    </tns:SLGetAllSetpoint>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetThermostatDetails xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
    </tns:GetThermostatDetails>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:SLGetUserConfig xmlns:tns="http://tempuri.org/">
      <tns:strMAcAddr>001BC500B00015DB</tns:strMAcAddr>
    </tns:SLGetUserConfig>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetConsumerThermDetails xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
    </tns:GetConsumerThermDetails>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetDRLCMode xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
    </tns:GetDRLCMode>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetWeatherFeed xmlns:tns="http://tempuri.org/">
      <tns:strZIP>K1V7P1</tns:strZIP>
    </tns:GetWeatherFeed>
  </SOAP-ENV:Body>
--
  <SOAP-ENV:Body>
    <tns:GetEventNMessageDetails xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
    </tns:GetEventNMessageDetails>
  </SOAP-ENV:Body>


