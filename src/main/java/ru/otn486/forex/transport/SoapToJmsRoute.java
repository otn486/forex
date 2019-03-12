package ru.otn486.forex.transport;

import org.apache.camel.builder.RouteBuilder;
import ru.otn486.forex.filter.CurrencyFilter;

public class SoapToJmsRoute extends RouteBuilder {

    CurrencyFilter filter = new CurrencyFilter("USD", 0.01);

    public void configure() throws Exception {

        //Получаем данные на SOAP сервис
        //Разбиваем список на отдельные сообщения
        //Фильтруем по базовой валюте
        //Фильтруем сообщения где спрэд мемьше заданного значения
        //Отправляем в очередь JMS
        from("cxf:bean:soapEndpoint?dataFormat=PAYLOAD")
              .split(xpath("//currency"))
              .filter().method(filter, "isBaseCurrency")
              .filter().method(filter, "isSpreadLess")
              .to("jms:fromSoap?exchangePattern=InOnly");
    }
}
