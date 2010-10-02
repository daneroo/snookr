From this directory:

GetThermostat Details:
Magic params: <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
curl -i -X POST -H "Content-Type: text/xml; charset=utf-8" -H "soapaction: http://tempuri.org/ICCDRService/GetThermostatDetails" -d '@GetThermostatDetailsRequest.xml' "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc/basic"; echo

